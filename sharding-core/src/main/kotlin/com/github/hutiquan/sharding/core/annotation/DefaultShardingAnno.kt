package com.github.hutiquan.sharding.core.annotation

import com.github.hutiquan.sharding.api.Sharding
import kotlin.reflect.jvm.jvmName

class DefaultShardingAnno: IShardingAnnotation {
    override fun provideAnno(): String {
        return Sharding::class.jvmName
    }
}