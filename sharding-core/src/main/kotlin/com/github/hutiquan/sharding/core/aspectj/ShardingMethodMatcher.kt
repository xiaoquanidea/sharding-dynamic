package com.github.hutiquan.sharding.core.aspectj

import org.springframework.aop.support.AopUtils
import org.springframework.aop.support.StaticMethodMatcher
import org.springframework.core.annotation.AnnotatedElementUtils
import java.lang.reflect.Method
import java.lang.reflect.Proxy

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