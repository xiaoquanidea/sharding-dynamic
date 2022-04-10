package com.github.hutiquan.sharding.api

import java.lang.annotation.Inherited
import kotlin.reflect.KClass


@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Database(
    val value: String = ""
    )



