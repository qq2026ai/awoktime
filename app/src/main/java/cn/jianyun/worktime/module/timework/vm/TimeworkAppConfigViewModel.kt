package cn.jianyun.worktime.module.timework.vm




import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.timework.dao.TimeworkAppConfigDao
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.mlog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkAppConfigViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    private val dao: TimeworkAppConfigDao
) : ViewModel() {

    var formType: FormType by mutableStateOf(FormType(type=""))
    var isDelete by mutableStateOf(false)
    var editItem by mutableStateOf(TimeworkAppConfigDTO())
    var oldSid by mutableStateOf(0)
    var inited by mutableStateOf(false)

    init {
        reload()
    }

    fun tryReload(){
        mlog("try reload",oldSid, baseRepository.sid, oldSid != baseRepository.sid)
        if(oldSid != baseRepository.sid) {
            reload()
        }
    }

    fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
            editItem = dao.get()
            mlog("reload data")
        }
    }

    fun save(ok: () -> Unit) {
        viewModelScope.launch {
            editItem.gmtCreate = MyDateTool.toDateTimeString(Date())
            dao.set(editItem.toConfig())
            baseRepository.toast("设置成功")
            baseRepository.reload()
            formType = FormType()
            ok()
        }
    }

    fun justSave() {
        viewModelScope.launch {
            editItem.gmtCreate = MyDateTool.toDateTimeString(Date())
            dao.set(editItem.toConfig())
//            baseService.makeNotify()
            baseRepository.reload()
        }
    }

}