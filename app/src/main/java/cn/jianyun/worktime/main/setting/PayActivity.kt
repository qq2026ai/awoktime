//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.os.Handler
//import android.os.Message
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import cn.jianyun.worktime.api.ShareApi
//import cn.jianyun.worktime.hilt.respo.BaseRepository
//import cn.jianyun.worktime.util.mlog
//import cn.jianyun.worktime.ui.component.form.LoadingDialog
//import cn.jianyun.worktime.ui.theme.PlanTheme
//import com.alipay.sdk.app.PayTask
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.runBlocking
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class PayActivity : ComponentActivity() {
//
//    private val mHandler: Handler = @SuppressLint("HandlerLeak")
//    object : Handler() {
//        override fun handleMessage(msg: Message) {
//            mlog("pay info", msg.obj)
//        }
//    }
//
//    @Inject
//    lateinit var baseRepository: BaseRepository
//
//    @Inject
//    lateinit var shareApi: ShareApi
//
//    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mlog("has regist")
//
//        makePay()
//
//        setContent {
//            PlanTheme(darkTheme = isSystemInDarkTheme()) {
//                Scaffold(content = {
//                    VipView(this)
//                })
//            }
//        }
//    }
//
//    fun makePay(){
//        var payRunnable = Runnable {
//            val alipay = PayTask(this)
//            runBlocking {
//                val info = shareApi.sdkRequest("plan","android", baseRepository.getUid(), "vip")
//                if(info.success){
//                    val orderResult = info.getResult()
//                    val result = alipay.payV2(orderResult, true)
//                    shareApi.checkVip("plan", "android", baseRepository.getUid(), baseRepository.purchaseMode)
//                    val msg = Message()
//                    msg.what = 1
//                    msg.obj = result
//                    mHandler.sendMessage(msg)
//                }
//            }
//        }
//
//        val payThread = Thread(payRunnable)
//        payThread.start()
//    }
//
//}
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun VipView(activity: PayActivity) {
//
//    val context = LocalContext.current
//
//    Box(modifier= Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        LoadingDialog("正在拉起支付")
//    }
//}