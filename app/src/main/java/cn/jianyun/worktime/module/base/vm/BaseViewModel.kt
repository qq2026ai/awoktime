package cn.jianyun.worktime.module.base.vm


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.util.mlog

abstract class BaseViewModel: ViewModel() {


    var currentPage by mutableStateOf("")

    var currentProjectId by mutableStateOf("default")
    var inited by mutableStateOf(false)
    var oldSid by mutableStateOf(0)
    var formType: FormType by mutableStateOf(FormType(type=""))
    var isEmpty by mutableStateOf(true)

    abstract fun getRepository(): BaseRepository
    fun tryReload(key: String = ""){
        currentPage = key
        var flag = getRepository().isNeedReload(currentPage, oldSid)
        mlog("try reload page", currentPage, getRepository().currentPage,  flag)
        if(flag) {
            oldSid = getRepository().sid
            reload()
        }
    }

    abstract fun reload()

    abstract fun reloadData(dataChanged: Boolean = false)

    fun toast(msg: String) {
        getRepository().toast(msg)
    }

    fun resetForm() {
        formType = FormType()
    }
}