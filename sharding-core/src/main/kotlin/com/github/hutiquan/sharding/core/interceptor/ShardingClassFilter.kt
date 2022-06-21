package com.github.hutiquan.sharding.core.interceptor

import org.springframework.aop.ClassFilter
import org.springframework.core.annotation.AnnotatedElementUtils

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