package com.github.hutiquan.sharding.starter.conditional;

import com.baomidou.mybatisplus.core.MybatisPlusVersion
import org.springframework.boot.autoconfigure.condition.ConditionMessage
import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-12 6:00 PM
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
class OnMybatisPlusVersion : SpringBootCondition() {

    override fun getMatchOutcome(
        context: ConditionContext,
        metadata: AnnotatedTypeMetadata
    ): ConditionOutcome {

        val attributes = metadata.getAnnotationAttributes(
            ConditionalOnMyBatisPlusVersion::class.java.name
        )!!
        val range = attributes["range"] as Range
        val version = attributes["value"] as String

        return doMatchOutcome(range, version)
    }

    private fun doMatchOutcome(range: Range, version: String): ConditionOutcome {
        val mybatisPlusVersion = MybatisPlusVersion.getVersion()

        // 比较两个版本字符串。如果 v1 大于、等于或小于 v2，则返回 1、0 或 - 1。
        val compareResult = compareVersion(mybatisPlusVersion, version)

        val realRange = when {
            // 如果mp version比注解声明的要低
            compareResult < 0 -> Range.OLDER_THAN
            else -> Range.EQUAL_OR_NEWER
        }

        val match = realRange == range

        val expected = "当前Mybatis Plus版本号${mybatisPlusVersion}, 版本号相等或者更新"
        val message =
            ConditionMessage.forCondition(ConditionalOnMyBatisPlusVersion::class.java, expected)
                .foundExactly(version)

        return ConditionOutcome(match, message)
    }

    /** Compare two version arrays.
     * Return 1, 0 or -1 if v1 is greater than, equal to, or less than v2.
     */
    fun compareVersion(v1: IntArray?, v2: IntArray?): Int {
        var v1 = v1
        var v2 = v2
        if (v1 == null) v1 = IntArray(0)
        if (v2 == null) v2 = IntArray(0)
        for (i in v1.indices) {
            if (i >= v2.size || v1[i] > v2[i]) //v1 is longer or greater than v2
                return 1
            if (v1[i] < v2[i]) return -1
        }
        return if (v1.size == v2.size) 0 else -1
    }

    /** Compare two version strings.
     * Return 1, 0 or -1 if v1 is greater than, equal to, or less than v2.
     */
    @Synchronized
    fun compareVersion(v1: String?, v2: String?): Int {
        return compareVersion(
            parseVersion(v1),
            parseVersion(v2)
        )
    }

    /** Parse a version string such as "1.1.6" or "jdk1.2fcs" into
     * a version array of integers {1, 1, 6} or {1, 2}.
     * A string of "n." or "n..m" is equivalent to "n.0" or "n.0.m" respectively.
     */
    fun parseVersion(version: String?): IntArray {
        if (version == null) return IntArray(0)
        val s = version.toCharArray()
        //find the maximum span of the string "n.n.n..." where n is an integer
        var start = 0
        while (start < s.size && (s[start] < '0' || s[start] > '9')) {
            if (start == s.size) //no digit found
                return IntArray(0)
            ++start
        }
        var end = start + 1
        var size = 1
        while (end < s.size) {
            if (s[end] == '.') ++size else if (s[end] < '0' || s[end] > '9') break
            ++end
        }
        val `val` = IntArray(size)
        for (i in 0 until size) {
            var dot = version.indexOf('.', start)
            if (dot == -1 || dot > end) dot = end
            if (start >= dot) //cases like "n." or "n..m"
                `val`[i] = 0 //convert equivalent to "n.0" or "n.0.m"
            else `val`[i] = version.substring(start, dot).toInt()
            start = dot + 1
        }
        return `val`
    }

}


fun main() {
//    val l = "1.2.12"
//    val r = "1.2.3"
//
//    val compareVersion = compareVersion(l, r)
//    println("compareVersion = ${compareVersion}")
}