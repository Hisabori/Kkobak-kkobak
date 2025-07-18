// settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Kakao SDK Repository
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "kkobakkobak"
// ✅ 이 부분이 수정된 핵심! 줄바꿈하고 문법을 명확히 했어.
include(":app")
include(":flutter_module")