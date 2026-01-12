package cn.jianyun.worktime.module.timework.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import cn.jianyun.worktime.module.timework.dao.TimeworkProjectDao
import cn.jianyun.worktime.module.timework.model.TimeworkProject
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.MyDateTool
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.uuid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeworkProjectViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val timeworkService: TimeworkService
) : BaseViewModel() {

    var editItem by mutableStateOf(TimeworkProject())
    var datalist by mutableStateOf(listOf<TimeworkProject>())

    var sizeMap by mutableStateOf(mutableMapOf<String, Int>())

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
            datalist = timeworkService.listProject()

            datalist.forEach {
                val size = timeworkService.dataDao.sizeByProject(it.uuid)
                sizeMap.put(it.uuid, size)
            }

        }
    }

    override fun reloadData(dataChanged: Boolean) {
        TODO("Not yet implemented")
    }

    fun initModel(model: TimeworkProject){
        if(inited){
            return
        }
        inited = true
        editItem = model
    }

    fun save() {
        viewModelScope.launch {
            val msg = editItem.isValid()
            if(isOk(msg)){
                if(editItem.isAdd()){
                    editItem.gmtCreate = MyDateTool.toDateTimeString(Date())
                    editItem.uuid = uuid()
                    timeworkService.projectDao.insert(editItem)
                    reload()
                    baseRepository.toast("添加成功")
                }
                else {
                    timeworkService.projectDao.update(editItem)
                    reload()
                    baseRepository.toast("编辑成功")
                }
                baseRepository.reload()
                resetForm()
            }
            else{
                baseRepository.toast(msg)
            }
        }
    }

    fun doDelete(){
        viewModelScope.launch {
            timeworkService.projectDao.delete(editItem)
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
                timeworkService.projectDao.update(newItem)
            }
            reload()
        }
    }

    fun makeCurrentProject() {
        viewModelScope.launch {
            baseRepository.loading()
            timeworkService.setProjectId(editItem.uuid)
            currentProjectId = editItem.uuid
            resetForm()
            baseRepository.reload()
        }
    }

    fun doClear() {
        viewModelScope.launch {
            datalist.forEach {
                if(it.uuid != currentProjectId) {
                    timeworkService.projectDao.delete(it)
                }
            }
            reload()
        }
    }

}


