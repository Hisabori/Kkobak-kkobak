plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    //id("com.google.gms.google-services")
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
        buildConfigField("boolean", "SHOW_SCHEDULE", "false")

        //경기도 정신건강 API 호출
        buildConfigField("String", "GG_API_KEY", "\"${properties["GG_API_KEY"]}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        // Kotlin 1.9.24 호환 Compose Compiler
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {

    //xml2kotlin
    implementation("com.tickaroo.tikxml:retrofit:4.1.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")


    // Compose BoM
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    // xml to .kt converter
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")

    // ViewModel, Fragment KTX (by viewModels)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.fragment:fragment-ktx:1.8.2")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Room + KSP
    val room = "2.6.1"
    implementation("androidx.room:room-ktx:$room")
    ksp("androidx.room:room-compiler:$room")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Material (뷰 위젯)
    implementation("com.google.android.material:material:1.12.0")

    // Lottie
    implementation("com.airbnb.android:lottie:6.4.0")

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
