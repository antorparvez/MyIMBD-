// Project-level build.gradle.kts (root)
buildscript {
    val kotlin_version by extra("2.0.20")
    val hilt_version by extra("2.52")

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
    }

    repositories {
        google()
        mavenCentral()
    }
}
