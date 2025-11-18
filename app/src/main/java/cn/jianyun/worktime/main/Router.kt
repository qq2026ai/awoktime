package cn.jianyun.worktime.main

import android.os.Bundle
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator


sealed class Router(open val route: String) {
    object Home : Router("Home")
    object TestMainView : Router("TestMainView")
    object PrivatePolicy : Router("PrivatePolicy2")
    object UserPolicy : Router("UserPolicy")
    object TodoList: Router("TodoList")
    object Password: Router("Password")
    object Piecework: Router("Piecework")
    object Schedule: Router("Schedule")
    object Gift: Router("Gift")
    object Countdown: Router("Countdown")
    object Time: Router("Time")
    object Borrow: Router("Borrow")
    object Diary: Router("Diary")
    object Travel: Router("Travel")
    object Setting: Router("Setting")
    object Feedback: Router("Feedback")
    object Question: Router("Question")
    object UserDetail: Router("UserDetail")
    object WebDAVManage: Router("WebDAVManage")
    object WebDAV: Router("WebDAV")
    object LocalBackup: Router("LocalBackup")
    object NotifySetting: Router("NotifySetting")

    object VipPage: Router("VipPage")
    object BatchAdd: Router("BatchAdd")
    object BatchDelete: Router("BatchDelete")
    object ImportData: Router("ImportData")
}


fun NavHostController.navigateTo(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorExtras)
    } else {
        navigate(route, navOptions, navigatorExtras)
    }
}