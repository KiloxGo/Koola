dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        /**mavenLocal {
            content {
                includeGroup("io.github.libxposed")
            }
        }
        **/
        maven { url =  uri("https://api.xposed.info/") }

    }
    versionCatalogs {
        create("libs")
    }
}


pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Koola"
include(":app")
 