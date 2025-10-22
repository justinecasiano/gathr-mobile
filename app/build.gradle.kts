import java.util.Properties
import java.io.FileInputStream

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.diffplug.spotless") version "8.0.0"
    id("com.google.gms.google-services")
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint("1.3.1").editorConfigOverride(
            mapOf(
                "ktlint_standard_function-naming" to "disabled", // allow Composables starting with uppercase
                "ktlint_standard_no-wildcard-imports" to "disabled", // allow wildcard imports
                "indent_size" to 4,
                "max_line_length" to 120,
            ),
        )
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

android {
    namespace = "com.example.gathr"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gathr"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val homeDir = System.getProperty("user.home")
            storeFile = keystoreProperties.getProperty("SIGNING_STORE_FILE")?.let {
                rootProject.file(it)
            } ?: rootProject.file("$homeDir/.keystores/release-keystore.jks")

            storePassword = keystoreProperties.getProperty("STORE_PASSWORD")
            keyAlias = keystoreProperties.getProperty("KEY_ALIAS")
            keyPassword = keystoreProperties.getProperty("KEY_PASSWORD")

            if (!storeFile?.exists()!! ||
                storePassword.isNullOrBlank() ||
                keyAlias.isNullOrBlank() ||
                keyPassword.isNullOrBlank()) {
                throw GradleException("❌ Missing or invalid signing config for release build.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
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
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.animation.core.lint)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
