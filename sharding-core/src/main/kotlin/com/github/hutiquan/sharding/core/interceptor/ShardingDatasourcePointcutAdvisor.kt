package com.github.hutiquan.sharding.core.interceptor

import com.github.hutiquan.sharding.core.annotation.ShardingAnnotationContainer
import org.aopalliance.aop.Advice
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractPointcutAdvisor
import org.springframework.aop.support.ComposablePointcut
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut

class ShardingDatasourcePointcutAdvisor(
    private val shardingAnnotationContainer: ShardingAnnotationContainer,
    private val advice: Advice,
) : AbstractPointcutAdvisor() {

    private var pointcut: Pointcut? = null


    override fun getAdvice(): Advice {
        return advice
    }

    override fun getPointcut(): Pointcut {
        if (pointcut == null) {
            pointcut = buildPointcut()
        }
        return this.pointcut!!
    }

    private fun buildPointcut(): Pointcut {

        val shardingAnnotationTypes = shardingAnnotationContainer.shardingAnnotationTypes

        val mpc = ShardingAnnotationMatchingPointcut.forMethodAnnotation(shardingAnnotationTypes)
        val cpc = ShardingAnnotationMatchingPointcut(shardingAnnotationTypes, true)


        // 直接new,默认匹配所有方法
        val composablePointcut = ComposablePointcut()
        composablePointcut.intersection(mpc) // 相交
            .union(cpc) // 并集
        return composablePointcut
    }


}


/*
fun main() {
    val container = ShardingAnnotationContainer()
    container.addShardingAnnotation(Sharding::class.jvmName)
    container.addShardingAnnotation(Database::class.jvmName)
    container.addShardingAnnotation(Source::class.jvmName)
    val advisor =
        ShardingDatasourcePointcutAdvisor(container, ShardingDatasourceInterceptor(container))
    val pointcut = advisor.pointcut

    val matches = pointcut.classFilter.matches(A::class.java)

    val method = A::printA.javaMethod
    val methodMatches = pointcut.methodMatcher.matches(method!!, A::class.java)
    println("pointcut = ${pointcut}")
}*/
