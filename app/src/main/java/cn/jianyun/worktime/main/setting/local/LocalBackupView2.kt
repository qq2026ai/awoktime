//package cn.jianyun.worktime.main.setting.local
//
//
//import android.content.Intent
//import android.net.Uri
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.navigation.NavHostController
//import cn.jianyun.worktime.model.FormType
//import cn.jianyun.worktime.util.Blank
//import cn.jianyun.worktime.util.LinkText
//import cn.jianyun.worktime.util.MyFileUtil
//import cn.jianyun.worktime.util.mlog
//import cn.jianyun.worktime.ui.component.form.ConfirmDialog
//import cn.jianyun.worktime.ui.component.form.GroupView
//import cn.jianyun.worktime.ui.component.form.LongCancelButton
//import cn.jianyun.worktime.ui.component.form.LongDeleteButton
//import cn.jianyun.worktime.ui.component.form.SettingGroupView
//import cn.jianyun.worktime.ui.component.form.SwitchItemView
//import cn.jianyun.worktime.ui.component.nav.LeadingHintView
//import cn.jianyun.worktime.ui.component.nav.PageView
//import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun LocalBackupView(navHostController: NavHostController) {
//    val viewModel = hiltViewModel<LocalBackupViewModel>()
//    viewModel.tryReload()
//
////    var filePermission = rememberMultiplePermissionsState(
////        permissions = listOf(
////            android.Manifest.permission.READ_EXTERNAL_STORAGE,
////            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
////        )
////    )
//
//
////    fun checkPermission(context: Context): Boolean {
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
////            return Environment.isExternalStorageManager()
////        } else {
////            val readFile = ContextCompat.checkSelfPermission(
////                context,
////                android.Manifest.permission.READ_EXTERNAL_STORAGE
////            ) == PackageManager.PERMISSION_GRANTED
////            val writeFile = ContextCompat.checkSelfPermission(
////                context,
////                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
////            ) == PackageManager.PERMISSION_GRANTED
////            return readFile && writeFile
////        }
////    }
////
////    fun requestPermission(context: Context) {
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
////            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
////            intent.addCategory("android.intent.category.DEFAULT")
////            intent.data = Uri.parse("package:${context.packageName}")
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
////            context.startActivity(intent)
////        } else {
////            filePermission.launchMultiplePermissionRequest()
////        }
////    }
//
//    PageView(navHostController = navHostController, title = "本地备份") {
//
////        Text(filePermission.permissions.get(0).status.toString())
////
////
////        if (checkPermission(context = viewModel.baseRepository.context)) {
////            LongCancelButton("写文件") {
////
////            }
////            LongCancelButton("读文件") {
////
////            }
////            LongCancelButton("创建目录") {
////
////            }
////        } else {
////            LongOkButton("授权访问外部文件") {
////                requestPermission(viewModel.baseRepository.context)
////            }
////        }
//
////        LongOkButton("立即备份") {
////            viewModel.backupAll()
////        }
//
////        LongCancelButton("选择文件") {
////            viewModel.loadFile()
////        }
//
//        if(viewModel.localPath != ""){
//            LeadingHintView("本地备份目录")
//            SettingGroupView(horizonPadding = 15.dp, verticalPadding = 15.dp) {
//                Text(MyFileUtil.readDirRelativePath(viewModel.localPath))
//            }
//
//            LinkText(label = "恭喜，你已经成功设置好本地备份目录，即便app卸载，你依然可以从此目录恢复数据")
//            Blank()
//            Blank()
//
//
//            GroupView {
//                SwitchItemView(label = "自动备份", value = , onValueChange = )
//            }
//
//            LongDeleteButton("切换备份目录") {
//                viewModel.formType = FormType("toggleTip")
//            }
//        }
//        else{
//            LongCancelButton("选择备份目录") {
//                viewModel.formType = FormType("pickDir")
//            }
//        }
//
//        if(viewModel.formType.isForm("toggleTip")){
//            ConfirmDialog(title = "真的要切换吗？这样会导致历史备份目录下备份文件失效喔", okAction = {
//                viewModel.formType = FormType("pickDir")
//            }) {
//                viewModel.resetForm()
//            }
//        }
//
////export https_proxy=http://127.0.0.1:55754 http_proxy=http://127.0.0.1:55754 all_proxy=socks5://127.0.0.1:55754
//
////        var showFilePicker by remember { mutableStateOf(false) }
//
////        FilePicker(
////            show = viewModel.formType.isForm("pickFile"),
////            fileExtensions = listOf("jpg", "png")
////        ) { platformFile ->
////            viewModel.formType = FormType()
////            // do something with the file
////        }
//
//        DirectoryPicker(show = viewModel.formType.isForm("pickDir")) { platformFile ->
//            mlog("path", platformFile ?: "")
//            viewModel.formType = FormType()
//
////            val folder =
////                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//            // geeksData represent the file data that is saved publicly
//            if (platformFile != null) {
//
//
//                viewModel.viewModelScope.launch {
//
//                    val takeFlags: Int = (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                    viewModel.baseRepository.context.contentResolver.takePersistableUriPermission(Uri.parse(platformFile), takeFlags)
//
////                    viewModel.baseRepository.context.contentResolver.persistedUriPermissions
//
//                    viewModel.changeLocalPath(platformFile)
//                    delay(200)
//                    viewModel.passwordService.makeLocalBackup()
//                }
//
////                MyFileUtil.writeFile(context = viewModel.baseRepository.context, platformFile, "hello/uu2.txt", "hello")
//
//            }
//
//            // do something with the file
//        }
//    }
//
////
//
//
//}