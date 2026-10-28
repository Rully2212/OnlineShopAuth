plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.onlineshopauth"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.onlineshopauth"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
        buildConfigField("String", "BASE_URL", "\"https://pasarrakyatbangetayukulon.my.id/OnlineShop/\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    testImplementation("junit:junit:4.13.2")
}
