package cn.jianyun.worktime.module.timework.vm




import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.dao.TimeworkDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDefaultConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkSalaryDao
import cn.jianyun.worktime.module.timework.model.TimeworkSalary
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.uuid
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.mlog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkSalaryViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val timeworkService: TimeworkService
) : BaseViewModel() {

    var isDelete by mutableStateOf(false)
    var editItem by mutableStateOf(TimeworkSalary())
    var datalist by mutableStateOf(listOf<TimeworkSalary>())
    var showAll by mutableStateOf(false)

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
            datalist = timeworkService.listSalaryByProject(currentProjectId)

            //判断是否有变更过
            datalist.forEach {
                it.showValue = it.makeShowValue(datalist)
            }

        }
    }

    override fun reloadData(dataChanged: Boolean) {
        TODO("Not yet implemented")
    }

    fun getBaseSalarys(): List<SelectDO> {
        return datalist.filter{it.type == "normal"}.map{it.toBaseSelect()}
    }

    fun initModel(model: TimeworkSalary){
        if(inited){
            return
        }
        inited = true
        editItem = model
    }

    fun save(navHostController: NavHostController) {
        viewModelScope.launch {
            val msg = editItem.isValid()
            //计算显示内容
            editItem.showValue = editItem.makeShowValue(datalist)
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
                    mlog("salary", editItem)
                    timeworkService.salaryDao.insert(editItem)
                    baseRepository.toast("添加成功")
                }
                else {
                    timeworkService.salaryDao.update(editItem)
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

    suspend fun checkSalaryUsed(salary: TimeworkSalary):String {

        if(timeworkService.dataDao.findSalary(salary.uuid) > 0){
            return "该薪水已被添加到工时打卡，无法删除"
        }
        if(timeworkService.defaultConfigDao.findSalary(salary.uuid) > 0){
            return "该薪水已被用于快捷打卡，无法删除"
        }
        if(timeworkService.salaryDao.findSalary(salary.uuid) > 0){
            return "该薪水已被其他薪水引用，无法删除"
        }
        return "ok"

    }

    fun doDelete(navHostController: NavHostController){
        viewModelScope.launch {
            //判断该薪水是否被使用
            val msg = checkSalaryUsed(editItem)
            if(!isOk(msg)) {
                baseRepository.toast(msg)
                resetForm()
                return@launch
            }
            timeworkService.salaryDao.delete(editItem)
            isDelete = false
            resetForm()
            baseRepository.reload()
            reload()
            goBack(navHostController)
        }
    }

    fun hide(navHostController: NavHostController) {
        viewModelScope.launch {
            editItem.shown = !editItem.shown
            timeworkService.salaryDao.update(editItem)
            baseRepository.toast("操作成功")
            baseRepository.reload()
            goBack(navHostController)
        }
    }

    fun doSort(newOrderList: List<String>) {

        viewModelScope.launch {
            datalist.forEach{
                val i = newOrderList.indexOf(it.uuid)
                var newItem = it
                newItem.ordinal = i.toLong()
                timeworkService.salaryDao.update(newItem)
            }
            reload()
        }

    }


}