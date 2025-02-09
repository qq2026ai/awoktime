package cn.jianyun.worktime.hilt.respo

import android.os.Parcelable
import cn.jianyun.worktime.util.MyDateTool
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegistModel(
    var registTime: Long = 0
): Parcelable {

    fun isRegist(): Boolean{
        return registTime > 0
    }
}