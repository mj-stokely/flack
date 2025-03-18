import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    targetHierarchy.default()

    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "flack"
        }
        
        target.compilations.getByName("main") {
            cinterops {
                val Network by creating {
                    defFile = project.file("src/nativeInterop/cinterop/Network.def")
                    packageName = "platform.Network"
                }
                val NetworkExtension by creating {
                    defFile = project.file("src/nativeInterop/cinterop/NetworkExtension.def")
                    packageName = "platform.NetworkExtension"
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core.ktx)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }
        val appleMain by getting {
            // Apple-specific dependencies
        }
        val appleTest by getting {
            // Apple-specific test dependencies
        }
    }
}

android {
    namespace = "dev.mjstokely.flack"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 30 // API 30+ as required
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            // Configure maven publication here
            create<MavenPublication>("release") {
                groupId = "dev.mjstokely"
                artifactId = "flack"
                version = "0.1.0"
                
                from(components["release"])
            }
        }
    }
}