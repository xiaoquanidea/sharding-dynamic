package com.github.hutiquan.sharding.starter

import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.annotation.ShardingAnnotationContainer
import com.github.hutiquan.sharding.core.aspectj.ShardingDatasourceInterceptor
import com.github.hutiquan.sharding.core.aspectj.ShardingDatasourcePointcutAdvisor
import com.github.hutiquan.sharding.core.context.*
import com.github.hutiquan.sharding.core.plugin.MybatisReadWriteAutoRoutingPlugin
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 5:05 PM
 */
@EnableAspectJAutoProxy(proxyTargetClass = false) // true--使用CGLIB基于类创建代理，false--使用java接口创建代理
@EnableConfigurationProperties(ShardingProperties::class)
@AutoConfigureBefore(DataSourceAutoConfiguration::class, XADataSourceAutoConfiguration::class)
class ShardingAutoConfiguration {

    @Bean
    fun dataSource(): ShardingDataSource {
        return ShardingDataSource()
    }

    @Bean
    @ConditionalOnMissingBean(name = ["shardingManagedTransactionFactory"])
    fun shardingContext(
        properties: ShardingProperties,
        shardingDataSource: ShardingDataSource
    ): ShardingContext {
        return DefaultShardingContext(properties, shardingDataSource)
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("org.springframework.boot.jta.atomikos.AtomikosDataSourceBean")
    fun dataSourceBuilder(): DataSourceBuilder<*> {
        return HikariDataSourceBuilder()
    }


    @Bean
    fun shardingAnnotationContainer() = ShardingAnnotationContainer()

    @Bean
    fun shardingDatasourceInterceptor(annoContainer: ShardingAnnotationContainer) =
        ShardingDatasourceInterceptor(annoContainer)

    @Bean
    fun shardingDatasourcePointcutAdvisor(
        annoContainer: ShardingAnnotationContainer,
        advice: ShardingDatasourceInterceptor
    ) =
        ShardingDatasourcePointcutAdvisor(annoContainer, advice)

    @Bean
    fun mybatisReadWriteAutoRoutingPlugin(): MybatisReadWriteAutoRoutingPlugin =
        MybatisReadWriteAutoRoutingPlugin()

}