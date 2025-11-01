package cn.jianyun.worktime.main.setting.local


import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.model.LocalBackupConfig
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.LinkText
import cn.jianyun.worktime.util.MyFileUtil
import cn.jianyun.worktime.util.oneLine
import cn.jianyun.worktime.ui.component.form.ConfirmDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.component.form.InputNumberView
import cn.jianyun.worktime.ui.component.form.LongCancelButton
import cn.jianyun.worktime.ui.component.form.SettingGroupView
import cn.jianyun.worktime.ui.component.form.SwitchItemView
import cn.jianyun.worktime.ui.component.nav.CenterSmallTipText
import cn.jianyun.worktime.ui.component.nav.LeadingHintView
import cn.jianyun.worktime.ui.component.nav.PageView
import cn.jianyun.worktime.ui.theme.ThemeColor
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import kotlinx.coroutines.launch

@Composable
fun LocalBackupView(navHostController: NavHostController) {
    val viewModel = hiltViewModel<LocalBackupViewModel>()
    viewModel.tryReload()
    PageView(navHostController = navHostController, title = "本地备份") {
        if(viewModel.localBackupConfig.path != ""){
            LeadingHintView("本地备份目录")
            SettingGroupView(horizonPadding = 15.dp, verticalPadding = 15.dp) {
                Text(MyFileUtil.readDirRelativePath(viewModel.localPath))
            }

//            LinkText(label = "恭喜，你已经成功设置好本地备份目录，即便app卸载，你依然可以从此目录恢复数据，为了避免本地备份文件占用过多本地存储，你可以设置是否自动备份，以及每个功能模块下的备份文件数据，超过指定数量，历史备份会自动删除喔")
            LinkText(label = "恭喜，你已经成功设置好本地备份目录，即便app卸载，你依然可以从此目录恢复数据")
            Blank()
            Blank()

//            GroupView {
//                SwitchItemView(label = "自动备份", value = viewModel.localBackupConfig.autoBackup, onValueChange = {
//                    viewModel.localBackupConfig = viewModel.localBackupConfig.copy(autoBackup = it)
//                    viewModel.saveLocalInfo()
//                })
//                InputNumberView(label = "最多保留备份文件数量", minValue=1, maxValue=50, unit = "个", value = viewModel.localBackupConfig.autoFile, onValueChange = {
//                    viewModel.localBackupConfig = viewModel.localBackupConfig.copy(autoFile = it)
//                    viewModel.saveLocalInfo()
//                })
//            }

//            LongOkButton(label= "全量备份") {
//                viewModel.saveAll()
//            }

            CenterSmallTipText(text = "切换备份目录", color = ThemeColor) {
                viewModel.formType = FormType("toggleTip")
            }

//            LongCancelButton("清空备份目录") {
//                viewModel.localPath = ""
//                viewModel.localBackupConfig = LocalBackupConfig(path="")
//                viewModel.saveLocalInfo()
//            }
        }
        else{

            GroupView(horizonPadding = 15.dp, verticalPadding = 15.dp) {
                Text("""
                    选择本地备份有以下几个好处：
                    """.oneLine())
                Text("""
                    1.万一APP不小心被自己或他人卸载，依然可以从本地备份目录找回数据<br> 
                    2.如果APP升级因为某种莫名的原因导致APP崩溃，无法打开APP，这个时候可以卸载APP，重新安装最新APP，依然可以找回历史数据<br>
                    3.买了新手机，可以通过本地备份还原旧手机数据""".oneLine(), fontSize = 13.sp)
            }

            LongCancelButton("选择备份目录") {
                viewModel.formType = FormType("pickDir")
            }
        }

        if(viewModel.formType.isForm("toggleTip")){
            ConfirmDialog(title = "真的要切换吗？这样会导致历史备份目录下备份文件失效喔", okAction = {
                viewModel.formType = FormType("pickDir")
            }) {
                viewModel.resetForm()
            }
        }

        DirectoryPicker(show = viewModel.formType.isForm("pickDir")) { path ->
            viewModel.formType = FormType()
            if (path != null) {
                viewModel.viewModelScope.launch {
                    val takeFlags: Int = (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    viewModel.baseRepository.context.contentResolver.takePersistableUriPermission(Uri.parse(path), takeFlags)
                    viewModel.localBackupConfig = LocalBackupConfig(path=path, autoBackup = true, autoFile = "10")
                    viewModel.localPath = path
                    viewModel.saveLocalInfo()
                }
            }
        }
    }

//


}