package com.github.hutiquan.sharding.core.interceptor

import org.springframework.aop.ClassFilter
import org.springframework.aop.MethodMatcher
import org.springframework.aop.Pointcut
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut
import org.springframework.util.Assert

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-08-11 1:34 AM
 */
class ShardingAnnotationMatchingPointcut : Pointcut {

    private val shardingAnnotationTypes: MutableSet<Class<out Annotation>> = mutableSetOf()
    private val checkInherited: Boolean = false


    private var classFilter: ClassFilter

    private var methodMatcher: MethodMatcher


    constructor(classAnnotationTypes: MutableSet<Class<out Annotation>>) : this(classAnnotationTypes, false)

    constructor(classAnnotationTypes: MutableSet<Class<out Annotation>>, checkInherited: Boolean) {
        this.classFilter = ShardingClassFilter(classAnnotationTypes, checkInherited)
        this.methodMatcher = MethodMatcher.TRUE
    }

    constructor(
        classAnnotationTypes: MutableSet<Class<out Annotation>>?,
        methodAnnotationTypes: MutableSet<Class<out Annotation>>?,
    ): this(classAnnotationTypes, methodAnnotationTypes, false)

    constructor(
        classAnnotationTypes: MutableSet<Class<out Annotation>>?,
        methodAnnotationTypes: MutableSet<Class<out Annotation>>?,
        checkInherited: Boolean
    ) {
        Assert.isTrue(
            !classAnnotationTypes.isNullOrEmpty() || !methodAnnotationTypes.isNullOrEmpty(),
            "Either Class annotation type or Method annotation type needs to be specified (or both)"
        )

        if (!classAnnotationTypes.isNullOrEmpty()) {
            this.classFilter = ShardingClassFilter(classAnnotationTypes, checkInherited)
        }else {
            this.classFilter = ClassFilter.TRUE
        }

        if (!methodAnnotationTypes.isNullOrEmpty()) {
            this.methodMatcher = ShardingMethodMatcher(methodAnnotationTypes, checkInherited)
        }else {
            this.methodMatcher = MethodMatcher.TRUE
        }

    }


    companion object {
        fun forClassAnnotation(classAnnotationTypes: MutableSet<Class<out Annotation>>?): ShardingAnnotationMatchingPointcut {
            Assert.isTrue(!classAnnotationTypes.isNullOrEmpty(), "Annotation type must not be null")
            return ShardingAnnotationMatchingPointcut(classAnnotationTypes!!)
        }

        fun forMethodAnnotation(methodAnnotationTypes: MutableSet<Class<out Annotation>>?): ShardingAnnotationMatchingPointcut {
            Assert.isTrue(!methodAnnotationTypes.isNullOrEmpty(), "Annotation type must not be null")

            return ShardingAnnotationMatchingPointcut(null, methodAnnotationTypes)
        }


    }

    override fun getClassFilter(): ClassFilter {
        return classFilter
    }

    override fun getMethodMatcher(): MethodMatcher {
        return methodMatcher
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShardingAnnotationMatchingPointcut

        if (classFilter != other.classFilter) return false
        if (methodMatcher != other.methodMatcher) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}