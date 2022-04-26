package com.github.hutiquan.sharding.core.strategy

interface ShardingDataSourceDetermineStrategy {

    fun determineDataSource(dataSources: List<String>): String

}