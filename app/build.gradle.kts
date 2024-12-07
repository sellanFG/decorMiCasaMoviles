plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.decormicasa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.decormicasa"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    //libreria de swip refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation (libs.play.services.maps)
    implementation (libs.play.services.location)
    // sms firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-appcheck-playintegrity:17.1.1")
    implementation ("com.google.firebase:firebase-appcheck:17.1.1")
    implementation ("com.google.firebase:firebase-appcheck-debug:17.1.1")  // Solo para desarrollo
    //paea el usuario*
    implementation ("com.google.android.material:material:1.9.0") // Usa la última versión estable
    //mercado pago
    implementation("androidx.browser:browser:1.4.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.biometric:biometric:1.1.0")

}