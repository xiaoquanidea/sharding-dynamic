import Libs.deploySkip

dependencies {
    implementation(libs.comBaomidou.mybatisPlusSpringBoot3Starter)
    implementation(libs.comAtomikos.transactionsSpringBoot3Starter)
}

ext.deploySkip()