
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url=uri ("https://www.jitpack.io")}
        maven { url=uri("https://maven.admobile.top/repository/maven-releases/")}
    }

    resolutionStrategy {
        eachPlugin {
            if( requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:2.44")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url=uri ("https://www.jitpack.io")}
        maven { url=uri("https://maven.admobile.top/repository/maven-releases/")}
    }
}

rootProject.name = "worktime"
include(":app")

