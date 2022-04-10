dependencies {
    api(project(":sharding-core"))
    api(project(":sharding-xa"))

    implementation(Libs.springbootStarter)

    kapt(Libs.springbootAutoconfigureProcessor)
}