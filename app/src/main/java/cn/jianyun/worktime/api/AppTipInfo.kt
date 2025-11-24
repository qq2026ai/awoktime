package cn.jianyun.worktime.api

import androidx.compose.ui.graphics.Color
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.MyEncryptTool

data class AppTipInfo(
    var app: String = "",
    var name: String = "",
    var platform: String = "",
    var message: String = "",
    var type: String = "",
    var state: String = "",
    var top: Int = 0,
    var url: String = "",
    var vip: Int = 0,
    var title: String = "",
    var closed: Int = 0,
    var maxDay: Int = 0,
    var minDay: Int = 0,
    var minVipDay: Int = 30,
    var newPage: String = "",
    var minIOS: Int = 0,
    var maxIOS: Int = 0,
    var discount: Boolean = false,
    var showPurchase: Boolean = false,
    var testMode: Int = -1,
    var forceRate: Int = 0,
    var rateNumber: Int = 30,
    var minVersion: String = "",
    var maxVersion: String = "",
    var tipTime: Long = 0,
    var helpInfo: String = "",
    var helpFlag: String = "",
    var helpNumber: String = "",
    var isShow: Boolean = false,
    var openScreenAds: Boolean = true,
    var screenAdsGapMinute: Int = 2,
    var showWx: Boolean = false,
    var showShare: Boolean = false,
    var shareEvent: Boolean = false
) {

    fun showColor(): Color {
        val c = when (type) {
            "default" -> ThemeColor // 需要定义这些颜色
            "primary" -> ThemeColor
            else -> Color.Red
        }
        return c
    }


    fun uid(): String {
        if (message.isEmpty()) {
            return  "none"
        } else {
            return "n_" + MyEncryptTool.md5(message) // 需要实现md5扩展函数
        }
    }

    suspend fun isShown(baseRepository: BaseRepository): Boolean {
        val registDay = baseRepository.getRegistDay()
        if (registDay < minDay || registDay > maxDay) {
            return false
        }
        val isVip = baseRepository.isVip()
        if (vip >= 0) {
            if ((vip == 1) != isVip) {
                return false
            }
        }
        return true
    }


}
