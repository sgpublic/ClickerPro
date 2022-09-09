// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val androidVer = "7.2.2"
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version androidVer apply false

    val kotlinVer = "1.7.10"
    id("org.jetbrains.kotlin.android") version kotlinVer apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version kotlinVer apply false
    id("org.jetbrains.kotlin.kapt") version kotlinVer apply false
    id("org.jetbrains.kotlin.jvm") version kotlinVer apply false
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}