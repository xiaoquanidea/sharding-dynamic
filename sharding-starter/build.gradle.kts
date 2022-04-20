dependencies {
    api(project(":sharding-core"))
    api(project(":sharding-xa"))

    implementation(Libs.springbootStarter)
    kapt(Libs.springbootAutoconfigureProcessor)
    compileOnly(Libs.mybatisPlus)
}

tasks.jar {
    archiveBaseName.set("spring-boot-starter-sharding")
}