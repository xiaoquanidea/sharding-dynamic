dependencies {
    implementation(project(":sharding-starter"))

    implementation(Libs.springbootWeb)
//    implementation(Libs.mybatisPlus)
    implementation("com.baomidou:mybatis-plus-boot-starter:3.3.2")
    implementation(Libs.springbootJtaAtomikos)
    implementation(Libs.springbootStarterAop)

    runtimeOnly(Libs.mysql)
}

ext {
    set("deploy.skip", "true")
    println(project.ext.properties)
}