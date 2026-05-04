package cn.jianyun.worktime.module.timework.route


sealed class TimeworkRouter(open val route: String) {
    object TimeworkSalaryEdit : TimeworkRouter("TimeworkSalaryEdit")
    object TimeworkAwardEdit : TimeworkRouter("TimeworkAwardEdit")
    object TimeworkDefaultManage : TimeworkRouter("TimeworkDefaultManage")
    object TimeworkDefaultEdit : TimeworkRouter("TimeworkDefaultEdit")
    object TimeworkAppStyle : TimeworkRouter("TimeworkAppStyle")
    object TimeworkAppTheme : TimeworkRouter("TimeworkAppTheme")
    object TimeworkDetailData : TimeworkRouter("TimeworkDetailData")
    object TimeworkBatchSettle : TimeworkRouter("TimeworkBatchSettle")
    object TimeworkCloudManage : TimeworkRouter("TimeworkCloudManage")
    object TimeworkShare : TimeworkRouter("TimeworkShare")
    object TimeworkProjectManage : TimeworkRouter("TimeworkProjectManage")
}
