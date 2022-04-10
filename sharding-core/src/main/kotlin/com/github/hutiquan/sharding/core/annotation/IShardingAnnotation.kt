package com.github.hutiquan.sharding.core.annotation

@FunctionalInterface
interface IShardingAnnotation {

    fun provideAnno(): String
}