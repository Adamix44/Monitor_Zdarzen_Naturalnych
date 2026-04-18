// To jest specyficzny plik Gradle tylko dla naszej głównej części kodowej - modułu "app".

plugins {
    // Oznacza, że to co kompilujemy jest faktyczną instalowalną aplikacją Android
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Włącza mechanizm ukrywania fizycznego klucza z map Google w czasie kompilacji aplikacji
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    // Unikalna nazwa Twojego pakietu instalacyjnego (widoczna tak w Sklepie Play)
    namespace = "com.example.monitorzdarzennaturalnych"
    // Kompilujemy to korzystając z wersji SDK 34 (Android 14)
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.monitorzdarzennaturalnych"
        minSdk = 24 // Apka zadziała na starych systemach aż do Androida 7.0 !
        targetSdk = 34 // Celuje jednak na stabilność pod najnowszym Androidem 14
        versionCode = 1 // Numer wersji dla systemu (1, 2, 3...) kiedy wydasz aktualizację
        versionName = "1.0" // Wersja tekstowa (v1.0, v1.0.1)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Konfiguracje wydania do Sklepu Google Play
        release {
            isMinifyEnabled = false // Szyfrowanie kodu przed złodziejami (tu wyłączone do nauki)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Pozwala na unikanie powtarzających sie findViewById w oknach
    buildFeatures {
        viewBinding = true
    }
}

// Tutaj mówisz systemowi: "Słuchaj, mój kod korzysta z logiki innej firmy, połącz mi z nią apkę"
dependencies {
    // Podstawowe zestawy dla języka i designu
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Google Maps - odpowiedzialne za okno wyświetlające mapę na ekranie
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    
    // Retrofit & Gson - Narzędzia do łączenia z systemem NASA EONET v3
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Coroutines - "wielowątkowość" do pobierania paczek poza głównym ekranem aby go nie zablokować
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
