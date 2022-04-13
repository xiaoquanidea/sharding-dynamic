package com.github.hutiquan.sharding.starter.conditional

import org.springframework.context.annotation.Conditional

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Conditional(OnMybatisPlusVersion::class)
annotation class ConditionalOnMyBatisPlusVersion(
    val value: String,
    val range: Range = Range.EQUAL_OR_NEWER
)

enum class Range {
    /**
     * 等于 或 高于 指定的version
     */
    EQUAL_OR_NEWER,

    /**
     * 低于指定的version
     */
    OLDER_THAN
}