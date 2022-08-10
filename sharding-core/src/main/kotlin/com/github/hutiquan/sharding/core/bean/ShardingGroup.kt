package com.github.hutiquan.sharding.core.bean;

import com.github.hutiquan.sharding.api.DatabaseCluster
import com.github.hutiquan.sharding.core.strategy.LoadBalanceDataSourceStrategy
import com.github.hutiquan.sharding.core.strategy.ShardingDataSourceDetermineStrategy
import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-06 5:46 PM
 */
class ShardingGroup(
    val groupName: String,
    val strategy: ShardingDataSourceDetermineStrategy?
) {

    val masters: MutableList<String> = mutableListOf()
    val slaves: MutableList<String> = mutableListOf()


    fun chooseMasterFirst(): String {
        return masters[0]
    }

    /**
     * 当没有配置slave数据源时,从masters数据源中返回
     */
    fun chooseSharding(cluster: DatabaseCluster): String? {
        return when(cluster) {
            DatabaseCluster.MASTER -> strategy?.determineDataSource(masters)
            DatabaseCluster.SLAVE -> {
                if (slaves.isEmpty()) {
                    return strategy?.determineDataSource(masters)
                }
                strategy?.determineDataSource(slaves)
            }
        }

    }

}

fun main() {
    val shardingGroup = ShardingGroup("haha", LoadBalanceDataSourceStrategy())
    shardingGroup.masters.add("haha")
//    shardingGroup.slaves.add("haha32532")
    val chooseSharding = shardingGroup.chooseSharding(DatabaseCluster.SLAVE)
    println("chooseSharding = ${chooseSharding}")
}
