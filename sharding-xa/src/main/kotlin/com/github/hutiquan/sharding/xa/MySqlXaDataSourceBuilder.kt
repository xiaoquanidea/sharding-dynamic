package com.github.hutiquan.sharding.xa;

import com.github.hutiquan.sharding.core.DataSourceProperty
import com.github.hutiquan.sharding.core.context.XaDataSourceCreator;
import com.github.hutiquan.sharding.core.context.DataSourceBuilder;
import com.github.hutiquan.sharding.core.context.ShardingContext.Companion.SHARDING_KEY_SEPARATOR
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean
import java.util.Optional;
import javax.sql.DataSource

class MySqlXaDataSourceBuilder(
    private val xaDataSourceOpt:Optional<XaDataSourceCreator>
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