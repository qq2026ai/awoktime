package cn.jianyun.worktime.module.base.router


sealed class WebDAVRouter(open val route: String) {
    object UserEdit : WebDAVRouter("WebDAVUserEdit")

}
