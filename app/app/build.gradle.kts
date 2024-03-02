plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.clubwat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clubwat"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildFeatures {
            buildConfig = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val registerApi = properties["REGISTER_API_URL"]?.toString()
        if (registerApi != null) {
            buildConfigField("String", "REGISTER_API_URL", registerApi)
        }
        val loginApi = properties["LOGIN_URL"]?.toString()
        if (loginApi != null) {
            buildConfigField("String", "LOGIN_URL", loginApi)
        }
        val emailVerificationEmail = properties["EMAIL_VERIFICATION_URL"]?.toString()
        if (emailVerificationEmail != null) {
            buildConfigField("String", "EMAIL_VERIFICATION_URL", emailVerificationEmail)
        }

        val searchClub = properties["SEARCH_CLUB_URL"]?.toString()
        if (searchClub != null) {
            buildConfigField("String", "SEARCH_CLUB_URL", searchClub)
        }

        val searchEvent = properties["SEARCH_EVENT_URL"]?.toString()
        if (searchEvent != null) {
            buildConfigField("String", "SEARCH_EVENT_URL", searchEvent)
        }

        val getClub = properties["GET_CLUB_URL"]?.toString()
        if (getClub != null) {
            buildConfigField("String", "GET_CLUB_URL", getClub)
        }

        val getAllClubs = properties["GET_ALL_CLUBS_FOR_USER"]?.toString()
        if (getAllClubs != null) {
            buildConfigField("String", "GET_ALL_CLUBS_FOR_USER", getAllClubs)
        }

        val getAllEvents = properties["GET_ALL_EVENTS_FOR_USER"]?.toString()
        if (getAllEvents != null) {
            buildConfigField("String", "GET_ALL_EVENTS_FOR_USER", getAllEvents)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.4.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-core:1.6.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("com.auth0:java-jwt:3.18.2")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0")
    implementation("androidx.compose.material:material:1.6.1")
}