// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // ✅ kapt 플러그인을 버리고 ksp를 사용합니다.
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.kkobakkobak"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kkobakkobak"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Room 데이터베이스
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Retrofit (네트워킹)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Kakao SDK
    implementation("com.kakao.sdk:v2-user:2.19.0")
    implementation("com.kakao.maps.open:android:2.9.5")
    implementation("com.kakao.sdk:v2-navi:2.19.0")

    // Lottie
    implementation("com.airbnb.android:lottie:6.4.0")
    // build.gradle.kts (:app 기준)
    implementation("com.kakao.maps.open:android:2.6.0") // 최신 버전 확인
    implementation("androidx.cardview:cardview:1.0.0")
    // Flutter engine for add-to-app
    implementation("io.flutter:flutter_embedding_debug:1.0.0")



    // 테스트 관련
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
