import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSerialization)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// Version Name
val major = 0
val minor = 0
val patch = 0
val date: String? = SimpleDateFormat("dd-MMM-yyyy", Locale.UK).format(Date())

// Version Code
val epoch = LocalDate.of(2025, 1, 1)!!
val today = LocalDate.now(ZoneOffset.UTC)!!
val daysSinceEpoch = ChronoUnit.DAYS.between(epoch, today).toInt()
val currentTime = ZonedDateTime.now(ZoneOffset.UTC)!!
val hour = currentTime.hour
val minute = currentTime.minute
val timeCode = hour * 100 + minute

android {
    namespace = "com.deepvisiontech.thecomicinator3000"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.deepvisiontech.thecomicinator3000"
        minSdk = 30
        targetSdk = 36
        versionCode = daysSinceEpoch * 10000 + timeCode
        versionName = "$major.$minor.$patch $date"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {

    //Dependency Injection
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.documentfile)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    //Local Storage
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences.core)

    //Icon
    implementation(libs.androidx.material.icons.extended)

    //Serialization
    implementation(libs.kotlinx.serialization.json)

    //WorkManager (Background Jobs)
    implementation(libs.androidx.work.runtime.ktx)

    //Image handling
    implementation(libs.coil.compose)
    implementation(libs.zoomable.image.coil)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)

    //Testing
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.work.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}