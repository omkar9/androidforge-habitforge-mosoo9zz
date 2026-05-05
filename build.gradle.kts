plugins {
    id("com.android.application") version "8.3.2" apply false
    id("com.android.library") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.23" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

buildscript {
    dependencies {
        // No extra dependencies needed here, versions are handled by libs.versions.toml
    }
}

// No subprojects block or allprojects block needed for a single-module app.
// Dependencies are managed via libs.versions.toml