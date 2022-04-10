package com.github.hutiquan.sharding.core.plugin

import com.github.hutiquan.sharding.core.context.ShardingSourceContext
import org.apache.ibatis.executor.Executor
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.plugin.*
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 11:21 AM
 */
@Intercepts(
    Signature(
        type = Executor::class,
        method = "query",
        args = [MappedStatement::class, Any::class, RowBounds::class, ResultHandler::class]
    ),
    Signature(
        type = Executor::class,
        method = "update",
        args = [MappedStatement::class, Any::class]
    )
)
class MybatisReadWriteAutoRoutingPlugin : Interceptor {
    @Throws(Throwable::class)
    override fun intercept(invocation: Invocation): Any {
        val args = invocation.args
        val ms = args[0] as MappedStatement
        return try {
            ShardingSourceContext.CUR_SQL_COMMAND_TYPE.set(ms.sqlCommandType)
            invocation.proceed()
        } finally {
            ShardingSourceContext.CUR_SQL_COMMAND_TYPE.remove()
        }
    }

    override fun plugin(target: Any?): Any {
        return (if (target is Executor) Plugin.wrap(target, this) else target)!!
    }

    override fun setProperties(properties: Properties?) {}
}