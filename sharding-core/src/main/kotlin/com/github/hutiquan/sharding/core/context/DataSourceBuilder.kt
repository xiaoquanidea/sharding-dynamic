/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-06 7:26 PM
 */
package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.core.DataSourceProperty
import com.github.hutiquan.sharding.core.context.ShardingContext.Companion.SHARDING_KEY_SEPARATOR
import com.mysql.cj.jdbc.MysqlXADataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean
import sun.misc.Unsafe
import java.util.*
import javax.sql.DataSource
import javax.sql.XADataSource

interface DataSourceBuilder<T : DataSource> {

    fun build(groupKey: String, shardingKey: String, property: DataSourceProperty): T
}


class HikariDataSourceBuilder : DataSourceBuilder<HikariDataSource> {
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


interface XaDataSourceCreator {
    fun createXaDataSource(
        groupKey: String,
        shardingKey: String,
        property: DataSourceProperty
    ): XADataSource
}

class DefaultMySqlXaDataSource : XaDataSourceCreator {
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

class MySqlXaDataSourceBuilder(
    private val xaDataSourceOpt: Optional<XaDataSourceCreator>
) : DataSourceBuilder<DataSource> {
    override fun build(
        groupKey: String,
        shardingKey: String,
        property: DataSourceProperty
    ): DataSource {
        val atomikosDataSourceBean = AtomikosDataSourceBean()
        atomikosDataSourceBean.xaDataSource =
            xaDataSourceOpt.get()?.createXaDataSource(groupKey, shardingKey, property)

        atomikosDataSourceBean.uniqueResourceName = groupKey + SHARDING_KEY_SEPARATOR + property.key
        atomikosDataSourceBean.minPoolSize = property.minPoolSize
        atomikosDataSourceBean.maxPoolSize = property.maxPoolSize
        atomikosDataSourceBean.maxLifetime = property.maxLifetime
        atomikosDataSourceBean.borrowConnectionTimeout = property.borrowConnectionTimeout
        atomikosDataSourceBean.loginTimeout = property.loginTimeout
        atomikosDataSourceBean.maintenanceInterval = property.maintenanceInterval
        atomikosDataSourceBean.reapTimeout = property.reapTimeout
        atomikosDataSourceBean.maxIdleTime = property.maxIdleTime
        atomikosDataSourceBean.testQuery = property.testQuery

        return atomikosDataSourceBean

    }
}