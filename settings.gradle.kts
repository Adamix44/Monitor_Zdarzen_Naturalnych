// Plik settings.gradle.kts to główne ustawienia środowiska Twojego projektu.
// Określamy w nim "skąd" Android Studio ma ściągać wtyczki do budowania aplikacji.
pluginManagement {
    repositories {
        google() // Repozytorium Google (niezbędne do wtyczek od Androida)
        mavenCentral() // Główne publiczne repozytorium z setkami tysięcy bibliotek open-source
        gradlePluginPortal() // Portal przechowujący główne narzędzia Gradle
    }
}
// Poniższy blok zabezpiecza projekt, nakazując żeby to TE repozytoria (Google i MavenCentral) 
// były używane przez wszystkie moduły Twojej aplikacji, zamiast szukać po plikach poszczególnych modułów.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
// Nazwa korzenia Twojego projektu
rootProject.name = "Monitor_Zdarzen_Naturalnych"
// Włączenie modułu o nazwie "app" (w którym tworzymy MainActivity i resztę kodu)
include(":app")
