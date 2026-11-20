plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.flotime"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.flotime"
        minSdk = 24
        targetSdk = 34

        val rawVersion = System.getenv("APP_VERSION_NAME")
            ?.trim()
            ?.removePrefix("v")
            ?.removePrefix("V")
            ?.takeIf { it.isNotEmpty() } ?: "1.0.0"

        val verParts = rawVersion.split("-", limit = 2)[0]
            .split(".")
            .mapNotNull { it.toIntOrNull() }
        val autoVersionCode = when {
            verParts.size >= 3 -> verParts[0] * 10000 + verParts[1] * 100 + verParts[2]
            verParts.size == 2 -> verParts[0] * 10000 + verParts[1] * 100
            verParts.size == 1 -> verParts[0] * 10000
            else -> 1
        }.coerceAtLeast(1)

        versionCode = System.getenv("APP_VERSION_CODE")?.toIntOrNull() ?: autoVersionCode
        versionName = rawVersion

        resourceConfigurations += listOf("en")
    }

    signingConfigs {
        create("release") {
            val ksFile = file("release.jks")
            if (ksFile.exists()) {
                storeFile = ksFile
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE*",
                "/META-INF/NOTICE*",
                "DebugProbesKt.bin",
                "kotlin-tooling-metadata.json"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Zero external dependencies (100% Native Android SDK + Kotlin stdlib)
}
