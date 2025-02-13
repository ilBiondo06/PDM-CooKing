import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    //unica cosa diversa più o meno
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")


}

android {
    namespace = "com.unimib.cooking"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unimib.cooking"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    implementation(libs.androidx.annotation)
    implementation (libs.androidx.fragment)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.runtime)
    testImplementation(libs.junit)
    implementation (libs.retrofit.v290)
    implementation (libs.converter.gson)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.commons.validator.commons.validator)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.gson)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation (libs.shimmer)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.database)
    implementation (libs.google.firebase.auth)
    implementation (libs.google.firebase.database)

    implementation (libs.google.services)  // Assicurati che sia l'ultima versione
    implementation(platform(libs.firebase.bom.v3380))
    implementation(libs.play.services.auth)



}