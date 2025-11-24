plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") //version "1.9.24"
    //id("com.google.devtools.ksp") -> kapt로 변경
    id("org.jetbrains.kotlin.kapt")


    // id("com.google.gms.google-services")
    //alias(libs.plugins.composeComfiler)
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.kkobakkobak"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.kkobakkobak"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("boolean", "SHOW_SCHEDULE", "false")
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
    kotlinOptions { jvmTarget = "17"
        freeCompilerArgs =
            listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }


    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
    }

    dependencies {
        // 코루틴
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

        //kapt로 변경
        val room = "2.8.0"
        implementation("androidx.room:room-ktx:$room")
        kapt("androidx.room:room-compiler:$room")

        // Retrofit (최신 안정 버전으로 업데이트)
        implementation("com.squareup.retrofit2:retrofit:3.0.0")
        implementation("com.squareup.retrofit2:converter-simplexml:3.0.0")
        implementation("com.squareup.retrofit2:converter-gson:3.0.0")
        implementation("com.google.code.gson:gson:2.13.2")
        implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")

        // 코틀린 표준 라이브러리 (버전 고정)
        implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20") //
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.2.20") // 👈 1.9.24 -> 2.2.20으로 변경
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.20") //

        // Compose Material3 (BOM으로 버전 통합)
        implementation(platform("androidx.compose:compose-bom:2024.06.00"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")
        debugImplementation("androidx.compose.ui:ui-tooling")

        // Material Components
        implementation("com.google.android.material:material:1.13.0")

        // 라이프사이클 및 UI
        implementation("androidx.activity:activity-compose:1.11.0")
        implementation("androidx.core:core-ktx:1.17.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
        implementation("androidx.fragment:fragment-ktx:1.8.9")

        // SwipeRefreshLayout
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

        // Lottie 애니메이션
        implementation("com.airbnb.android:lottie:6.6.9")

        // MPAndroidChart
        implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

        val tikxml_version = "0.8.13" // 적절한 안정 버전 사용

        //implementation("com.tickaroo.tikxml:core:$tikxml_version")

        //--kapt("com.tickaroo.tikxml:processor:$tikxml_version")
    }
}