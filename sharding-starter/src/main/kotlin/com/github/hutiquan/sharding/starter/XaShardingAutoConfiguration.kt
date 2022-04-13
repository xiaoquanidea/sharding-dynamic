package com.github.hutiquan.sharding.starter

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
import com.github.hutiquan.sharding.core.ShardingProperties
import com.github.hutiquan.sharding.core.context.*
import com.github.hutiquan.sharding.starter.conditional.ConditionalOnMyBatisPlusVersion
import com.github.hutiquan.sharding.starter.conditional.Range
import com.github.hutiquan.sharding.xa.*
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory
import org.apache.ibatis.transaction.TransactionFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.RuntimeBeanNameReference
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.*
import javax.sql.DataSource
import javax.sql.XADataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 5:22 PM
 */
@ConditionalOnClass(AtomikosDataSourceBean::class, XADataSource::class)
@AutoConfigureBefore(ShardingAutoConfiguration::class)
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


    @ConditionalOnMyBatisPlusVersion(value = "3.4.0", Range.OLDER_THAN)
    class MybatisPlusInterposer : BeanPostProcessor, BeanFactoryAware {
        private lateinit var beanFactory: BeanFactory

        override fun setBeanFactory(beanFactory: BeanFactory) {
            this.beanFactory = beanFactory
        }

        /**
         * [com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean#545]
         */
        override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
            if (bean is DefaultSqlSessionFactory) {
                val transactionFactory = beanFactory.getBean(TransactionFactory::class.java)
                val mybatisOfficialEnv = bean.configuration.environment

                val shardingProxyEnv = Environment(
                    mybatisOfficialEnv.id,
                    transactionFactory,
                    mybatisOfficialEnv.dataSource
                )
                bean.configuration.environment = shardingProxyEnv
            }
            return super.postProcessBeforeInitialization(bean, beanName)
        }

/*        override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
            // 因Configuration类无setTransactionFactory方法，此方法行不通
            registry.getBeanDefinition("sqlSessionFactory").propertyValues.add(
                "configuration.environment.transactionFactory",
                RuntimeBeanNameReference("shardingManagedTransactionFactory")
            )

        }*/
    }
}