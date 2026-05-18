//plik Gradle dla czesci app
plugins {
    //kompilujemy aplikacje 
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    //mechanizm ukrywania fizycznego klucza z map Google w czasie kompilacji aplikacji
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    //nazwa pakietu instalacyjnego 
    namespace = "com.example.monitorzdarzennaturalnych"
    //kompilujemy korzystając z wersji SDK 34 
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
        compose = true
        buildConfig = true
    }
}

//biblioteki
dependencies {
    //podstawowe zestawy dla jezyka
    implementation("androidx.core:core-ktx:1.12.0")
    
    //narzedzia mvvm
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // JETPACK COMPOSE (Nowoczesny Interfejs)
    val composeBom = platform("androidx.compose:compose-bom:2024.04.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.9.0")
    
    // Odczytywanie LiveData (naszego powiadamiacza z viewmodelu) wewnatrz Compose
    implementation("androidx.compose.runtime:runtime-livedata")

    // Nowoczesne Mapy pod Compose
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    //Retrofit oraz Gson - narzedzia do laczenia z systemem NASA EONET v3
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //Coroutines - wielowatkowosc do pobierania danych poza glownym ekranem aby go nie zablokowac
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
