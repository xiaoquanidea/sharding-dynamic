dependencies {
    api(project(":sharding-api"))
    api(libs.orgJetbrainsKotlin.kotlinReflect)

    implementation(libs.orgSpringframeworkBoot.springBootStarter)
    implementation(libs.orgSpringframeworkBoot.springBootStarterAop)

    kapt(libs.orgSpringframeworkBoot.springBootConfigurationProcessor)

    compileOnly(libs.comBaomidou.mybatisPlusSpringBoot3Starter)
    compileOnly(libs.comMysql.mysqlConnectorJ)
//    compileOnly(Libs.springbootJtaAtomikos)
}

kapt {
    keepJavacAnnotationProcessors = true
}

//tasks.named("compileJava") {
//    inputs.files(tasks.named("processResources"))
//}

