plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.secrets)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.hidesign.hiweather"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("src/hiweather")
            storePassword = "Qwerty@+-123"
            keyAlias = "HiWeather"
            keyPassword = "Qwerty@+-123"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isDebuggable = false
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        getByName("debug") {
            versionNameSuffix = ".debug"
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = true
            isMinifyEnabled = false
            ext["enableCrashlytics"] = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    defaultConfig {
        applicationId = "com.hidesign.hiweather"
        minSdk = 28
        targetSdk = 35
        versionCode = 48
        versionName = "0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    dependenciesInfo {
        includeInBundle = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    kapt {
        correctErrorTypes = true
    }

    packaging {
        resources.excludes.add("META-INF/*.md")
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.pickFirsts.add("META-INF/LICENSE")
        resources.pickFirsts.add("META-INF/NOTICE")
        resources.pickFirsts.add("missing_rules.txt")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.work.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.timber)
    implementation(libs.permissionx)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttpprofiler)

    // Kotlin Dependencies
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)

    // Google Dependencies
    implementation(libs.places)
    implementation(libs.material)
    implementation(libs.play.services.location)
    implementation(libs.play.services.ads)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.gson)

    // Glide
    implementation(libs.android.decoview.charting)
    implementation(libs.glide)
    kapt(libs.compiler)
    implementation(libs.compose)
    implementation(libs.coil.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.viewbinding)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lottie.compose)

    // Dagger Hilt
    kapt(libs.hilt.compiler)
    kaptTest(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.androidx.hilt.work)

    // Testing
    implementation(libs.junit)
    implementation(libs.mockito.kotlin)
    implementation(libs.mockk)
    implementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.core.testing)
    implementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
}