package com.github.hutiquan.sharding.core.bean

import com.github.hutiquan.sharding.core.context.ShardingContext
import com.github.hutiquan.sharding.core.context.ShardingContext.Companion.SHARDING_KEY_SEPARATOR
import com.mysql.cj.util.StringUtils

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-11 5:34 PM
 */
class ShardingKey(
    var group: String?,
    var datasourceKey: String?,
) {
    constructor() : this(null, null)

    companion object {
        fun mapToShardingKey(shardingStr: String): ShardingKey {
            val shardingKey = ShardingKey()
            if (shardingStr.contains(ShardingContext.SHARDING_KEY_SEPARATOR)) {
                val shardingArr = shardingStr.split(ShardingContext.SHARDING_KEY_SEPARATOR)
                shardingKey.group = shardingArr[0]
                shardingKey.datasourceKey = shardingArr[1]
            } else {
                shardingKey.group = shardingStr
            }

            return shardingKey
        }
    }

    fun groupIsNull() : Boolean {
        return StringUtils.isNullOrEmpty(group)
    }

    fun datasourceKeyIsNull() : Boolean {
        return StringUtils.isNullOrEmpty(datasourceKey)
    }

    fun onlyGroupName() : Boolean {
        return !groupIsNull() && datasourceKeyIsNull()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShardingKey

        if (group != other.group) return false
        if (datasourceKey != other.datasourceKey) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        if (datasourceKey == null) {
            return group ?: ""
        }
        return ((group?.plus(SHARDING_KEY_SEPARATOR)) ?: "") + (datasourceKey ?: "")
    }

}
