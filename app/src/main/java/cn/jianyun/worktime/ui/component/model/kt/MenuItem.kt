package cn.jianyun.worktime.ui.component.model.kt


data class MenuItem(
    var name: String = "",
    var icon: Int = 0,
    var action: () -> Unit
)