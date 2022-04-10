package com.github.hutiquan.sharding.core.strategy

import javax.sql.DataSource

interface ShardingDataSourceDetermineStrategy {

    fun determineDataSource(dataSources: List<String>): String

}