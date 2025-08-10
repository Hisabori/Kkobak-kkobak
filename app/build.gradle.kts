// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // KSP for Room
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

        // 🔧 Kotlin DSL에서는 함수 형태로 써야 함
        buildConfigField("boolean", "SHOW_SCHEDULE", "false")
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
        // (유지) Java 8 대상 — 필요하면 이후 17로 올리자
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "1.8" }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true   // ✅ 이 줄 추가


        // ⬇️ 만약 입원현황 화면을 Compose로 만들 거면 true
        // compose = true
    }

    // ⬇️ Compose를 쓰는 경우에만 활성화
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.5.14"
    // }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4") // ✅ 추가

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Kakao SDK
    implementation("com.kakao.sdk:v2-user:2.19.0")
    implementation("com.kakao.sdk:v2-navi:2.19.0")

    // Kakao Maps — 🔥 중복 버전 제거 (하나만 남기기)
    implementation("com.kakao.maps.open:android:2.9.5")
    // implementation("com.kakao.maps.open:android:2.6.0") // ❌ 삭제

    // Lottie
    implementation("com.airbnb.android:lottie:6.4.0")

    // --- 선택 1) Compose로 입원현황 화면 구성 시 추가 ---
    // val composeBom = platform("androidx.compose:compose-bom:2024.08.00")
    // implementation(composeBom)
    // implementation("androidx.activity:activity-compose:1.9.0")
    // implementation("androidx.compose.ui:ui")
    // implementation("androidx.compose.ui:ui-tooling-preview")
    // implementation("androidx.compose.material3:material3")
    // implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    // implementation("androidx.compose.material:material-pull-refresh") // pull-to-refresh
    // debugImplementation("androidx.compose.ui:ui-tooling")

    // --- 선택 2) XML(RecyclerView)로 가면 간단히 이거만 ---
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
