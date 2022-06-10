package com.github.hutiquan.sharding.core.bean;

import com.github.hutiquan.sharding.api.DatabaseCluster
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

    fun chooseSharding(cluster: DatabaseCluster): String? {
        return when(cluster) {
            DatabaseCluster.MASTER -> strategy?.determineDataSource(masters)
            DatabaseCluster.SLAVE -> strategy?.determineDataSource(slaves)
        }

    }

}
