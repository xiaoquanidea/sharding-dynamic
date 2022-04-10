dependencies {
    implementation(project(":sharding-starter"))

    implementation(Libs.springbootWeb)
    implementation(Libs.mybatisPlus)
    implementation(Libs.springbootJtaAtomikos)
    implementation(Libs.springbootStarterAop)

    runtimeOnly(Libs.mysql)
}

tasks.register<Copy>("copySpringBootMetadataFile") {
    dependsOn("classes")
//    shouldRunAfter("build")

    val metadataFileOutputPathSuffix = "/tmp/kapt3/classes/main/META-INF"

    val shardingCoreProject = project.rootProject.project("sharding-core")
    val shardingStarterProject = project.rootProject.project("sharding-starter")

    from("${shardingCoreProject.buildDir}$metadataFileOutputPathSuffix") {
        include("**/*.json")
    }

    from("${shardingStarterProject.buildDir}$metadataFileOutputPathSuffix") {
        include("**/*.properties")
    }

    into("${project.buildDir}$metadataFileOutputPathSuffix")
}

tasks.findByName("build")?.dependsOn("copySpringBootMetadataFile")