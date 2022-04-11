package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.api.DatabaseCluster
import com.github.hutiquan.sharding.api.ex.ShardingException
import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.bean.ShardingGroup
import com.github.hutiquan.sharding.core.bean.ShardingKey
import com.github.hutiquan.sharding.core.bean.ShardingKey.Companion.mapToShardingKey
import com.github.hutiquan.sharding.core.bean.debugOutput
import com.github.hutiquan.sharding.core.strategy.ShardingDataSourceDetermineStrategy
import org.apache.ibatis.mapping.SqlCommandType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.*
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.jta.atomikos.AtomikosProperties
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import javax.sql.DataSource
import kotlin.properties.ReadOnlyProperty

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
    fun getDatabaseKeyOrElsePrimaryKey(): String {
        val shardingKey = ShardingSourceContext.get()?.apply { log.debugOutput("返回数据源${this}") }
            ?: return getDefaultMasterSharding()
        return shardingKey
    }

    @kotlin.jvm.Throws(ShardingException::class)
    protected fun getDefaultMasterSharding(): String {
        return healthShardingSources[primarySharding]?.chooseSharding(DatabaseCluster.MASTER)
            ?.run {
                val key = primarySharding + SHARDING_KEY_SEPARATOR + this
                log.debugOutput("返回默认主数据源${key}")
                key
            }
            ?: throw ShardingException("未找到${primarySharding}组主数据源,请重新确认配置文件")
    }


    override fun chooseShardingKey(): String {
        // 如果没有加@Sharding注解,此处get会为null
        val shardingValue = ShardingSourceContext.get() ?: return getDefaultMasterSharding()
        val shardingKey = mapToShardingKey(shardingValue)
        return smartChooseShardingKey(shardingKey)
    }

    abstract fun smartChooseShardingKey(shardingKey: ShardingKey): String

    /**
     * 智能路由数据源，考虑读写分离
     */
//    override fun smartChooseShardingKey(): String {
//        // 如果没有加@Sharding注解,此处get会为null
//        var shardingKey = ShardingSourceContext.get() ?: return getDefaultMasterSharding()
//
//        var groupName: String = ""
//        var dataSourceKey: String = ""
//
//        // 如果shardingKey中没有用.分割，则将其作为sharding组名
//        if (shardingKey.contains(SHARDING_KEY_SEPARATOR)) {
//            val shardingKeyArr = shardingKey.split(SHARDING_KEY_SEPARATOR)
//            groupName = shardingKeyArr[0]
//            dataSourceKey = shardingKeyArr[1]
//        } else {
//            groupName = shardingKey
//        }
//
//        /*
//            什么时候能走到这里？开启了事务，也配置了@Sharding注解
//         */
//        if (!xaTxEnable) {
//            if (dataSourceKey.isBlank()) { // 只配置了组名
//                val shardingGroup = healthShardingSources[groupName] ?: throw ShardingException("没有在${groupName}中找到可用的数据源")
//                if (TransactionSynchronizationManager.isSynchronizationActive()) { // 如果事务管理器处于活跃状态,则取出主数据源
//                    shardingGroup
//                }
//            }
//        }
//
//        /*
//            没有事务，需要判断SqlCommand，是否走从库
//            并且开了事务，则读写都走主库
//         */
//        if (groupName.isNotBlank() && dataSourceKey.isBlank()) { // 说明只配置了组名,这个时候需要用路由策略选择数据源
//            val sqlCommandType = ShardingSourceContext.CUR_SQL_COMMAND_TYPE.get()
//            val isRead = SqlCommandType.SELECT == sqlCommandType
//
//            // TODO 如果是因为开事务而进的该方法，事务管理器还没来及初始化，所以明明有事务，这里却也是false================
//            shardingKey = if (TransactionSynchronizationManager.isActualTransactionActive()) { // 如果有事务,读写都走主库
//                val shardingGroup = this.healthShardingSources[groupName] ?: throw ShardingException("${TransactionSynchronizationManager.getCurrentTransactionName()}事务正处于活跃状态,没有在${groupName}中找到可用的数据源")
//                val chooseSharding = shardingGroup.chooseSharding(DatabaseCluster.MASTER)
//                groupName + SHARDING_KEY_SEPARATOR + chooseSharding
//            }else { // 无事务
//                val shardingGroup = this.healthShardingSources[groupName] ?: throw ShardingException("没有在${groupName}中找到可用的数据源")
//
//                // 如果是读Command,则走从库
//                val chooseSharding = when { // TODO 如果是事务注解加载Controller上,会提前开启数据源,明明是读请求,到这里sqlCommand还没拿到
//                    isRead -> shardingGroup.chooseSharding(DatabaseCluster.SLAVE)
//                    else -> shardingGroup.chooseSharding(DatabaseCluster.MASTER)
//                } ?: shardingGroup.chooseSharding(DatabaseCluster.MASTER)
//                groupName + SHARDING_KEY_SEPARATOR + chooseSharding
//            }
//        }
//        return shardingKey
//    }


    override fun findCurrentDataSource() : DataSource {
        val dataSource = shardingDataSource.storeDataSource[chooseShardingKey()]
        return dataSource!!
    }

    override fun afterPropertiesSet() {
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

fun main() {
    val a : Int? = null
    val result = a.run {
        1
    } ?: throw RuntimeException("空")

    println("result = ${result}")
}