plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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

        val clubDiscussion = properties["CLUB_DISCUSSION_URL"]?.toString()
        if (clubDiscussion != null) {
            buildConfigField("String", "CLUB_DISCUSSION_URL", clubDiscussion)
        }

        val approveClub = properties["GET_UNAPPROVED_CLUBS_URL"]?.toString()
        if (approveClub != null) {
            buildConfigField("String", "GET_UNAPPROVED_CLUBS_URL", approveClub)
        }

        val getNotifications = properties["GET_NOTIFICATIONS_URL"]?.toString()
        if (getNotifications != null) {
            buildConfigField("String", "GET_NOTIFICATIONS_URL", getNotifications)
        }

        val getEvent = properties["GET_EVENT_URL"]?.toString()
        if (getEvent != null) {
            buildConfigField("String", "GET_EVENT_URL", getEvent)
        }

        val getFriend = properties["GET_FRIEND_URL"]?.toString()
        if (getFriend != null) {
            buildConfigField("String", "GET_FRIEND_URL", getFriend)
        }

        val clubAdmin = properties["CLUB_ADMIN_URL"]?.toString()
        if (clubAdmin != null) {
            buildConfigField("String", "CLUB_ADMIN_URL", clubAdmin)
        }

        val getForYouReccs = properties["FOR_YOU_URL"]?.toString()
        if (getForYouReccs != null) {
            buildConfigField("String", "FOR_YOU_URL", getForYouReccs)
        }

        val addEvent = properties["ADD_EVENT_URL"]?.toString()
        if (addEvent != null) {
            buildConfigField("String", "ADD_EVENT_URL", addEvent)
        }

        val getSpotlights = properties["GET_SPOTLIGHTS"]?.toString()
        if (getSpotlights != null) {
            buildConfigField("String", "GET_SPOTLIGHTS", getSpotlights)
        }

        val getAllCategories = properties["GET_ALL_CATEGORIES"]?.toString()
        if (getAllCategories != null) {
            buildConfigField("String", "GET_ALL_CATEGORIES", getAllCategories)
        }

        val getInterests = properties["GET_INTERESTS"]?.toString()
        if (getInterests != null) {
            buildConfigField("String", "GET_INTERESTS", getInterests)
        }

        val passwordChange = properties["PASSWORD_CHANGE"]?.toString()
        if (passwordChange != null) {
            buildConfigField("String", "PASSWORD_CHANGE", passwordChange)
        }

        val editEvent = properties["EDIT_EVENT_URL"]?.toString()
        if (editEvent != null) {
            buildConfigField("String", "EDIT_EVENT_URL", editEvent)
        }
        val getUserProfile = properties["GET_OWN_PROFILE"]?.toString()
        if (getUserProfile != null) {
            buildConfigField("String", "GET_OWN_PROFILE", getUserProfile)
        }

        val userRoute = properties["USER_ROUTE"]?.toString()
        if (userRoute != null) {
            buildConfigField("String", "USER_ROUTE", userRoute)
        }

        val wusaClubId = properties["WUSA_CLUB_ID"]?.toString()
        if (wusaClubId != null) {
            buildConfigField("String", "WUSA_CLUB_ID", wusaClubId)
        }

        val addSpotlightUrl = properties["ADD_SPOTLIGHT_URL"]?.toString()
        if (addSpotlightUrl != null) {
            buildConfigField("String", "ADD_SPOTLIGHT_URL", addSpotlightUrl)
        }

        val feedbackUrl = properties["FEEDBACK_URL"]?.toString()
        if (feedbackUrl != null) {
            buildConfigField("String", "FEEDBACK_URL", feedbackUrl)
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

// Allow references to generated code
kapt {
    correctErrorTypes = true
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
    implementation("androidx.compose.material:material-icons-core:1.6.3")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    implementation("com.auth0:java-jwt:3.18.2")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material:material:1.6.3")
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}
