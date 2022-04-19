package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.core.DataSourceProperty
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

open class HikariDataSourceBuilder : DataSourceBuilder<HikariDataSource> {
    override fun build(
        groupKey: String,
        shardingKey: String,
        property: DataSourceProperty
    ): HikariDataSource {

        val config = property.hikari
            ?: HikariConfig().apply {
                this.minimumIdle = property.minPoolSize
                this.maximumPoolSize = property.maxPoolSize
                this.maxLifetime = property.maxLifetime.toLong()
                this.validationTimeout = property.borrowConnectionTimeout.toLong()
                this.connectionTimeout = 60_000 // 连接超时默认为60秒
                this.idleTimeout = property.maxIdleTime.toLong()
            }

        config.username = property.username
        config.password = property.password
        config.jdbcUrl = property.url
        config.driverClassName = property.driverClassName
        config.poolName = "HikariCP[$groupKey.$shardingKey]"

        config.dataSourceJNDI = property.jndiName
        config.schema = property.schema

        val dataSource = HikariDataSource(config)
        return dataSource
    }
}