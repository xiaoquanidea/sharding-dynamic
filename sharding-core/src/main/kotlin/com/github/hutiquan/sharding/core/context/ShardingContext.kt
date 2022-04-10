package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.api.DatabaseCluster
import com.github.hutiquan.sharding.api.ex.ShardingException
import com.github.hutiquan.sharding.core.DataSourceProperty
import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.bean.ShardingGroup
import com.github.hutiquan.sharding.core.strategy.ShardingDataSourceDetermineStrategy
import org.apache.ibatis.mapping.SqlCommandType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-07 10:01 AM
 */


class ShardingContext(
    private val properties: ShardingProperties,
    private val shardingDataSource : ShardingDataSource
) : InitializingBean, BeanFactoryAware, DataSourceFinder {

    companion object {
        const val SHARDING_KEY_SEPARATOR = "."
        val log: Logger = LoggerFactory.getLogger(ShardingContext::class.java)
    }


    private val loaded: AtomicBoolean = AtomicBoolean(false)
    private lateinit var beanFactory: ConfigurableListableBeanFactory
    private lateinit var primaryShardingKey: String

    var shardingSources = ConcurrentHashMap<String, ShardingGroup>()
    val allShardingSources = mutableMapOf<String, ShardingGroup>()

    val healthShardingSources = ConcurrentHashMap<String, ShardingGroup>()

    override fun smartChooseShardingKey(): String {
        var shardingKey = ShardingSourceContext.get()
        if (shardingKey == null) {
            shardingKey = primaryShardingKey
        }

        var groupName: String = shardingKey
        var dataSourceKey: String = ""
        // 如果shardingKey不是'booking.bookingRead'样式，则认为值为shardingGroup
        if (shardingKey.contains(SHARDING_KEY_SEPARATOR)) {
            val shardingKeyArr = shardingKey.split(SHARDING_KEY_SEPARATOR)
            groupName = shardingKeyArr[0]
            dataSourceKey = shardingKeyArr[1]
        }

        /*
            如果配置了路由策略，没有事务，需要判断SqlCommand，是否走从库
            如果配置了路由策略，并且开了事务，则读写都走主库
         */
        if (dataSourceKey.isBlank()) { // 说明只配置了组名,这个时候需要用路由策略选择数据源
            val sqlCommandType = ShardingSourceContext.CUR_SQL_COMMAND_TYPE.get()
            val isRead = SqlCommandType.SELECT == sqlCommandType

            if (TransactionSynchronizationManager.isSynchronizationActive()) { // 如果有事务,读写都走主库
                val shardingGroup = healthShardingSources[groupName] ?: throw ShardingException("事务正处于活跃状态[${TransactionSynchronizationManager.getCurrentTransactionName()}],shardingGroup[${groupName}]没有找到可用的数据源")
                val chooseSharding = shardingGroup.chooseSharding(DatabaseCluster.MASTER)
                shardingKey = chooseSharding
            }else { // 无事务
                val shardingGroup = healthShardingSources[groupName] ?: throw ShardingException("shardingGroup[${groupName}]没有找到可用的数据源")

                // 如果是读Command,则走从库
                val chooseSharding = when {
                    isRead -> shardingGroup.chooseSharding(DatabaseCluster.SLAVE)
                    else -> shardingGroup.chooseSharding(DatabaseCluster.MASTER)
                } ?: shardingGroup.chooseSharding(DatabaseCluster.MASTER)
                shardingKey = chooseSharding
            }
        }
        return shardingKey
    }

    override fun findCurrentDataSource() : DataSource {
        val dataSource = shardingDataSource.storeDataSource[smartChooseShardingKey()]
        return dataSource!!
    }

    override fun afterPropertiesSet() {
        // 加载数据源
        loadShardingDataSource()
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
        this.primaryShardingKey = properties.primary ?: throw ShardingException("${ShardingProperties.SHARDING_PROPERTIES_PREFIX}.primary未配置")

        properties.shardingGroup.forEach { (groupKey, container) ->
            // 装配strategy
            val strategy = container.groupRouteStrategy.run {
                beanFactory.autowire(
                    this::class.java,
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
            shardingSources.putAll(allShardingSources)
        }
    }


    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as ConfigurableListableBeanFactory
    }
}