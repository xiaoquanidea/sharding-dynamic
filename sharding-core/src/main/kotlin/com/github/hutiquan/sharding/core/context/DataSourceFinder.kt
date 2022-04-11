package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.core.bean.ShardingKey
import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 10:56 AM
 */
interface DataSourceFinder {

    fun chooseShardingKey() : String

    fun findCurrentDataSource() : DataSource
}