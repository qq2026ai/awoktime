package cn.jianyun.worktime.module.timework.route


sealed class TimeworkRouter(open val route: String) {
    object TimeworkSalaryEdit : TimeworkRouter("TimeworkSalaryEdit")
    object TimeworkAwardEdit : TimeworkRouter("TimeworkAwardEdit")
    object TimeworkDefaultManage : TimeworkRouter("TimeworkDefaultManage")
    object TimeworkDefaultEdit : TimeworkRouter("TimeworkDefaultEdit")
    object TimeworkAppStyle : TimeworkRouter("TimeworkAppStyle")
    object TimeworkDetailData : TimeworkRouter("TimeworkDetailData")
    object TimeworkCloudManage : TimeworkRouter("TimeworkCloudManage")
    object TimeworkProjectManage : TimeworkRouter("TimeworkProjectManage")
}
