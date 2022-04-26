package com.github.hutiquan.sharding.core.annotation

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-26 11:04 AM
 */
@FunctionalInterface
interface ShardingAnnotationInjector {
    fun inject(): List<String>
}