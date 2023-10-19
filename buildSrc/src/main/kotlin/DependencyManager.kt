import org.gradle.api.plugins.ExtraPropertiesExtension

object Libs {

    fun ExtraPropertiesExtension.deploySkip() {
        set("deploy.skip", "true")
    }
}