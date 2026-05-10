//Plik settings.gradle.kts to glowne ustawienia srodowiska projektu.
//Okreslamy w nim "skad" Android Studio ma sciagac wtyczki do budowania aplikacji.
pluginManagement {
    repositories {
        google()//Repozytorium Google
        mavenCentral()//Glowne publiczne repozytorium z bibliotekami
        gradlePluginPortal()//Portal przechowujacy glowne narzedzia Gradle
    }
}
//blok zabezpieczajacy, ktory nakazuje aby repozytoria byly uzywane przez wszystkie moduly aplikacji
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
//Nazwa projektu
rootProject.name = "Monitor_Zdarzen_Naturalnych"
//wlaczenie app 
include(":app")
