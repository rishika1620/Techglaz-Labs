plugins {
    id("com.android.application")
    id("realm-android")
}

android {
    namespace = "com.example.radha.techglaz"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.radha.techglaz"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
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

    buildToolsVersion = "35.0.0"

    realm{
        isSyncEnabled = true;
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.9.0")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")
    implementation ("androidx.credentials:credentials:1.2.2")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.browser:browser:1.8.0")
    implementation ("org.mongodb:stitch-android-sdk:4.1.0")
    implementation ("com.razorpay:checkout:1.6.10")
    implementation ("com.itextpdf:itext7-core:7.2.3")
    implementation ("org.mongodb:mongodb-driver-sync:4.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation("androidx.multidex:multidex:2.0.1")
}