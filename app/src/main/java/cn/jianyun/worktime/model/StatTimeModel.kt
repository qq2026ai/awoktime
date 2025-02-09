package cn.jianyun.worktime.model

data class StatTimeModel(
    val gmtBegin: String = "2024-05-01",
    val gmtEnd: String = "2024-05-30"
){

    fun isValid(): Boolean {
        return gmtBegin != "" && gmtEnd != ""
    }
}