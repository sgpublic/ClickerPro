@file:Suppress("PropertyName")

import com.android.build.api.dsl.VariantDimension
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import io.github.sgpublic.gradle.core.BuildTypes
import io.github.sgpublic.gradle.core.SignConfig
import io.github.sgpublic.gradle.core.VersionGen
import io.github.sgpublic.gradle.util.ApkUtil
import java.util.*

plugins {
    id("android-vc-plugin")

    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.kapt")
}

fun VariantDimension.buildConfigField(name: String, value: String) {
    buildConfigField("String", name, "\"$value\"")
}
fun VariantDimension.buildConfigField(name: String, value: Int) {
    buildConfigField("int", name, value.toString())
}

android {
    compileSdk = 33
    buildToolsVersion = "33.0.0"

    val properties = file("./sign/sign.properties")
    val signInfoExit: Boolean = properties.exists()

    if (signInfoExit){
        val keyProps = Properties()
        keyProps.load(properties.inputStream())
        signingConfigs {
            create(SignConfig.NAME) {
                val SIGN_DIR: String by keyProps
                val SIGN_PASSWORD_STORE: String by keyProps
                val SIGN_ALIAS: String by keyProps
                val SIGN_PASSWORD_KEY: String by keyProps
                keyPassword = SIGN_PASSWORD_KEY
                keyAlias = SIGN_ALIAS
                storeFile = file(SIGN_DIR)
                storePassword = SIGN_PASSWORD_STORE
            }
        }
    }

    namespace = "io.github.clickerpro"
    defaultConfig {
        applicationId = namespace
        minSdk = 28
        targetSdk = 33
        versionCode = VersionGen.COMMIT_VERSION
        versionName = "1.0.0"

        renderscriptTargetApi = 26
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        if (signInfoExit) {
            signingConfig = signingConfigs.getByName("sign")
        }

        "mhmzx/ClickerPro".let {
            buildConfigField("GITHUB_REPO", it)
            val repo = it.split("/")
            buildConfigField("GITHUB_AUTHOR", repo[0])
            buildConfigField("GITHUB_REPO_NAME", repo[1])
        }
        buildConfigField("PROJECT_NAME", rootProject.name)
        buildConfigField("TYPE_RELEASE", BuildTypes.TYPE_RELEASE)
        buildConfigField("LEVEL_RELEASE", BuildTypes.LEVEL_RELEASE)
        buildConfigField("TYPE_BETA", BuildTypes.TYPE_BETA)
        buildConfigField("LEVEL_BETA", BuildTypes.LEVEL_BETA)
        buildConfigField("TYPE_ALPHA", BuildTypes.TYPE_ALPHA)
        buildConfigField("LEVEL_ALPHA", BuildTypes.LEVEL_ALPHA)
        buildConfigField("TYPE_DEBUG", BuildTypes.TYPE_DEBUG)
        buildConfigField("LEVEL_DEBUG", BuildTypes.LEVEL_DEBUG)
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        all {
            isMinifyEnabled = false
            if (signInfoExit) {
                signingConfig = signingConfigs.getByName(SignConfig.NAME)
            }
        }
        /** 自动化版本命名 */
        named(BuildTypes.TYPE_RELEASE) {
            versionNameSuffix = "-$name"
            buildConfigField("VERSION_SUFFIX", "")
            buildConfigField("BUILD_LEVEL", BuildTypes.LEVEL_RELEASE)
        }
        named(BuildTypes.TYPE_DEBUG) {
            defaultConfig.versionCode = VersionGen.DATED_VERSION
            isDebuggable = true
            versionNameSuffix = "-$name"
            buildConfigField("VERSION_SUFFIX", "")
            buildConfigField("BUILD_LEVEL", BuildTypes.LEVEL_DEBUG)
        }
        if (signInfoExit) {
            register(BuildTypes.TYPE_BETA) {
                val suffix = VersionGen.GIT_HEAD
                versionNameSuffix = "-$suffix-$name"
                isDebuggable = true
                buildConfigField("VERSION_SUFFIX", suffix)
                buildConfigField("BUILD_LEVEL", BuildTypes.LEVEL_BETA)
            }
            register(BuildTypes.TYPE_ALPHA) {
                val suffix = VersionGen.TIME_MD5
                defaultConfig.versionCode = VersionGen.DATED_VERSION
                isDebuggable = true
                versionNameSuffix = "-$suffix-$name"
                buildConfigField("VERSION_SUFFIX", suffix)
                buildConfigField("BUILD_LEVEL", BuildTypes.LEVEL_ALPHA)
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packagingOptions {
        resources.excludes.addAll(listOf(
            "META-INF/DEPENDENCIES",
            "META-INF/NOTICE",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/NOTICE.txt",
        ))
    }
}

dependencies {
    implementation("androidx.test.ext:junit-ktx:1.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation("androidx.test:runner:1.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")

    implementation("com.google.android.material:material:1.7.0")

    /* https://developer.android.com/reference/kotlin/androidx/core/splashscreen/SplashScreen */
    implementation("androidx.core:core-splashscreen:1.0.0")
    /* https://github.com/square/okhttp */
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    /* https://github.com/yanzhenjie/Sofia */
    implementation("com.yanzhenjie:sofia:1.0.5")
    /* https://github.com/getActivity/XXPermissions */
    implementation("com.github.getActivity:XXPermissions:16.2")

//    val lombok = "1.18.16"
//    compileOnly("org.projectlombok:lombok:$lombok")
//    annotationProcessor("org.projectlombok:lombok:$lombok")

    val roomVer = "2.4.2"
    implementation("androidx.room:room-runtime:$roomVer")
    implementation("androidx.room:room-ktx:$roomVer")
    annotationProcessor("androidx.room:room-compiler:$roomVer")
    kapt("androidx.room:room-compiler:$roomVer")
    testImplementation("androidx.room:room-testing:$roomVer")

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("com.github.tony19:logback-android:2.0.0")
    implementation(kotlin("reflect"))
}

/** 自动修改输出文件名并定位文件 */
android.applicationVariants.all {
    for (output in outputs) {
        if (output !is BaseVariantOutputImpl) {
            continue
        }
        val name = output.name.split("-")
            .joinToString("") { it.capitalize() }
        val taskName = "assemble${name}AndLocate"
        tasks.register(taskName) {
            dependsOn("assemble${name}")
            doLast {
                ApkUtil.assembleAndLocate(output.name, output.outputFile, "./build/assemble")
            }
        }
    }
}