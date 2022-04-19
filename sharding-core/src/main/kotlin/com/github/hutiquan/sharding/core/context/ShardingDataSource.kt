package com.github.hutiquan.sharding.core.context

import com.github.hutiquan.sharding.core.ShardingProperties
import org.springframework.aop.aspectj.InstantiationModelAwarePointcutAdvisor
import org.springframework.beans.factory.*
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.jdbc.datasource.AbstractDataSource
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-07 8:35 PM
 */
abstract class AbstractRoutingDataSource : AbstractDataSource() {

    abstract fun determineTargetDataSource(): DataSource


    @Suppress("UNCHECKED_CAST", "WRONG_NULLABILITY_FOR_JAVA_OVERRIDE")
    @Throws(SQLException::class)
    override fun <T> unwrap(iface: Class<T>): T {
        if (iface.isInstance(this)) {
            return this as T
        }

        return determineTargetDataSource().unwrap(iface)
    }

    @Throws(SQLException::class)
    override fun isWrapperFor(iface: Class<*>): Boolean {
        if (iface.isInstance(this)) {
            return true
        }
        return determineTargetDataSource().isWrapperFor(iface)
    }

    @Throws(SQLException::class)
    override fun getConnection(): Connection = determineTargetDataSource().connection

    @Throws(SQLException::class)
    override fun getConnection(username: String, password: String): Connection =
        determineTargetDataSource().getConnection(username, password)
}


open class ShardingDataSource : AbstractRoutingDataSource(), SmartInitializingSingleton, BeanFactoryAware {

    val shardingContext : ShardingContext by lazy {
        beanFactory.getBean(ShardingContext::class.java)
    }
    private lateinit var beanFactory: BeanFactory

    /**
     * 实例化的真实数据源集合
     */
    val storeDataSource : MutableMap<String, DataSource> = hashMapOf()

    /**
     * 查找选择数据源
     */
    override fun determineTargetDataSource(): DataSource {
        return shardingContext.findCurrentDataSource()
    }



    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun afterSingletonsInstantiated() {

    }

}