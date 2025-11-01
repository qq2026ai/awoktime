package cn.jianyun.worktime.module.timework.vm


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.uuid
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.mlog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkAwardViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val timeworkService: TimeworkService
) : BaseViewModel() {

    val commonAwards = listOf("餐补", "交通补贴", "话费补贴", "住宿补贴", "高温补贴", "油费补贴", "夜班补贴", "出差补贴", "全勤奖", "工龄补贴")
    val commonFines = listOf("旷工", "住宿", "迟到", "电费", "交税", "病假", "事假", "租车")

    var isDelete by mutableStateOf(false)
    var editItem by mutableStateOf(TimeworkAward())
    var datalist by mutableStateOf(listOf<TimeworkAward>())
    var showAll by mutableStateOf(false)

    init {
        mlog("initAward")
        reload()
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
            currentProjectId = timeworkService.getProjectId()
            datalist = timeworkService.listAwardByProject(currentProjectId)
            mlog("reload data")
        }
    }

    override fun reloadData(dataChanged: Boolean) {

    }

    fun initModel(model: TimeworkAward){
        if(inited){
            return
        }
        inited = true
        editItem = model
    }


    fun save(navHostController: NavHostController) {
        viewModelScope.launch {
            val msg = editItem.isValid()
            if(isOk(msg)){
                if(!datalist.filter{it.name == editItem.name && it.uuid != editItem.uuid}.isEmpty()){
                    baseRepository.toast( "存在相同名称")
                    return@launch
                }
                if(editItem.isAdd()){
                    editItem.gmtCreate = MyDateTool.toDateTimeString(Date())
                    editItem.uuid = uuid()
                    editItem.projectUuid = currentProjectId
                    editItem.ordinal = System.currentTimeMillis()
                    timeworkService.awardDao.insert(editItem)
                    baseRepository.toast("添加成功")
                }
                else {
                    timeworkService.awardDao.update(editItem)
                    reload()
                    baseRepository.toast("编辑成功")
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


    suspend fun checkAwardUsed(data: TimeworkAward):String {
        if(timeworkService.defaultConfigDao.findSalary(data.uuid) > 0){
            return "该补扣已被用于快捷打卡，无法删除，建议隐藏"
        }
        if(timeworkService.awardDataDao.findAward(data.uuid) > 0){
            return "该补扣已被用于打卡，无法删除，建议隐藏"
        }
        return "ok"

    }

    fun doDelete(navHostController: NavHostController){
        viewModelScope.launch {
            val msg = checkAwardUsed(editItem)
            if(!isOk(msg)){
                baseRepository.toast(msg)
                return@launch
            }
            timeworkService.awardDao.delete(editItem)
            isDelete = false
            resetForm()
            baseRepository.reload()
            reload()
            goBack(navHostController)
        }
    }

    fun doDeleteOne(item: TimeworkAward){
        viewModelScope.launch {
            //判断是否被使用
            val msg = checkAwardUsed(item)
            if(!isOk(msg)){
                baseRepository.toast(msg)
                baseRepository.reload()
                return@launch
            }
            timeworkService.awardDao.delete(item)
            resetForm()
            baseRepository.reload()
            reload()
        }
    }


    fun doSort(newOrderList: List<String>) {
        viewModelScope.launch {
            datalist.forEach{
                val i = newOrderList.indexOf(it.uuid)
                var newItem = it
                newItem.ordinal = i.toLong()
                timeworkService.awardDao.update(newItem)
            }
            reload()
        }
    }

    fun doHide(navHostController: NavHostController) {
        viewModelScope.launch {
            editItem.shown = !editItem.shown
            timeworkService.awardDao.update(editItem)
            baseRepository.reload()
            goBack(navHostController)
        }
    }


}