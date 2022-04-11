package com.github.hutiquan.sharding.starter

import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.context.*
import com.github.hutiquan.sharding.xa.ShardingManagedTransactionFactory
import com.github.hutiquan.sharding.xa.ShardingTransaction
import com.github.hutiquan.sharding.xa.XaShardingContext
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.util.*
import javax.sql.DataSource
import javax.sql.XADataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 5:22 PM
 */
@ConditionalOnClass(AtomikosDataSourceBean::class, XADataSource::class)
@AutoConfigureAfter(ShardingAutoConfiguration::class)
class XaShardingAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    fun shardingContext(
        properties: ShardingProperties,
        shardingDataSource: ShardingDataSource
    ): ShardingContext {
        return XaShardingContext(properties, shardingDataSource)
    }

    @Primary
    @Bean("shardingManagedTransactionFactory")
    fun shardingManagedTransactionFactory() = ShardingManagedTransactionFactory()


    @Bean
    @ConditionalOnMissingBean
    fun xaDataSourceCreator() = DefaultMySqlXaDataSource()

    @Bean
    @ConditionalOnMissingBean
    fun mySqlXaDataSourceBuilder(xaDataSourceOpt: Optional<XaDataSourceCreator>): DataSourceBuilder<*> {
        return MySqlXaDataSourceBuilder(xaDataSourceOpt)
    }

}