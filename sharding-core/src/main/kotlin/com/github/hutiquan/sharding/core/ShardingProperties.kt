package com.github.hutiquan.sharding.core

import com.github.hutiquan.sharding.api.DatabaseCluster
import com.github.hutiquan.sharding.core.ShardingProperties.Companion.SHARDING_PROPERTIES_PREFIX
import com.github.hutiquan.sharding.core.strategy.LoadBalanceDataSourceStrategy
import com.github.hutiquan.sharding.core.strategy.ShardingDataSourceDetermineStrategy
import com.zaxxer.hikari.HikariConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.io.Serializable

@ConfigurationProperties(prefix = SHARDING_PROPERTIES_PREFIX)
class ShardingProperties {

    companion object {
        const val SHARDING_PROPERTIES_PREFIX = "spring.datasource.sharding"
    }

    /**
     * 主数据源
     */
    var primary: String? = null

    /**
     * 数据源组
     */
    var shardingGroup: Map<String, DataSourceContainer> = hashMapOf()

    /**
     * 是否打印banner
     */
    var printBanner: Boolean = true

}

class DataSourceContainer {
//    var mybatisReadWriteAutoRouting: Boolean = false

    /**
     * 组路由策略
     */
    var groupRouteStrategy: Class<out ShardingDataSourceDetermineStrategy> = LoadBalanceDataSourceStrategy::class.java

    /**
     * 数据源属性
     */
    var datasource: List<DataSourceProperty> = mutableListOf()
}




// https://www.iteye.com/blog/banshanxianren-2369961
class DataSourceProperty : Serializable {
    /**
     * 数据库厂商驱动
     */
    var driverClassName: String? = null

    /**
     * jdbc url
     */
    var url: String? = null

    /**
     * 数据库账号
     */
    var username: String? = null

    /**
     * 数据库密码
     */
    var password: String? = null

    /**
     * jndi
     */
    var jndiName: String? = null

    /**
     * schema
     */
    var schema: String? = null

    /**
     * 组内数据源key
     */
    lateinit var key: String

    /**
     * 是否主库
     */
    var cluster: DatabaseCluster? = null

    /**
     * 连接池中保留的最小连接数
     */
    var minPoolSize = 3

    /**
     * 连接池中保留的最大连接数
     */
    var maxPoolSize = 100

    /**
     * 连接最大空闲时间（秒）
     */
    var maxLifetime = 30_000

    /**
     * 获取连接失败重新获等待最大时间（秒），在这个时间内如果有可用连接，将返回
     */
    var borrowConnectionTimeout = 10_000

    /**
     * 登录超时时间（秒）
     */
    var loginTimeout = 30

    /**
     * atomikos
     * 设置池维护线程的维护间隔（连接回收），时间间隔以秒为单位。如果未设置或不为正，则将使用池的默认值（60秒）。
     */
    var maintenanceInterval = 60


    @Deprecated("https://www.atomikos.com/Blog/ExtremeTransactions6dot0")
    /**
     *
     * atomikos
     * 连接池在收回连接之前允许借用连接的时间（秒）
     * <br/>
     * 最大获取数据时间，如果不设置这个值，Atomikos使用默认的0（不超时）
     * <br/>
     * 如果设置为5分钟，那么在处理大批量数据读取的时候，一旦超过5分钟，就会抛出类似 Resultset is close 的错误
     */
    var reapTimeout = 0

    /**
     * 最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃
     */
    var maxIdleTime = 60_000

    /**
     * 连接保活
     */
    var testQuery: String? = "select 1"

    @NestedConfigurationProperty
    var hikari: HikariConfig? = null

    companion object {
        fun of(driverClassName: String, url: String, username: String, password: String): DataSourceProperty {
            val dataSourceProperty = DataSourceProperty()
            dataSourceProperty.driverClassName = driverClassName
            dataSourceProperty.url = url
            dataSourceProperty.username = username
            dataSourceProperty.password = password

            return dataSourceProperty
        }
    }
}
