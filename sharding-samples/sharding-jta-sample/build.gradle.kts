import Libs.deploySkip

dependencies {


    implementation("com.baomidou:mybatis-plus-boot-starter:3.3.2")
    implementation(Libs.springbootJtaAtomikos)
}

ext.deploySkip()