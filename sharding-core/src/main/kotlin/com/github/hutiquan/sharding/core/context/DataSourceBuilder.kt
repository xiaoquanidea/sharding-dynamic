/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-06 7:26 PM
 */
package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.core.DataSourceProperty
import javax.sql.DataSource

interface DataSourceBuilder<T : DataSource> {

    fun build(groupKey: String, shardingKey: String, property: DataSourceProperty): T
}