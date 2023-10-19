rootProject.name = "sharding-dynamic"

buildscript {
    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public")
        maven("https://maven.aliyun.com/nexus/content/repositories/gradle-plugin")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.apache.maven:maven-core:3.8.1")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

//rootProject.children.forEach {
//    it.name = (if("sharding-starter" == it.name)  "spring-boot-starter-sharding" else it.name)
//}

include("sharding-api")
include("sharding-core")
include("sharding-starter")
include("sharding-samples")
include("sharding-xa")
include("sharding-samples:sharding-jta-sample")
include("sharding-samples:sharding-nonjta-sample")
