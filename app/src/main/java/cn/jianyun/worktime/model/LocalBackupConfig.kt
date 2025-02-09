package cn.jianyun.worktime.model



data class LocalBackupConfig(
    var path: String = "",
    var autoBackup: Boolean = false,
    var autoFile: String = "10"
)