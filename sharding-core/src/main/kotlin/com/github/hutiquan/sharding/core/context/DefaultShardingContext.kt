package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.api.DatabaseCluster
import com.github.hutiquan.sharding.api.ex.ShardingException
import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.bean.ShardingKey
import org.apache.ibatis.mapping.SqlCommandType
import org.springframework.transaction.support.TransactionSynchronizationManager

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-11 5:18 PM
 */
class DefaultShardingContext(
    properties: ShardingProperties,
    shardingDataSource: ShardingDataSource
) : ShardingContext(properties, shardingDataSource) {


    override fun smartChooseShardingKey(shardingKey: ShardingKey): String {
        if (!shardingKey.groupIsNull() && !shardingKey.datasourceKeyIsNull()) {
            return shardingKey.toString()
        }

        // 只配置了组名
        val groupName = shardingKey.group
        val shardingGroup = healthShardingSources[groupName] ?: throw ShardingException("没有在${groupName}组中找到可用的数据源")

        // 开启了事务,只能选择主数据源
        if (TransactionSynchronizationManager.isSynchronizationActive()) { // 如果事务管理器处于活跃状态,则取出主数据源
            val chooseSharding = shardingGroup.chooseSharding(DatabaseCluster.MASTER)
            shardingKey.datasourceKey = chooseSharding
        }else {
            val sqlCommandType = ShardingSourceContext.CUR_SQL_COMMAND_TYPE.get()
            // 如果是事务注解加载Controller上,会提前开启数据源,明明是读请求,到这里sqlCommand还没拿到
            if (sqlCommandType == null) {
                shardingKey.datasourceKey = shardingGroup.chooseSharding(DatabaseCluster.MASTER)
            }else{
                val chooseSharding = when (SqlCommandType.SELECT) {
                    sqlCommandType -> shardingGroup.chooseSharding(DatabaseCluster.SLAVE)
                    else -> shardingGroup.chooseSharding(DatabaseCluster.MASTER)
                }
                shardingKey.datasourceKey = chooseSharding
            }

        }
        return shardingKey.toString()
    }
}