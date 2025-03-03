plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Remove KSP plugin references (we're using kapt)
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.pkart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pkart"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

kapt {

    correctErrorTypes = true
    showProcessorStats = true

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")

    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // Room dependencies using kapt (no KSP)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0") {
        because("Fix compatibility with Kotlin 2.1.0")
    }
}
