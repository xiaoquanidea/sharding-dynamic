dependencies {
    implementation(project(":sharding-starter"))

    implementation(Libs.springbootWeb)
    implementation(Libs.mybatisPlus)
    implementation(Libs.springbootStarterAop)

    runtimeOnly(Libs.mysql)
}

ext {
    set("deploy.skip", "true")
    println(project.ext.properties)
}