package cn.jianyun.worktime.module.timework.vm



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.api.ShareApi
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.withApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportDataViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    private val service: TimeworkService,
    val api: ShareApi
) : BaseViewModel() {

    var userId by mutableStateOf("")
    init {
        reload()
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
        }
    }

    override fun reloadData(dataChanged: Boolean) {
        TODO("Not yet implemented")
    }

    fun makeImport(navHostController: NavHostController) {
        viewModelScope.launch {
            baseRepository.loading()
            if(userId.length != 8){
                baseRepository.toast("不存在此用户ID")
            }
            else{
                var all = withApi {
                    api.fetchWechatData(userId)
                }
                mlog(all)
                baseRepository.finish()
                if(all.success && all.result != null){
                    var data = all.fetchResult()
                    service.makeWechatImport(data)
                    baseRepository.reload()
                    baseRepository.toast("导入成功")
                    goBack(navHostController)
                }
                else{
                    baseRepository.toast("导入失败，请联系开发者处理")
                }
            }
        }
    }


}