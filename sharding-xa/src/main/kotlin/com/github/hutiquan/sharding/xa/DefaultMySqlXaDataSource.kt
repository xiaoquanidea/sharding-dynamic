package com.github.hutiquan.sharding.xa

import com.github.hutiquan.sharding.core.DataSourceProperty
import com.github.hutiquan.sharding.core.context.XaDataSourceCreator
import com.mysql.cj.jdbc.MysqlXADataSource
import javax.sql.XADataSource

open class DefaultMySqlXaDataSource : XaDataSourceCreator {
    override fun createXaDataSource(
        groupKey: String,
        shardingKey: String,
        property: DataSourceProperty
    ): XADataSource {

        // mysql xa
        val mysqlXaDataSource = MysqlXADataSource()
        mysqlXaDataSource.characterEncoding
        mysqlXaDataSource.setUrl(property.url)
        mysqlXaDataSource.pinGlobalTxToPhysicalConnection = true
        mysqlXaDataSource.user = property.username
        mysqlXaDataSource.password = property.password
        mysqlXaDataSource.pinGlobalTxToPhysicalConnection = true
        return mysqlXaDataSource
    }
}