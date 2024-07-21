// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven{
            url = uri("https://jitpack.io")
        }

    }
    dependencies {
        classpath ("io.realm:realm-gradle-plugin:10.15.1")
    }
}

plugins {
    id("com.android.application") version "8.5.0" apply false;
    id("com.android.library") version "8.5.0" apply false;
}