//plik Gradle tylko dla czesci app
plugins {
    //kompilujemy aplikacje android
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //mechanizm ukrywania fizycznego klucza z map Google w czasie kompilacji aplikacji
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    //nazwa pakietu instalacyjnego 
    namespace = "com.example.monitorzdarzennaturalnych"
    //kompilujemy to korzystając z wersji SDK 34 (Android 14)
    compileSdk = 34

    //konfiguracja domyslna
    defaultConfig {
        applicationId = "com.example.monitorzdarzennaturalnych"
        minSdk = 24 
        targetSdk = 34 
        versionCode = 1 
        versionName = "1.0" 

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    //konfiguracja kompilacji
    buildTypes {

        release {
            isMinifyEnabled = false 
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

//biblioteki
dependencies {
    //podstawowe zestawy dla języka i designu
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //Google Maps - odpowiedzialne za okno wyświetlające mapę na ekranie
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    
    //Retrofit & Gson - Narzędzia do łączenia z systemem NASA EONET v3
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    //Coroutines - "wielowątkowość" do pobierania paczek poza głównym ekranem aby go nie zablokować
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
