import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val buildTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd.HHmm"))

android {
    namespace = "com.truebubble"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.spotrobotics.truebubble"
        minSdk = 26
        targetSdk = 36
        versionCode = 26
        versionName = "0.26"
        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
        // App UI strings are hardcoded in AppStrings.kt (not Android resources) for these
        // 10 languages; this only trims the AndroidX/Compose libraries' own bundled
        // resource strings (e.g. accessibility labels) down to the same locale set.
        resourceConfigurations += listOf("en", "pl", "es", "de", "fr", "pt", "ar", "ru", "in", "id", "ja")
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore/truebubble-release.jks")
            storePassword = "TrueBubble2024!"
            keyAlias = "truebubble"
            keyPassword = "TrueBubble2024!"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
        debug {
            applicationIdSuffix = ""
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    debugImplementation(libs.androidx.ui.tooling)
}
