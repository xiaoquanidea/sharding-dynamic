package com.github.hutiquan.sharding.core.bean

import org.slf4j.Logger

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-11 3:55 PM
 */

/**
 * trace级别输出
 */
fun Logger.traceOutput(format: String, vararg arguments: Any) {
    if (isTraceEnabled) {
        this.trace(format, *arguments)
    }
}


/**
 * debug级别输出
 */
fun Logger.debugOutput(format: String, vararg arguments: Any) {
    if (isDebugEnabled) {
        this.debug(format, *arguments)
    }
}

/**
 * info级别输出
 */
fun Logger.infoOutput(format: String, vararg arguments: Any) {
    if (isInfoEnabled) {
        this.info(format, *arguments)
    }
}

fun Logger.warnOutput(format: String, vararg arguments: Any) {
    if (isWarnEnabled) {
        this.warn(format, *arguments)
    }
}


fun Logger.errorOutput(format: String, vararg arguments: Any) {
    if (isErrorEnabled) {
        this.error(format, *arguments)
    }
}

fun Logger.errorOutput(msg: String, t: Throwable) {
    if (isErrorEnabled) {
        this.error(msg, t)
    }
}