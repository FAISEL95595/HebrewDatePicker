
plugins {

    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}


android {
    namespace = "com.faisel.hebrewdatepicker"

    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}