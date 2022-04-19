package com.github.hutiquan.sharding.xa

import com.github.hutiquan.sharding.api.DatabaseCluster
import com.github.hutiquan.sharding.api.ex.ShardingException
import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.bean.ShardingKey
import com.github.hutiquan.sharding.core.context.ShardingContext
import com.github.hutiquan.sharding.core.context.ShardingDataSource
import com.github.hutiquan.sharding.core.context.ShardingSourceContext
import org.apache.ibatis.mapping.SqlCommandType
import org.springframework.transaction.support.TransactionSynchronizationManager

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-11 5:17 PM
 */
open class XaShardingContext(
    properties: ShardingProperties,
    shardingDataSource: ShardingDataSource
) : ShardingContext(properties, shardingDataSource) {

    override fun smartChooseShardingKey(shardingKey: ShardingKey): String {
        val groupName = shardingKey.group
        /*
            没有事务，需要判断SqlCommand，是否走从库
            并且开了事务，则读写都走主库
         */
        if (shardingKey.onlyGroupName()) { // 说明只配置了组名,这个时候需要用路由策略选择数据源
            val sqlCommandType = ShardingSourceContext.CUR_SQL_COMMAND_TYPE.get()

            if (TransactionSynchronizationManager.isActualTransactionActive()) { // 如果有事务,读写都走主库
                val shardingGroup = this.healthShardingSources[groupName]
                    ?: throw ShardingException("${TransactionSynchronizationManager.getCurrentTransactionName()}事务正处于活跃状态,没有在${groupName}中找到可用的数据源")
                val chooseSharding = shardingGroup.chooseSharding(DatabaseCluster.MASTER)
                shardingKey.datasourceKey = chooseSharding
            } else { // 无事务
                val shardingGroup = this.healthShardingSources[groupName]
                    ?: throw ShardingException("没有在${groupName}中找到可用的数据源")

                if (sqlCommandType == null) {
                    shardingKey.datasourceKey = shardingGroup.chooseSharding(DatabaseCluster.MASTER)
                }else {
                    // 如果是读Command,则走从库
                    val chooseSharding = when (SqlCommandType.SELECT) { // TODO 如果是事务注解加载Controller上,会提前开启数据源,明明是读请求,到这里sqlCommand还没拿到
                            sqlCommandType -> shardingGroup.chooseSharding(DatabaseCluster.SLAVE)
                            else -> shardingGroup.chooseSharding(DatabaseCluster.MASTER)
                        }
                    shardingKey.datasourceKey = chooseSharding
                }

            }
        }

        return shardingKey.toString()
    }
}