package cn.jianyun.worktime.module.base.model


open class BaseRoomModel(
    open var uuid:String = "",
) {

    fun isAdd(): Boolean{
        return uuid == ""
    }

    open fun isValid():String {
        throw Exception("异常")
    }
}