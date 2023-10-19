import org.apache.maven.settings.Settings
import org.apache.maven.settings.io.DefaultSettingsReader
import org.apache.maven.settings.io.SettingsReader
import org.gradle.api.internal.artifacts.mvnsettings.DefaultMavenFileLocations
import org.gradle.api.internal.artifacts.mvnsettings.DefaultMavenSettingsProvider
import kotlin.reflect.full.declaredMemberProperties
import org.gradle.api.internal.artifacts.mvnsettings.MavenFileLocations
import kotlin.reflect.jvm.isAccessible
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.allopen") version libs.versions.kotlin // class默认是open
    kotlin("kapt") version libs.versions.kotlin
    java // 编译和测试Java源代码并将其组装成JAR文件的插件
    `maven-publish` // maven发布

    alias(libs.plugins.orgSpringframeworkBoot) apply false // 使用spring-boot-dependencies依赖管理版本，并支持打成可执行jar
    alias(libs.plugins.ioSpringDependencyManagement) // 提供类似maven依赖管理功能
}


allprojects {
    group = "com.github.hutiquan"
    version = "2.0-SNAPSHOT"

    repositories {
        maven("http://nexus.hgj.net/repository/public") { isAllowInsecureProtocol = true }
        maven("https://maven.aliyun.com/nexus/content/groups/public")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.aliyun.com/nexus/content/repositories/gradle-plugin")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    apply {
        plugin("kotlin")
        plugin("java")
    }

}

tasks.jar{ enabled = false}

subprojects {
    apply {
        plugin("io.spring.dependency-management")
        plugin("kotlin-kapt")
        plugin("maven-publish")
        plugin("kotlin-allopen")
    }
    allOpen {
        preset("spring")
    }
    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }

    dependencies {
//        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
//        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    tasks.jar { enabled = true }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_17 // 指定编译.java文件的jdk版本
        targetCompatibility = JavaVersion.VERSION_17 // 确保.class文件与targetCompatibility所指定版本或者更新版本的java虚拟机兼容
        toolchain {
            languageVersion.set(JavaLanguageVersion.of("17"))
            vendor.set(JvmVendorSpec.ORACLE)
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict") // 添加-Xjsr305编译器标志来启用此功能strict
            jvmTarget = "17" // Kotlin 编译器配置为生成 Java 8 字节码（默认为 Java 6）
        }
    }

    val configure: (PublishingExtension).() -> Unit = {
        repositories {
            maven {
                name = "hgjMaven"
                /*fun DefaultMavenSettingsProvider.readLocalSettings(): Settings? {
                    val options = mapOf(SettingsReader.IS_STRICT to false)
                    val settingsReader = DefaultSettingsReader()
                    val mavenFileLocationsKP = DefaultMavenSettingsProvider::class.declaredMemberProperties.find { it.name == "mavenFileLocations" }
                            ?: return null
                    mavenFileLocationsKP.isAccessible = true
                    val mavenFileLocations = mavenFileLocationsKP.get(this) as MavenFileLocations
                    val settings = settingsReader.read((mavenFileLocations.globalSettingsFile ?: return null), options)
                    return settings
                }

                val mavenFileLocations = DefaultMavenFileLocations()
                val mavenSettingsProvider = DefaultMavenSettingsProvider(mavenFileLocations)
                val settings = mavenSettingsProvider.readLocalSettings()*/

                val nexusReleases = "nexus.releases"
                val nexusSnapshot = "nexus.snapshots"
                val nexusPublic = "nexus.public"

                val defaultMavenFileLocations = DefaultMavenFileLocations()
                val userSettingsFile = defaultMavenFileLocations.userSettingsFile ?: defaultMavenFileLocations.globalSettingsFile
                var settings: Settings? = null
                try {
                    settings = DefaultSettingsReader().read(userSettingsFile, mapOf(SettingsReader.IS_STRICT to false))
                } catch (_: Exception) {
                    //ignore
                }
                val sever =
                    settings?.servers?.find { it.id == nexusReleases || it.id == nexusSnapshot || it.id == nexusPublic }
                        ?: return@maven

                credentials {
                    username = sever.username
                    password = sever.password
                }

                val isSnapshot = version.toString().endsWith("SNAPSHOT", true)
                val allConfiguredRepository = settings.profiles.flatMap { it.repositories }
                val repositoryUrl =
                    allConfiguredRepository.find { if (isSnapshot) it.id == nexusSnapshot else it.id == nexusReleases }?.url
                url = repositoryUrl?.let { uri(it) } ?: return@maven
                isAllowInsecureProtocol = true
            }

            publications {
                create<MavenPublication>("hgjMaven") {
                    from(components["java"])
                    pom {
                        artifactId = tasks.jar.orNull?.archiveBaseName?.get()
                        packaging = "jar"
                        name.set(rootProject.name)
                        description.set("Springboot整合MybatisPlus、Atomikos、多数据源读写分离")
                        url.set("http://git.hgj.net/booking/sharding-dynamic")
                        inceptionYear.set("2022")
                        organization {
                            name.set("海管家")
                            url.set("https://www.hgj.com")
                        }
                        /*licenses {
                            license {
                                name.set("Eclipse Public License v2.0")
                                url.set("http://www.eclipse.org/legal/epl-v20.html")
                                distribution.set("inside artifact")
                                comments.set("Successor of EPL v1.0")
                            }
                        }*/
                        developers {
                            developer {
                                id.set("TiQuan Hu")
                                name.set("TiQuan Hu")
                                email.set("xiaoquanidea@163.com")
                            }
                        }
                        scm {
                            connection.set("scm:git:http://git.hgj.net/booking/sharding-dynamic")
                            developerConnection.set("scm:git:ssh://git@git.hgj.net:8022/booking/sharding-dynamic.git")
                            url.set("http://git.hgj.net/booking/sharding-dynamic")
                            tag.set("HEAD")
                        }


                    }
                }
            }

        }
    }

    afterEvaluate {
        if (!project.ext.has("deploy.skip")) {
            publishing(configure)
        }
    }

}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}