package cn.jianyun.worktime.module.base.views.webdav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.module.base.vm.WebDAVUserViewModel
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.oneLine
import cn.jianyun.worktime.ui.component.form.BottomDialogView
import cn.jianyun.worktime.ui.component.form.LongOkButton
import cn.jianyun.worktime.ui.component.nav.PanelView

@Composable
fun WebDAVUserDocView(viewModel: WebDAVUserViewModel){

    val uriHandler = LocalUriHandler.current

    BottomDialogView(title = "云备份使用说明", onDismiss = {
        viewModel.resetForm()
    }) {

        PanelView("为什么需要云备份？", """
           云备份可以帮助用户实现多设备数据同步，同时也可以防止手机损坏或者丢失导致数据无法找回，
        """.oneLine(), dialog = true)
        PanelView("云备份安全吗？", """
           我们的云备份是将数据保存在用户自己的网盘空间，开发者不会将用户数据上传至开发者服务器，所以这个对于用户来说是比较安全的
        """.oneLine(), dialog = true)
        PanelView("云备份和本地备份区别在哪？", """
           本地备份最大的优点无疑是加载速度快，用户日常的操作都是通过本地备份来实现的，开发者会在合适的时机将用户数据上传至用户个人的网盘空间（前提得先配置云备份账号）
        """.oneLine(), dialog = true)

        PanelView("为什么推荐使用坚果云？", """
            在中国，坚果云独树一帜，提供业界领先的免费WebDAV协议访问服务。这一服务使得用户能够享受到更为便捷、高效的云端文件管理体验。WebDAV协议的兼容性和灵活性，让用户无论身处何地，都能轻松同步和共享文件，如同使用本地存储设备一样自如。
            坚果云虽然免费，但是它有上传和下载容量限制，如果觉得容量不足，也可以选择其他几种云存储服务
        """.oneLine(), dialog = true)

        PanelView("为什么不支持百度网盘？", """
            因为它不支持WebDAV协议访问服务
        """.oneLine(), dialog = true)

        PanelView("什么是Koofr？",  dialog = true) {
            Text("""
           Koofr本身是一个网盘，同时还支持绑定第三方网盘，并且它向用户免费提供WebDAV协议访问服务，
           使得用户可以直接通过它来对第三方网盘进行读取和写入文件，比如微软OneDrive,谷歌GoogleDrive,Dropbox，(我们将来支持对这些平台的访问）
           Koofr自己也向用户提供10GB的存储空间，只是这个平台访问速度比较慢，但是不影响我们数据的云备份
        """.oneLine(), fontSize = 13.sp)
            Blank()
            LongOkButton("注册koofr账号") {
                uriHandler.openUri("https://k00.fr/m8v2gygx")
            }
            Blank()
        }

//        PanelView("什么是Koofr？",  dialog = true) {
//            Text("""
//           Koofr本身是一个网盘，同时还支持绑定第三方网盘，并且它向用户免费提供WebDAV协议访问服务，
//           使得用户可以直接通过它来对第三方网盘进行读取和写入文件，比如微软OneDrive,谷歌GoogleDrive,Dropbox，
//           Koofr自己也向用户提供10GB的存储空间，只是这个平台访问速度比较慢，但是不影响我们数据的云备份
//        """.oneLine(), fontSize = 13.sp)
//            Blank()
//            LongOkButton("注册koofr账号") {
//                uriHandler.openUri("https://k00.fr/m8v2gygx")
//            }
//            Blank()
//        }

//        PanelView("如何绑定微软OneDrive空间？", """
//            由于微软OneDrive空间并没有直接向个人提供WebDAV协议访问服务，但是用户可以借用Koofr平台，先将OneDrive绑定到Koofr,然后通过Koofr免费提供的webDAV协议向OneDrive读取和写入文件
//        """.oneLine(), dialog = true)
//
//        PanelView("如何绑定GoogleDrive空间？", """
//           由于GoogleDrive空间并没有直接向个人提供WebDAV协议访问服务，但是用户可以借用Koofr平台，先将GoogleDrive绑定到Koofr,然后通过Koofr免费提供的webDAV协议向GoogleDrive读取和写入文件
//        """.oneLine(), dialog = true)

    }

}