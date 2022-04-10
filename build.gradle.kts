plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.0" // class默认是open
    java // 编译和测试Java源代码并将其组装成JAR文件的插件

    id("org.springframework.boot") version SpringBootVersion apply false // 使用spring-boot-dependencies依赖管理版本，并支持打成可执行jar
    id("io.spring.dependency-management") version "1.0.11.RELEASE" // 提供类似maven依赖管理功能

    idea
    kotlin("kapt") version "1.6.10"

}


allprojects {
    group = "com.github.hutiquan"
    version = "1.0-SNAPSHOT"

    repositories {
        maven("http://nexus.hgj.net/repository/public") {
            isAllowInsecureProtocol = true
        }
        maven("https://maven.aliyun.com/nexus/content/groups/public")
        maven("https://maven.aliyun.com/nexus/content/repositories/gradle-plugin")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    apply {
        plugin("kotlin")
        plugin("java")
    }

}

subprojects {
    apply {
        plugin("io.spring.dependency-management")
        plugin("kotlin-kapt")
        plugin("idea")
    }
    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    tasks.jar { enabled = true }

/*    sourceSets.main {
        java.srcDir("src/main/kotlin")
        kotlin.sourceSets.register("kotlin") {
            kotlin.srcDir("src/main/kotlin")
        }
//        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
//            kotlin.srcDir("src/main/kotlin")
//        }
    }*/

//    java {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//        registerFeature("source")
//
//    }

//    tasks.compileJava {
//        dependsOn(tasks.named("processResources"))
//    }

//    idea {
//        module {
//            val kaptMain = file("${project.buildDir}/generated/source/kapt/main")
//            sourceDirs.plusAssign(kaptMain)
//            generatedSourceDirs.plusAssign(kaptMain)
//
//            outputDir=file("${project.buildDir}/classes")
//            testOutputDir=file("${project.buildDir}/classes/test")
//        }
//    }
}

//tasks.named("compileJava") {
//    inputs.files(tasks.named("processResources"))
//}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}