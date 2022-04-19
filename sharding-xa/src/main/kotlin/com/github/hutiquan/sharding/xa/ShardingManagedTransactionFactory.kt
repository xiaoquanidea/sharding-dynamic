package com.github.hutiquan.sharding.xa

import com.github.hutiquan.sharding.api.ex.ShardingException
import org.apache.ibatis.session.TransactionIsolationLevel
import org.apache.ibatis.transaction.Transaction
import org.apache.ibatis.transaction.TransactionFactory
import org.mybatis.spring.transaction.SpringManagedTransactionFactory
import java.sql.Connection
import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 10:35 AM
 */
open class ShardingManagedTransactionFactory : SpringManagedTransactionFactory() {

    override fun newTransaction(
        dataSource: DataSource,
        level: TransactionIsolationLevel?,
        autoCommit: Boolean
    ): Transaction {
        return ShardingTransaction(dataSource)
    }
}