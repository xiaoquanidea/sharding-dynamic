dependencies {

    implementation(Libs.springbootStarter)
    implementation(Libs.kotlinReflect)
    compileOnly(Libs.mybatisPlus)
    compileOnly(Libs.mysql)
    compileOnly(Libs.springbootJtaAtomikos)

//    compileOnly(Libs.springbootMongodb)

    compileOnly(project(":sharding-core"))
}