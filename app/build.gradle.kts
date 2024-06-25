plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.restaurantmanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.restaurantmanager"
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.mediarouter:mediarouter:1.7.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.cloudinary:cloudinary-android:2.5.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.0")
    implementation("io.reactivex.rxjava2:rxjava:2.0.0")
    implementation("org.apache.commons:commons-lang3:3.5")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.android.gms:play-services-auth-api-phone:18.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.kenglxn.QRGen:android:2.6.0")
    implementation ("com.google.code.gson:gson:2.8.8")

}