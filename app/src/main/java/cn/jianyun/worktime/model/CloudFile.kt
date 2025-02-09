package cn.jianyun.worktime.model


data class CloudFile(
    var name: String = "",
    var content: String
) {
    fun fileSize(): String {
        return "${content.length}"
    }
}