package cn.jianyun.worktime.views.base



import android.annotation.SuppressLint
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
import androidx.navigation.NavHostController
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.util.goBack


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserPolicy(navHostController: NavHostController) {

    Scaffold (content = {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)) {
            HeaderView(title= "用户使用协议", backAction = {
                goBack(navHostController)
            })
            Column(modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
            ) {
                UserPolicyView()
            }
        }
    })

}



val USER_POLICY_DATA = """
    欢迎下载并使用极简记工时APP,请您（用户）仔细阅读以下全部内容。如用户已勾选同意极简记工时用户使用协议，即表示用户与杭州简蕴科技有限公司已达成协议，并同意接受本协议各项协议的约束。

    1、版权归属
    杭州简蕴科技有限公司拥有极简记工时所有版权，任何人不得对极简记工时中的内容进行复制

    2、客户隐私制度
    极简记工时作为一款工具软件，用户数据默认只保留在用户手机本地，用户可以自行进行数据备份，同时我们也给会员用户提供了云备份，方便数据找回

    3、用户协议的修改
    极简记工时始终在不断优化和改进服务。极简记工时有权在必要时通过APP推送通知、弹窗提示等合理方式修改本用户协议及各单项服务的相关协议，修订后的服务协议条款在公示届满7日起生效。用户在享受各项服务时，应及时查阅了解修改的内容，并自觉遵守修改后的用户协议及各单项服务的相关协议。如用户继续使用APP，则视为客户已同意修改的内容，发生有关争议时，以最新的用户协议为准；如用户不同意修改内容，则应停止使用该项服务。

    4、服务的变更或中止
    极简记工时目前完全免费供大家使用，若用户不愿意使用，用户可自行卸载

    5、用户的账号、密码和安全性
    极简记工时目前提供账号注册和登录，该功能主要用于绑定会员，您的密码会加密存储，开发者后台也不会知道您的密码，所以忘记密码，无法找回，只能联系开发者重置

    6、知识产权
    极简记工时是由杭州简蕴科技有限公司独立完成设计、开发，任何人请不要对app进行反编译、破解，请尊重别人的劳动成果，如带有恶意破解并分享，杭州简蕴科技有限公司有权利发起诉讼

    7. 服务内容管辖与法律适用
    （1）本协议的成立、生效、履行、解释及纠纷解决等相关事宜，均适用中华人民共和国大陆地区法律（不包括冲突法）。
    （2）本协议签订地为中华人民共和国浙江省杭州市余杭区。
    （3）若您因使用极简记工时发生任何纠纷或争议，首先应友好协商解决；协商不成的，您同意将纠纷或争议提交本协议签订地有管辖权的人民法院管辖。
    （4）本协议所有条款的标题仅为阅读方便，本身并无实际涵义，不能作为本协议涵义解释的依据。
    （5）本协议条款无论因何种原因部分无效或不可执行，其余条款仍有效，对双方具有约束力。
    本协议有效期为1年。在有效期满前30天，如任何一方未向对方提出终止或变更协议的要求，本协议有效期自动顺延1年。上述顺延不受次数限制。

    8.用户协议的生效

    本协议版本创建日期为2024年02月04日，生效日期为2024年08月20日

    发布人：杭州简蕴科技有限公司
""".trimIndent()


@Composable
fun UserPolicyView(){
    Column {
        Text(USER_POLICY_DATA, color=MaterialTheme.colorScheme.primary)
    }
}