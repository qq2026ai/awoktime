package cn.jianyun.worktime.util


import android.os.Build
import java.util.Locale

/**
 * 工具类参考 https://blog.csdn.net/convex1009848621/article/details/120767000
 */
object MyPhoneTool {
    /**
     * 设备名称
     *
     * @return 设备名称
     */
    fun getDeviceName(): String {
        try{
            return Build.DEVICE
        }
        catch (e: Exception){
            return ""
        }
    }

    /**
     * 设备型号
     *
     * @return 设备型号
     */
    fun getModelName(): String {
        try{
            return Build.MODEL
        }
        catch (e: Exception){
            return ""
        }
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    fun getSystemVersion(): String {
        try{
            return Build.VERSION.RELEASE
        }
        catch (e: Exception){
            return ""
        }
    }

    /**
     * 获取厂商
     *
     * @return 厂商
     */
    fun getBrand(): String {
        try{
            return Build.BRAND
        }
        catch (e: Exception){
            return ""
        }
    }

    /**
     * 获取设备制造商
     *
     * @return 制造商
     */
    fun getManufacturer(): String {
        try{
            return Build.MANUFACTURER
        }
        catch (e: Exception){
            return ""
        }
    }

    /**
     * SDK 版本
     * @return
     */
    fun getSDKVersion(): String {
        try{
            return Build.VERSION.SDK
        }
        catch (e: Exception){
            return ""
        }
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    fun getSystemLanguage(): String {
        try{
            return Locale.getDefault().language
        }
        catch (e: Exception){
            return ""
        }
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    fun getSystemLanguageList(): Array<Locale?>? {
        return Locale.getAvailableLocales()
    }

    fun getDid(): String {
        return getBrand() + getModelName() + getSystemVersion() + getSDKVersion()
    }


}
