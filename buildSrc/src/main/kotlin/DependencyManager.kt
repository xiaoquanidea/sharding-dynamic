import org.gradle.api.plugins.ExtraPropertiesExtension

const val kotlinVersion = "1.6.21"
const val springBootVersion = "2.6.6"
const val mybatisPlusVersion = "3.5.1"


object Libs {
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect"

    const val mybatisPlus = "com.baomidou:mybatis-plus-boot-starter:$mybatisPlusVersion"
    const val mysql = "mysql:mysql-connector-java"


    // springboot
    const val springbootStarter = "org.springframework.boot:spring-boot-starter"
    const val springbootConfigurationProcessor = "org.springframework.boot:spring-boot-configuration-processor"
    const val springbootAutoconfigureProcessor = "org.springframework.boot:spring-boot-autoconfigure-processor"
    const val springbootJtaAtomikos = "org.springframework.boot:spring-boot-starter-jta-atomikos"
    const val springbootStarterAop = "org.springframework.boot:spring-boot-starter-aop"
    const val springbootStarterTest = "org.springframework.boot:spring-boot-starter-test"
    const val springbootMongodb = "org.springframework.boot:spring-boot-starter-data-mongodb"


    const val springbootWeb = "org.springframework.boot:spring-boot-starter-web"


    fun ExtraPropertiesExtension.deploySkip() {
        set("deploy.skip", "true")
    }
}