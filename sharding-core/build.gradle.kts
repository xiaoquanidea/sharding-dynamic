import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

dependencies {
    api(project(":sharding-api"))
    api(Libs.kotlinReflect)

    implementation(Libs.springbootStarter)
    implementation(Libs.springbootStarterAop)

    kapt(Libs.springbootConfigurationProcessor)

    compileOnly(Libs.mybatisPlus)
    compileOnly(Libs.mysql)
//    compileOnly(Libs.springbootJtaAtomikos)
}

kapt {
    keepJavacAnnotationProcessors = true
}

//tasks.named("compileJava") {
//    inputs.files(tasks.named("processResources"))
//}

