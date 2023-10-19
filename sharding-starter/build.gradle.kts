dependencies {
    api(project(":sharding-core"))
    api(project(":sharding-xa"))

    implementation(libs.orgSpringframeworkBoot.springBootStarter)
//    kapt(Libs.springbootAutoconfigureProcessor)
    compileOnly(libs.comBaomidou.mybatisPlusSpringBoot3Starter)
}

tasks.jar {
    archiveBaseName.set("spring-boot-starter-sharding")
}