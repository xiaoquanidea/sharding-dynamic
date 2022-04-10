package com.github.hutiquan.sharding.core.strategy

import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource
import kotlin.math.abs

class LoadBalanceDataSourceStrategy : ShardingDataSourceDetermineStrategy {
    private val counter: AtomicInteger = AtomicInteger(0)

    override fun determineDataSource(dataSources: List<String>): String {
        val index = abs(counter.getAndIncrement()) % dataSources.size
        return dataSources[index]
    }
}