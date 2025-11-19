package cn.jianyun.worktime.views.base




import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.ui.component.nav.HeaderView

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PrivatePolicy(navHostController: NavHostController) {
    Scaffold (content = {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)) {
            HeaderView(title= "App隐私政策", backAction = {
                goBack(navHostController)
            })
            Column(modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
            ) {
                PrivatePolicyView()
            }
        }
    })

}

val PRIVATE_DATA = """
App 名称：极简记工时
开发者名称：杭州简蕴科技有限公司
注册地址：浙江省杭州市西湖区留下街道西溪路698号14号楼307-2室
联系方式：1009510944@qq.com
信息保护负责人联系电话：15068790467

生效日期：2024-08-20
更新日期：2024-08-20

感谢使用本产品。我们非常重视用户的隐私保护，极简记工时绝不会在未经允许的情景下收集您的任何隐私内容。本文尽可能简短，强烈建议您认真阅读本文。
如果您不同意以下隐私政策，请立即停止使用或访问我们的产品和服务。若您对其有任何问题，请联系：1009510944@qq.com

本文将从以下方面逐一介绍：
1.权限列表及用途
2.收集的数据
3.不会收集的数据
4.收集数据的使用规则
5.个人信息的查询、更正、删除
6.对未成年人的保护
7.隐私政策的更改
8.第三方 SDK


一：权限列表及用途

(1) 获取本地文件存储权限，用于保存相关工时数据；

(2) 日历读写和访问权限，用于课程提醒；

(3) 网络权限，用于工时数据导入；

(4) 提醒权限，用于定时更新小组件；


二：我们收集的数据

(1).应用崩溃时产生的日志信息，以便于我们定位 bug 和改善应用健壮性。

三: 我们不会收集的数据

App主要是做一个本地记工时工具，不会收集用户手机号、地理位置、ip地址等无关的用户隐私数据，我们也不会给用户发送任何短信验证码

四: 我们收集数据的使用规则

(1). 我们可能会收集和使用您的个人信息，以提供更好的服务。在此，我们承诺合法、合理、必要的原则，维护您的个人隐私。我们收集的个人信息范围仅限于与提供服务相关的信息，并且将遵循最小化原则，仅在必要时收集必要的信息。个人信息收集范围、方式、以及目的在本隐私政策中明确说明，不会超出明示的目的范围使用。
(2). 我们承诺，在未经您的明示同意的情况下，不会向任何第三方共享、转让您的个人数据。
(3). 我们将数据（包括个人数据）传输到中华人民共和国境内的服务器并在中华人民共和国境内进行处理和存储。
(4). 我们将采取一切合理可行的措施，包括但不限于使用HTTPS协议严格加密传输您的数据，保护您的个人信息安全，防止数据丢失、被滥用或遭未授权访问、披露、更改或破坏。

五: 个人信息的查询、更正、删除

(1). 默认情况下我们仅在手机本地存储了您的工时数据，这其中不涉及您的个人信息，这些数据会随着 App 的卸载而被删除。
(2). App 没有账号功能，因此不需要注销账号。
(3).如果您想删除您的数据，只需卸载本 App。

六: 对未成年人的保护

(1). 我们非常重视对未成年人个人信息的保护。根据相关法律法规的规定，收集、使用未满14周岁的未成年人的个人信息，需由监护人授权同意；收集、使用已满 14 周岁未满 18 周岁的未成年人个人信息，可由监护人授权同意或自行授权同意。
(2). 如您为未成年人（尤其是不满 14 周岁的未成年人），我们要求您请您的父母或其他监护人仔细阅读本隐私政策，并在征得您的监护人授权同意的前提下使用我们的服务或向我们提供信息。
(3). 如您是未成年人的监护人，请您关注您所监护的未成年人是否是在您授权同意之后使用我们的产品或服务。如果您对您所监护的未成年人的个人信息有疑问，请通过本隐私政策载明的方式与我们联系。

七: 隐私政策的更改

(1). 如果决定更改隐私政策，我们会在本政策中、网站中、App 中以及我们认为适当的位置发布这些更改，以便您了解我们收集使用个人信息的目的、方式、范围发生的变化。
(2). 我们可能会不定期修改和更新本隐私政策，您可以随时访问本页面查询最新版本的隐私政策。

八: 第三方SDK

暂无


""".trimIndent()

@Composable
fun PrivatePolicyView(){
    Column {
        Text(PRIVATE_DATA, color = MaterialTheme.colorScheme.primary)
    }
}
