package cn.jianyun.worktime.main.question

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.api.BaseApi
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.util.oneLine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class QuestionModel(
    var tag: String = "",
    var title: String = "",
    var content: String = "",
    var ordinal: Int = 0
)

@HiltViewModel
class QuestionViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val api: BaseApi
) : BaseViewModel() {


    var currentModule by mutableStateOf("")

    var questions = mutableListOf<QuestionModel>()

    init {
        reload()
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
            reloadData()
        }
    }

    override fun reloadData(dataChanged: Boolean) {

        if(questions.isEmpty()){
            //系统篇
            questions.add(QuestionModel("入门", "极简记工时APP适合哪些人用？", "如果你的薪水不是按月固定发放的，薪水是以小时计算的，或者是日结的，都非常适合，比如：工厂小时工，家教，培训教练，日结工等等"))
            questions.add(QuestionModel("入门", "极简记工时APP如何使用？", """
                第一步：添加薪水和加班薪水<br>
                第二步：添加补贴或扣款项<br>
                第三步：根据实际情况创建快捷打卡<br>
                第四步：首页快捷打卡，或手动工时打卡
            """.oneLine()))

//            questions.add(QuestionModel("入门", "计划师只有目前几个模块吗？", "计划师会不断地丰富模块，但前提是目前模块做的非常完善，开发者不会开发一些滥竽充数的模块，一定会让用户觉得这个好用才会继续探索新的模块"))
//            questions.add(QuestionModel("入门", "为什么要收费？", "由于开发APP需要耗费开发者大量的时间和精力，但为了让大家用到更好的使用我们的app，我们也是采用尽量低的价格，让用户觉得实惠"))
//            questions.add(QuestionModel("入门", "怎么在不同设备同步会员状态？", "你只要在新设备上登录同一个计划师账号，系统会自动为您同步会员状态，为了防止有些人共享账号，我们有对账号登录做限制，一个账号最多同时登录3个设备，如果频繁登录新设备，我们会对账号封号喔，会员权益也会丢失"))
            questions.add(QuestionModel("入门", "为什么薪水不支持修改？", "因为直接修改薪水金额，会影响历史打卡薪水统计，建议用户隐藏该薪水，然后添加一个新的薪水，这样既不妨碍你使用新的薪水打卡，也不影响历史薪水的统计，同时用户还可以看到自己的工时薪水不段的在变化，也是激励自己变得更好"))
            questions.add(QuestionModel("入门", "打卡工时可以修改吗？", "在主页点击日期后，底部有当天打卡详情，可以左滑打卡详情删除，如果想要修改，可以点击打卡详情（补贴也是同理）"))
            questions.add(QuestionModel("入门", "补贴和扣款怎么记合适？", "不同的人有不同的需求，如果你的补贴是按天发放的，那就以天为单位记录补贴和扣款，比如餐补，迟到扣款等，如果你是按月发放的，建议放在月末那一天，比如：全勤奖，季度奖，年终奖"))
            questions.add(QuestionModel("入门", "用户工时数据存储在哪里？", "用户工时数据仅保存在用户手机本地，开发者服务器不会存储用户工时数据"))
            questions.add(QuestionModel("入门", "为什么你们不做云备份？", "开发者认为只有数据掌握在用户自己手里，对用户才是负责任的，这样既保证了用户数据的安全性，也避免开发者服务器遭受攻击，导致用户无法访问APP，影响用户正常操作"))
            questions.add(QuestionModel("入门", "什么是秘钥校验？", "工时数据属于个人隐私，如果不想让别人查看，用户可以设置一个秘钥，每次访问APP，都要输入秘钥确认"))
            questions.add(QuestionModel("入门", "忘记秘钥了怎么办？", "为了防止用户忘记密码，我们特定只给了四位数密码，请用户设置秘钥后，最好将秘钥记录在某个地方，如果真的忘记了，可以找开发者，远程重置一下。<br>为了防止别人试你的密码，每输错一次，就要等一段时间才能重试，错误次数越多，等待时间越长，最长是24小时后才能解锁".oneLine()))

        }

    }

    fun initModel(module: String){
        if(inited){
            return
        }
        inited = true
        currentModule = module
    }

}