package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.core.DataSourceProperty
import javax.sql.XADataSource

interface XaDataSourceCreator {
    fun createXaDataSource(
        groupKey: String,
        shardingKey: String,
        property: DataSourceProperty
    ): XADataSource
}