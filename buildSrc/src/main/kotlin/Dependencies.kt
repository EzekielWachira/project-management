const val kotlinVersion = "1.4.2"
const val hilt_version = "2.33-beta"

object BuildPlugins {
    private object Version {
        const val buildToolsVersion = "4.1.2"
        const val gsmVersion = "4.3.5"
    }
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Version.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val hiltPlugin = "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
    const val safeArgsPlugins = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navVersion}"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val daggerPlugin = "dagger.hilt.android.plugin"
    const val navigationSafeArgs = "androidx.navigation.safeargs.kotlin"
    const val googleServices = "com.google.gms:google-services:${Version.gsmVersion}"
    const val googleServicesPlugin = "com.google.gms.google-services"
}

object AndroidSDK {
    const val minSdk = 19
    const val targetSdk = 30
    const val compileSdk = targetSdk
}

object TestLibraries {
    private object Version {
        const val jUnitVersion = "4.13.1"
        const val espressoVersion = "3.3.0"
        const val testRunner = "1.1.2"
    }
    const val jUnit = "junit:junit:${Version.jUnitVersion}"
    const val testRunner = "androidx.test.ext:junit:${Version.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Version.espressoVersion}"

}

object Dependencies {

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtxVersion}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
    const val materialComponents = "com.google.android.material:material:${Versions.materialDesignVersion}"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constrainLayoutVersion}"
    const val daggerHilt = "com.google.dagger:hilt-android:$hilt_version"
    const val daggerKtxCompiler = "com.google.dagger:hilt-compiler:$hilt_version"
    const val legacySupport = "androidx.legacy:legacy-support-v4:${Versions.legacySupportVersion}"

    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navVersion}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navVersion}"
    const val navigationDynamic = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navVersion}"

    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}"
    const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleVersion}"

    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
    const val lifecyleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycleVersion}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timberVersion}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glideVersion}"
    const val glideAnnotation = "com.github.bumptech.glide:compiler:${Versions.glideVersion}"
    const val workerKtx = "androidx.work:work-runtime-ktx:${Versions.workVersion}"
    const val circleImageView = "de.hdodenhof:circleimageview:${Versions.circleImageViewVersion}"
    const val multiDex = "androidx.multidex:multidex:${Versions.multidexVersion}"
    const val sdpAndroid = "com.intuit.sdp:sdp-android:${Versions.sdpAndroidVersion}"
    const val sspAndroid = "com.intuit.ssp:ssp-android:${Versions.sdpAndroidVersion}"

    const val firebase = "com.google.firebase:firebase-bom:${Versions.firebaseVersion}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseFireStore = "com.google.firebase:firebase-firestore-ktx"
    const val firebaseStorage = "com.google.firebase:firebase-storage-ktx"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val firebaseUiAuth = "com.firebaseui:firebase-ui-auth:${Versions.firebaseUiVersion}"
    const val firebaseUiForFirestore = "com.firebaseui:firebase-ui-firestore:${Versions.firebaseUiVersion}"
    const val coroutinesForFirebase = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutinesForFirebaseVersion}"
}