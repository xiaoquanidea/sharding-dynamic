package com.github.hutiquan.sharding.core.interceptor

import com.github.hutiquan.sharding.core.annotation.ShardingAnnotationContainer
import com.github.hutiquan.sharding.core.context.ShardingClassResolver
import com.github.hutiquan.sharding.core.context.ShardingSourceContext
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.springframework.core.Ordered
import org.springframework.core.annotation.AnnotatedElementUtils

class ShardingDatasourceInterceptor(
    private val shardingAnnoContainer: ShardingAnnotationContainer
) : MethodInterceptor, Ordered {
    override fun invoke(invocation: MethodInvocation): Any? {


        try {
            before(invocation)
            val result = invocation.proceed()
            return result
        } finally {
            after()
        }
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }


    private fun before(invocation: MethodInvocation) {
        val method = invocation.method
        val targetClass: Class<*> = ShardingClassResolver.targetClass(invocation)

        var shardingKey: String? = null
        val shardingAnnotationFromMethod = shardingAnnoContainer.findShardingAnnotation(method)
        if (shardingAnnotationFromMethod != null) {
            shardingKey = shardingAnnoContainer.getShardingAnnoValue(shardingAnnotationFromMethod)
        } else {
            shardingAnnoContainer.shardingAnnotationTypes.filter {
                AnnotatedElementUtils.findMergedAnnotation(targetClass, it) != null
            }.map { annoClazz ->
                AnnotatedElementUtils.findMergedAnnotationAttributes(
                    targetClass,
                    annoClazz,
                    false,
                    false
                )
            }.first()?.apply { shardingKey = shardingAnnoContainer.getShardingAnnoValue(this) }

        }

        shardingKey?.apply {
            ShardingSourceContext.push(this)
        }
    }

    private fun after() {
        ShardingSourceContext.pop()
    }
}