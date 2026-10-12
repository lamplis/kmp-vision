/*
 * Copyright 2026 lamplis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    id("com.vanniktech.maven.publish") version "0.37.0"
}

mavenPublishing {
    coordinates(project.group.toString(), "vision-text", project.version.toString())
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    pom {
        name.set("vision-text")
        description.set("On-device text recognition for Android and iOS.")
        url.set("https://github.com/lamplis/kmp-vision")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("lamplis")
                name.set("lamplis")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/lamplis/kmp-vision.git")
            url.set("https://github.com/lamplis/kmp-vision")
        }
    }
}

kotlin {
    androidLibrary {
        namespace = "io.github.lamplis.vision.text"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.mlkit.text.recognition)
            implementation(libs.kotlinx.coroutines.play.services)
        }
    }
}
