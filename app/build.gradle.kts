plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("jacoco") // ✅ Add JaCoCo plugin
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

    // Room dependencies
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation("androidx.cardview:cardview:1.0.0")

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0") {
        because("Fix compatibility with Kotlin 2.1.0")
    }

    implementation("com.google.android.gms:play-services-wallet:19.2.0")

    // Lottie for animations
    implementation("com.airbnb.android:lottie:6.0.0")

    // Shimmer for loading effects
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Blurry for blur effects
    implementation("jp.wasabeef:blurry:4.0.1")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation("nl.dionsegijn:konfetti-xml:2.0.2")
    implementation("com.github.jetradarmobile:android-snowfall:1.2.1")
    implementation("nl.dionsegijn:konfetti-core:2.0.2")

    // ✅ Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")

    // ✅ Android Instrumentation & UI testing
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
}

// ✅ JaCoCo configuration
tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    val kotlinDebugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val javaDebugTree = fileTree("${buildDir}/intermediates/javac/debug/classes") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(kotlinDebugTree, javaDebugTree))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree(buildDir).include(
        "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
        "jacoco/testDebugUnitTest.exec"
    ))



}
