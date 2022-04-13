package com.github.hutiquan.sharding.core

import java.util.jar.Manifest

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-12 4:49 PM
 */
object ShardingDynamicBanner {

    val banner: String = """
        
           _____ __                   ___                ____                              _     
          / ___// /_  ____ __________/ (_)___  ____ _   / __ \__  ______  ____ _____ ___  (_)____
          \__ \/ __ \/ __ `/ ___/ __  / / __ \/ __ `/  / / / / / / / __ \/ __ `/ __ `__ \/ / ___/
         ___/ / / / / /_/ / /  / /_/ / / / / / /_/ /  / /_/ / /_/ / / / / /_/ / / / / / / / /__  
        /____/_/ /_/\__,_/_/   \__,_/_/_/ /_/\__, /  /_____/\__, /_/ /_/\__,_/_/ /_/ /_/_/\___/  
                                            /____/         /____/   
                                                                         
                                     dynamic datasource version on ${getVersion()} designed by bookingGroup
        
    """.trimIndent()

    private fun getVersion(): String {
        val pck: Package? = ShardingDynamicBanner::class.java.`package`
        return pck?.implementationVersion ?: "(unknown)"
    }
}

fun main() {
    println(ShardingDynamicBanner.banner)
}