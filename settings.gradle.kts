//Plik settings.gradle.kts to glowne ustawienia srodowiska projektu.
//Okreslamy w nim "skad" Android Studio ma sciagac wtyczki do budowania aplikacji.
pluginManagement {
    repositories {
        google()//Repozytorium Google
        mavenCentral()//Glowne publiczne repozytorium z bibliotekami open-source
        gradlePluginPortal()//Portal przechowujacy glowne narzedzia Gradle
    }
}
//blok zabezpiecza projekt, nakazujac zeby te repozytoria (Google i MavenCentral) 
//byly uzywane przez wszystkie moduly aplikacji
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
//Nazwa projektu
rootProject.name = "Monitor_Zdarzen_Naturalnych"
//wlaczenie app (w ktorym tworzymy MainActivity i reszte kodu)
include(":app")
