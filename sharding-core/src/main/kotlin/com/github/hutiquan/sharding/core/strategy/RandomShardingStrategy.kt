package com.github.hutiquan.sharding.core.strategy

import java.util.concurrent.ThreadLocalRandom
import javax.sql.DataSource

class RandomShardingStrategy : ShardingDataSourceDetermineStrategy {
    override fun determineDataSource(dataSources: List<String>): String {

        return dataSources[ThreadLocalRandom.current().nextInt(dataSources.size)]
    }
}