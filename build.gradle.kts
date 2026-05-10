buildscript {
    repositories {
        maven(url = "https://maven.google.com")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
}
