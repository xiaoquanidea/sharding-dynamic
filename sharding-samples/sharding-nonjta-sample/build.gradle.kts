import Libs.deploySkip

dependencies {
    implementation(project(":sharding-starter"))

    implementation(Libs.springbootWeb)
    implementation(Libs.mybatisPlus)
    implementation(Libs.springbootStarterAop)

    runtimeOnly(Libs.mysql)
}

ext.deploySkip()