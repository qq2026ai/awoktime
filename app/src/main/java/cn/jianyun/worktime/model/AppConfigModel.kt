package cn.jianyun.worktime.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable


data class AppConfigModel(
    var needSecret: Boolean = false,
    var needSecretApps: String = "",
    var secret: String = "",
    var userApps: String = "",
    var appTheme: String = "auto"
) {
    @Composable
    fun getDarkTheme(): Boolean {
        if(appTheme == "auto"){
            return isSystemInDarkTheme()
        }
        return appTheme == "dark"
    }
}