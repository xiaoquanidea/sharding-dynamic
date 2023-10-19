dependencies {

    implementation(libs.orgSpringframeworkBoot.springBootStarter)
    implementation(libs.orgJetbrainsKotlin.kotlinReflect)
    compileOnly(libs.comBaomidou.mybatisPlusSpringBoot3Starter)
    compileOnly(libs.comMysql.mysqlConnectorJ)
    compileOnly(libs.comAtomikos.transactionsSpringBoot3Starter)

//    compileOnly(Libs.springbootMongodb)

    compileOnly(project(":sharding-core"))
}