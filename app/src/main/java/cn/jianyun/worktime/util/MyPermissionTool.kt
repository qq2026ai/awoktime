package cn.jianyun.worktime.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build


object MyPermissionTool {


    fun toAppSettingPage(context: Context){
        val mIntent = Intent()
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.packageName, null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.setAction(Intent.ACTION_VIEW);
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.packageName);
        }
        context.startActivity(mIntent);
    }
}