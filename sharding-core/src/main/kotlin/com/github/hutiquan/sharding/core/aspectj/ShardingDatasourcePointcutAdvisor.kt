package com.github.hutiquan.sharding.core.aspectj

import com.github.hutiquan.sharding.api.Database
import com.github.hutiquan.sharding.api.Sharding
import com.github.hutiquan.sharding.api.Source
import com.github.hutiquan.sharding.core.annotation.A
import com.github.hutiquan.sharding.core.annotation.ShardingAnnotationContainer
import org.aopalliance.aop.Advice
import org.springframework.aop.ClassFilter
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractPointcutAdvisor
import org.springframework.aop.support.AopUtils
import org.springframework.aop.support.ComposablePointcut
import org.springframework.aop.support.StaticMethodMatcher
import org.springframework.core.annotation.AnnotatedElementUtils
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmName

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

        val shardingMethodMatcher = ShardingMethodMatcher(shardingAnnotationTypes)
        val shardingClassFilter = ShardingClassFilter(shardingAnnotationTypes)

        // 直接new,默认匹配所有方法
        val composablePointcut: ComposablePointcut = ComposablePointcut()
        composablePointcut.intersection(shardingMethodMatcher) // 相交
            .union(shardingClassFilter) // 并集
        return composablePointcut
    }


}


data class ShardingMethodMatcher(
    val annotationTypes: MutableSet<Class<out Annotation>>,
    val checkInherited: Boolean = true
) : StaticMethodMatcher() {

    override fun matches(method: Method, targetClass: Class<*>): Boolean {
        if (matchesMethod(method)) {
            return true
        }

        // 代理类重新生成的字节码上没有注解
        if (Proxy.isProxyClass(targetClass)) {
            return false
        }

        // 该方法可能在接口上,所以也检查一下目标类
        val specificMethod = AopUtils.getMostSpecificMethod(method, targetClass)
        return specificMethod != method && matchesMethod(specificMethod)
    }

    private fun matchesMethod(method: Method): Boolean {
        return if (checkInherited)
            annotationTypes.any {
                AnnotatedElementUtils.hasAnnotation(method, it)
            }
        else
            annotationTypes.any {
                method.isAnnotationPresent(it)
            }

    }

}


data class ShardingClassFilter(
    val annotationTypes: MutableSet<Class<out Annotation>>,
    val checkInherited: Boolean = true
) : ClassFilter {

    override fun matches(clazz: Class<*>): Boolean {
        return filterClass(clazz)
    }

    private fun filterClass(clazz: Class<*>): Boolean {
        return if (checkInherited)
            annotationTypes.any {
                AnnotatedElementUtils.hasAnnotation(clazz, it)
            }
        else
            annotationTypes.any {
                clazz.isAnnotationPresent(it)
            }

    }
}


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
}