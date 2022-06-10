package com.github.hutiquan.sharding.starter.conditional;

import com.baomidou.mybatisplus.core.MybatisPlusVersion
import com.sun.corba.se.impl.orbutil.ORBUtility.compareVersion
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

}


fun main() {
//    val l = "1.2.12"
//    val r = "1.2.3"
//
//    val compareVersion = compareVersion(l, r)
//    println("compareVersion = ${compareVersion}")
}