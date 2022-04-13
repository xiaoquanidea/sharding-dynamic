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