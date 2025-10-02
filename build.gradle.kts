plugins {
    id("com.android.application") version "8.9.3" apply false
    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
    id("com.google.devtools.ksp") version "2.2.20-2.0.3" apply false // ← Kotlin과 맞춤
    //alias(libs.plugins.compose.compiler) apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20" apply false
}
