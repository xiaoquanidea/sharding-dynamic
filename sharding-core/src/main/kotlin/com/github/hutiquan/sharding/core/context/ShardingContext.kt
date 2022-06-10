package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.api.DatabaseCluster
import com.github.hutiquan.sharding.api.ex.ShardingException
import com.github.hutiquan.sharding.core.ShardingDynamicBanner
import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.bean.*
import com.github.hutiquan.sharding.core.bean.ShardingKey.Companion.mapToShardingKey
import com.github.hutiquan.sharding.core.strategy.ShardingDataSourceDetermineStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.*
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-07 10:01 AM
 */

abstract class ShardingContext(
    private val properties: ShardingProperties,
    private val shardingDataSource : ShardingDataSource
) : InitializingBean, BeanFactoryAware, DataSourceFinder {

    companion object {
        const val SHARDING_KEY_SEPARATOR = "."
        val log: Logger = LoggerFactory.getLogger(ShardingContext::class.java)
    }


    protected var xaTxEnable: Boolean = false

    private val loaded: AtomicBoolean = AtomicBoolean(false)
    protected lateinit var beanFactory: ConfigurableListableBeanFactory
    protected lateinit var primarySharding: String

    val allShardingSources = mutableMapOf<String, ShardingGroup>()
    var healthShardingSources = ConcurrentHashMap<String, ShardingGroup>()




    /**
     * 获取当前数据源key，如果没有找到，则获取属性文件中配置的主数据源
     */
    override fun chooseShardingKey(): String {
        // 如果没有加@Sharding注解,此处get会为null
        val shardingValue = ShardingSourceContext.get() ?: return getDefaultMasterSharding().apply {
            log.debugOutput("没有检测到注解,返回默认主数据源${this}")
        }
        val shardingKey = mapToShardingKey(shardingValue)
        if (shardingKey.groupIsNull()) {
            throw ShardingException("${shardingKey.group}组数据源不存在,请重新确认配置文件")
        }
        val smartChooseShardingKey = smartChooseShardingKey(shardingKey)
        log.traceOutput("返回数据源${smartChooseShardingKey}")
        return smartChooseShardingKey
    }

    @kotlin.jvm.Throws(ShardingException::class)
    protected fun getDefaultMasterSharding(): String {
        return healthShardingSources[primarySharding]?.masters?.get(0)
            ?.run {
                val key = primarySharding + SHARDING_KEY_SEPARATOR + this
                log.traceOutput("返回默认主数据源${key}")
                key
            }
            ?: throw ShardingException("未找到${primarySharding}组主数据源,请重新确认配置文件")
    }


    /**
     * 智能路由数据源，考虑读写分离
     */
    abstract fun smartChooseShardingKey(shardingKey: ShardingKey): String

    override fun findCurrentDataSource() : DataSource {
        val key = chooseShardingKey()
        val dataSource = shardingDataSource.storeDataSource[key] ?: throw ShardingException("没有找到${key}数据源")
        log.infoOutput("返回数据源${key}")
        return dataSource
    }

    override fun afterPropertiesSet() {
        // 开始print banner
        if (properties.printBanner) {
            log.info(ShardingDynamicBanner.banner)
        }
        // 加载数据源
        loadShardingDataSource()

        // 通过判断sharding-xa模块的bean是否有注入，来判定有xa支持
        this.xaTxEnable = beanFactory.containsBean("shardingManagedTransactionFactory")
    }


    private fun loadShardingDataSource() {
        val preVal = loaded.getAndSet(true)
        if (preVal) return
        doLoadShardingDataSource()
    }

    private fun doLoadShardingDataSource() {
        val dataSourceBuilder = BeanFactoryUtils.beanOfType(
            beanFactory as ListableBeanFactory,
            DataSourceBuilder::class.java
        )

        // 如果primaryShardingKey没有配置的话,抛出异常,中断上下文启动
        this.primarySharding = properties.primary ?: throw ShardingException("${ShardingProperties.SHARDING_PROPERTIES_PREFIX}.primary未配置")

        properties.shardingGroup.forEach { (groupKey, container) ->
            // 装配strategy
            val strategy = container.groupRouteStrategy.run {
                beanFactory.autowire(
                    this,
                    AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,
                    true
                ) as? ShardingDataSourceDetermineStrategy
            }

            val shardingGroup = ShardingGroup(groupKey, strategy)

            container.datasource.forEach {
                val dataSourceKey = it.key
                when (it.cluster) {
                    DatabaseCluster.MASTER -> shardingGroup.masters.add(dataSourceKey)
                    DatabaseCluster.SLAVE -> shardingGroup.slaves.add(dataSourceKey)
                    else -> throw ShardingException("shardingKey -> $groupKey.$dataSourceKey cluster 不能为空")
                }

                val dataSource = dataSourceBuilder.build(groupKey, dataSourceKey, it)
                shardingDataSource.storeDataSource["$groupKey$SHARDING_KEY_SEPARATOR$dataSourceKey"] = dataSource

            }

            allShardingSources[groupKey] = shardingGroup

        }

        if (allShardingSources.isNotEmpty()) {
            this.healthShardingSources.putAll(allShardingSources)
        }
    }


    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as ConfigurableListableBeanFactory
    }
}

/*
fun main() {
    val a : Int? = null
    val result = a.run {
        1
    } ?: throw RuntimeException("空")

    println("result = ${result}")
}*/
