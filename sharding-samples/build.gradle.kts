import Libs.deploySkip

subprojects {
    dependencies {
        implementation(project(":sharding-starter"))

        implementation(Libs.springbootWeb)
        implementation(Libs.springbootStarterAop)
        testImplementation(Libs.springbootStarterTest)
        runtimeOnly(Libs.mysql)
    }
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
    project.childProjects.forEach {
        into("${it.value.buildDir}$metadataFileOutputPathSuffix")
    }
}

tasks.findByName("build")?.dependsOn("copySpringBootMetadataFile")

ext.deploySkip()