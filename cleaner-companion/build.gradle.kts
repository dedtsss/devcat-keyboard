plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "devcat.catboard.cleaner"
    compileSdk = 36

    defaultConfig {
        applicationId = "devcat.catboard.cleaner"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "0.1-dev"
    }

    buildTypes {
        release { isMinifyEnabled = false }
        // Keep one explicit target package for the keyboard's allowlisted IPC.
        debug { }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
}
