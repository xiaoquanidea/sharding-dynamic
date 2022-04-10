package com.github.hutiquan.sharding.xa

import com.github.hutiquan.sharding.core.context.ShardingContext
import com.github.hutiquan.sharding.core.context.ShardingDataSource
import org.mybatis.spring.transaction.SpringManagedTransaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DataSourceUtils
import java.sql.Connection
import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 10:31 AM
 */
class ShardingTransaction(
    private var dataSource: DataSource
) : SpringManagedTransaction(dataSource) {

    companion object {
        val log : Logger = LoggerFactory.getLogger(ShardingTransaction::class.java)
    }

    private var isConnectionTransactional = false
    private var autoCommit = false
    private var connection: Connection? = null

    private var connectionMap: MutableMap<String, Connection> = mutableMapOf()
    private var shardingContext: ShardingContext
    private var curShardingKey: String

    init {
        val shardingDataSource = dataSource as ShardingDataSource
        shardingContext = shardingDataSource.shardingContext
        curShardingKey = shardingContext.smartChooseShardingKey()
    }

    override fun getConnection(): Connection {
        val shardingKey = shardingContext.smartChooseShardingKey()
        if (shardingKey == curShardingKey) {
            return connection ?: openCurrentConnection()
        }else {
            var connection = connectionMap[shardingKey]
            if (connection == null || connection.isClosed) {
                connection = dataSource.connection
                connectionMap[shardingKey] = connection
            }
            return connection!!
        }
    }

    private fun openCurrentConnection(): Connection {
        connection = DataSourceUtils.getConnection(dataSource)

        this.autoCommit = connection!!.autoCommit
        this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.connection!!, this.dataSource)
        if (log.isDebugEnabled) {
            log.debug("this.autoCommit[$autoCommit], this.isConnectionTransactional[$isConnectionTransactional]")
        }
        return connection!!
    }

    override fun commit() {
        super.commit()
    }

    override fun rollback() {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            log.debug("Rolling back JDBC Connection [${this.connection}]")
            this.connection!!.rollback()
            connectionMap.values.forEach {
                it.rollback()
            }
        }
    }

    override fun close() {
        DataSourceUtils.releaseConnection(this.connection, this.dataSource)

        connectionMap.values.forEach {
            DataSourceUtils.releaseConnection(it, this.dataSource)
        }
    }

    override fun getTimeout(): Int? {
        return null
    }
}
