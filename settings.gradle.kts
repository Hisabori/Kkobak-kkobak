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
        // BlurView 라이브러리를 위한 저장소
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "2" // 사용자님의 프로젝트 이름
include(":app")