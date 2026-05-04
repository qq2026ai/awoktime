package cn.jianyun.worktime.module.timework.vm



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.uuid
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.toJSONString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkDefaultConfigViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    private val timeworkService: TimeworkService
) : BaseViewModel() {

    var appConfig by mutableStateOf(TimeworkAppConfigDTO())
    var editItem by mutableStateOf(TimeworkDefaultConfig())
    var datalist by mutableStateOf(listOf<TimeworkDefaultConfig>())
    var awardList by mutableStateOf(listOf<TimeworkAward>())

    var salarys by mutableStateOf(listOf<TimeworkSalary>())
    var editWorkItem by mutableStateOf(TimeworkData())
    var editAwardItem by mutableStateOf(TimeworkAwardData())

    init {
        reload()
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
            currentProjectId = timeworkService.getProjectId()
            datalist = timeworkService.listDefaultConfigByProject(currentProjectId)
            salarys = timeworkService.listSalaryByProject(currentProjectId).filter{it.shown}
            awardList = timeworkService.listAwardByProject(currentProjectId)
            appConfig = timeworkService.appConfigDao.get()
            mlog("reload data")
        }
    }

    override fun reloadData(dataChanged: Boolean) {
        TODO("Not yet implemented")
    }

    fun initModel(model: TimeworkDefaultConfig){
        if(inited){
            return
        }
        inited = true
        editItem = model

        if(model.config != ""){
            if(model.type == "sign"){
                editWorkItem = JSON.parseObject(model.config, TimeworkData::class.java)
            }
            else{
                editAwardItem = JSON.parseObject(model.config, TimeworkAwardData::class.java)
            }
        }
    }

    fun save(navHostController: NavHostController) {
        viewModelScope.launch {
            var msg = editItem.isValid()

            if(!isOk(msg)){
                baseRepository.toast(msg)
                return@launch
            }
            editItem.remark = editWorkItem.remark

            if(editItem.type == "sign"){
                msg = editWorkItem.isValid()
                if(!isOk(msg)) {
                    baseRepository.toast(msg)
                    return@launch
                }
                editItem.aliasName = editWorkItem.fetchAliasName()
                editItem.config = editWorkItem.toJSONString()
            }
            else{
                editAwardItem.awardType = editItem.type
                msg = editAwardItem.isValid()
                if(!isOk(msg)){
                    baseRepository.toast(msg)
                    return@launch
                }
                editItem.aliasName = editAwardItem.awardName + ":" + editAwardItem.awardValue + "元"
                editItem.config = editAwardItem.toJSONString()
            }

            if(isOk(msg)){
                //判断同名信息
                if(!datalist.filter{it.name == editItem.name && it.uuid != editItem.uuid}.isEmpty()){
                    baseRepository.toast( "存在相同名称")
                    return@launch
                }
                if(editItem.isAdd()){
                    editItem.gmtCreate = MyDateTool.toDateTimeString(Date())
                    editItem.uuid = uuid()
                    editItem.projectUuid = currentProjectId
                    editItem.ordinal = System.currentTimeMillis()
                    timeworkService.defaultConfigDao.insert(editItem)
                    baseRepository.toast("添加成功")
                }
                else {
                    timeworkService.defaultConfigDao.update(editItem)
                    reload()
                    baseRepository.toast("更新成功")
                }
                baseRepository.reload()
                formType = FormType()
                goBack(navHostController)
            }
            else{
                baseRepository.toast(msg)
            }
        }
    }

    fun doDelete(navHostController: NavHostController){
        viewModelScope.launch {
            timeworkService.defaultConfigDao.delete(editItem)
            resetForm()
            baseRepository.reload()
            reload()
            goBack(navHostController)
        }
    }

    fun doSort(newOrderList: List<String>) {

        viewModelScope.launch {
            datalist.forEach{
                val i = newOrderList.indexOf(it.uuid)
                var newItem = it
                newItem.ordinal = i.toLong()
                timeworkService.defaultConfigDao.update(newItem)
            }
            reload()
        }

    }

    fun doHide(navHostController: NavHostController) {
        viewModelScope.launch {
            editItem = editItem.copy(shown = !editItem.shown)
            timeworkService.defaultConfigDao.update(editItem)
            baseRepository.toast("操作成功")
            baseRepository.reload()
            reload()
            goBack(navHostController)
        }
    }

    fun checkSalarys(uuid: String): List<SelectDO>{
        if(salarys.find{it.uuid == uuid}?.shown == false) {
            return salarys.map{it.toSelect()}
        }
        return salarys.filter{it.shown}.map{it.toSelect()}
    }

}
