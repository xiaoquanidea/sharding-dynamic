package com.github.hutiquan.sharding.core.context

import org.apache.ibatis.mapping.SqlCommandType
import java.util.*

object ShardingSourceContext {

    /**
     * 模拟栈FIFO,例如abc三个方法,a调用b,b调用c,传统的设置到当前线程的做法无法满足,
     * 需要模拟栈 先进后出,后进先出
     * a -> b -> c -> b -> a
     */
    val CUR_SHARDING_KEY: ThreadLocal<Deque<String>>  = ThreadLocal.withInitial { java.util.ArrayDeque() }

    /**
     * 当前数据源
     */
    val CUR_SQL_COMMAND_TYPE: ThreadLocal<SqlCommandType?> =  ThreadLocal.withInitial{ null }

    /**
     * 获取
     */
    fun get() = CUR_SHARDING_KEY.get().peek()

    /**
     * 压栈
     */
    fun push(shardingKey: String) = CUR_SHARDING_KEY.get().push(shardingKey)

    /**
     * 弹栈
     */
    fun pop(): String {
        val shardingKeys = CUR_SHARDING_KEY.get()
        val shardingKey = shardingKeys.pop()
        if (shardingKeys.isEmpty()) {
            CUR_SHARDING_KEY.remove()
        }
        return shardingKey
    }

}