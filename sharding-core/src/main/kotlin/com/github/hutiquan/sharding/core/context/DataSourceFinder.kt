package com.github.hutiquan.sharding.core.context

import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 10:56 AM
 */
interface DataSourceFinder {

    fun smartChooseShardingKey() : String

    fun findCurrentDataSource() : DataSource
}