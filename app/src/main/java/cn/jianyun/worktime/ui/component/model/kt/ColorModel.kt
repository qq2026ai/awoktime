package cn.jianyun.worktime.ui.component.model.kt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorModel(
    var name: String = "",
    val color: String = ""
):Parcelable