plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "cn.jianyun.worktime"
    compileSdk = 34

    defaultConfig {
        applicationId = "cn.jianyun.worktime"
        minSdk = 26
        targetSdk = 34
        versionCode = 61
        versionName = "3.2.2"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig=true
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "IS_DEV", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "IS_DEV", "false")
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {

    implementation("com.squareup:javapoet:1.13.0") // <-- added this
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    implementation("androidx.compose.foundation:foundation:1.6.0-alpha08")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0-alpha10")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.5.4")

    implementation("androidx.webkit:webkit:1.8.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")

    implementation("androidx.navigation:navigation-compose:2.7.4")

    implementation("androidx.room:room-ktx:2.6.0")

    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    //kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation("androidx.glance:glance-appwidget:1.0.0-alpha05")
    implementation("androidx.glance:glance:1.0.0-alpha05")

    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation("com.godaddy.android.colorpicker:compose-color-picker-android:0.7.0")

    implementation("dev.chrisbanes.snapper:snapper:0.3.0")
    implementation("co.yml:ycharts:2.1.0")

    implementation("org.apache.poi:poi-ooxml:5.2.3") // 支持.xlsx文件
    implementation("org.apache.poi:poi:5.2.3")      // 支持.xls文件
//    implementation("org.apache.xmlbeans:xmlbeans:5.1.1") // 必需依赖
//    implementation("javax.xml.stream:stax-api:1.0-2")    // 必需依赖

    implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.41")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.github.nanihadesuka:LazyColumnScrollbar:2.1.0")

    implementation("com.darkrockstudios:mpfilepicker:3.1.0")

    implementation("com.alipay.sdk:alipaysdk-android:+@aar")


    implementation("androidx.room:room-ktx:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")

    implementation("com.github.thegrizzlylabs:sardine-android:0.9")
    implementation("com.google.android.material:material:1.1.0")
    implementation("com.github.loper7:DateTimePicker:0.6.3")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.20")

    implementation("com.google.code.gson:gson:2.8.5")



}