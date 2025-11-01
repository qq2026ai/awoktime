package cn.jianyun.worktime.module.base.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.api.ApiResult
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.main.base.model.BasicCloudData
import cn.jianyun.worktime.main.base.service.BaseService
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.model.LocalBackupConfig
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.util.mlog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseCloudViewModel: ViewModel() {

    var inited by mutableStateOf(false)
    var oldSid by mutableStateOf(0)
    var datalist by mutableStateOf(listOf<BasicCloudData>())
    var localDatalist by mutableStateOf(listOf<BasicCloudData>())
    var cloudDatalist by mutableStateOf(listOf<BasicCloudData>())
    var webDavUser by mutableStateOf(WebDAVUser())
    var localPath by mutableStateOf("")
    var formType: FormType by mutableStateOf(FormType(type=""))
    var tip: FormType by mutableStateOf(FormType(type=""))
    var currentCloudData by mutableStateOf(BasicCloudData())
    var backType by mutableStateOf("local")
    var hasLoadCloud by mutableStateOf(false)
    var isEmpty by mutableStateOf(true)

    fun getRepository(): BaseRepository {
        return getCloudService().getRepository()
    }
    abstract fun getCloudService(): BaseService

    fun tryReload(){
        if(oldSid != getRepository().sid) {
            reload()
        }
    }

    fun isValid(): Boolean {
        if(backType == "local"){
            return localPath != ""
        }
        return webDavUser.bind
    }

    fun hasData(): Boolean{
        if(backType == "local"){
            return datalist.isNotEmpty()
        }
        return cloudDatalist.isNotEmpty()
    }

    open fun reload() {
        viewModelScope.launch {
            oldSid = getRepository().sid
            webDavUser = getRepository().getWebDAVUser()
            localPath = getRepository().getLocalPath()
            isEmpty = getCloudService().isEmpty()
            reloadData()
        }
    }

    abstract fun changeBackType(type: String)

    open fun reloadData() {
        viewModelScope.launch(Dispatchers.IO) {
            getRepository().loading()
            localDatalist = getCloudService().loadLocalBackupData()
            mlog("local files", localDatalist)

            if(backType == "cloud"){
                cloudDatalist = getCloudService().loadWebDavCloudFiles(webDavUser)
            }
            changeBackType(backType)
            getRepository().finish()
        }
    }

    open fun doBackup() {
        if(isEmpty){
            getRepository().toast("当前没有数据可以备份")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            getRepository().loading()
            if(backType == "local") {
                getCloudService().makeLocalBackup(force = true)
            }
            else{
                getCloudService().makeWebDavCloudBackup(webDavUser)
            }
            reloadData()
        }
    }

    fun cleanBackup(){
        viewModelScope.launch(Dispatchers.IO) {
            getRepository().loading()
            if(backType == "local"){
                getCloudService().cleanLocalData()
            }
            else{
                getCloudService().cleanWebdavCloud(webDavUser)
            }
            reloadData()
            resetForm()
        }
    }

    suspend fun loadBackData(cloudData: BasicCloudData) : ApiResult<Any>{
        if(backType == "local") {
            return withContext(Dispatchers.IO){
                getCloudService().loadLocalData(cloudData)
            }
        }
        else{
            return withContext(Dispatchers.IO) {
                getCloudService().loadCloudData(webDavUser, cloudData)
            }
        }
    }

    fun toast(msg: String) {
        getRepository().toast(msg)
    }

    fun resetForm() {
        formType = FormType()
    }

    fun justShowBackupDialog(model: BasicCloudData) {
        currentCloudData = model
        formType = FormType("showBackupData", model)
    }

    fun deleteBackup(){
        viewModelScope.launch {
            getRepository().loading()
            if(backType == "local") {
                withContext(Dispatchers.IO){
                    getCloudService().deleteLocalFile(currentCloudData.fileName)
                }
            }
            else{
                withContext(Dispatchers.IO){
                    getCloudService().deleteWebdavFile(webDavUser, currentCloudData.fileName)
                }
            }
            resetForm()
            getRepository().reload()
        }
    }

    abstract fun doBatchImport()

    fun saveLocalData(path: String) {
        viewModelScope.launch {
            getRepository().setLocalBackupConfig(LocalBackupConfig(path=path))
        }
    }
}