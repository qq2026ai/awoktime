package cn.jianyun.worktime.module.base.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.module.base.dao.WebDAVUserDao
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.util.uuid
import cn.jianyun.worktime.util.MyWebdavTool
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.isOk
import cn.jianyun.worktime.util.mlog
import com.alibaba.fastjson2.toJSONString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WebDAVUserViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    private val dao: WebDAVUserDao
) : ViewModel() {

    var formType: FormType by mutableStateOf(FormType(type=""))
    var isDelete by mutableStateOf(false)
    var editItem by mutableStateOf(WebDAVUser())
    var datalist by mutableStateOf(listOf<WebDAVUser>())
    var oldSid by mutableStateOf(0)
    var inited by mutableStateOf(false)

    init {
        reload()
    }

    fun tryReload(){
        if(oldSid != baseRepository.sid) {
            reload()
        }
    }

    fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
            datalist = dao.list()
        }
    }

    fun initModel(model: WebDAVUser){
        if(inited){
            return
        }
        inited = true
        editItem = model
    }


    fun save(navHostController: NavHostController) {
        viewModelScope.launch() {
            val msg = editItem.isValid()
            if(isOk(msg)){
                //校验
                baseRepository.loading()
                //判断是否存在相同节点
                if(!datalist.filter{it.nodeInfo() == editItem.nodeInfo() && it.uuid != editItem.uuid}.isEmpty()) {
                    baseRepository.toast("该平台已经绑定${editItem.username}账号了")
                    baseRepository.finish()
                    return@launch
                }
                var flag = withContext(Dispatchers.IO) {
                    MyWebdavTool.check(editItem)
                }
                if(!flag){
                    baseRepository.toast("服务器连接失败")
                    return@launch
                }
                if(editItem.masterNode){
                    //更新其他的节点为false
                    datalist.filter{it.uuid != editItem.uuid}.forEach{
                        dao.update(it.copy(masterNode = false))
                    }
                }

                if(editItem.isAdd()){
                    editItem.uuid = uuid()
                    dao.insert(editItem.copy(bind = true))
                }
                else {
                    dao.update(editItem.copy(bind = true))
                    reload()
                }
                baseRepository.reload()
                formType = FormType()
                saveToServer()
                goBack(navHostController)
            }
            else{
                baseRepository.toast(msg)
            }
        }
    }

    fun doDelete(navHostController: NavHostController){
        viewModelScope.launch {
            dao.delete(editItem)
            isDelete = false
            resetForm()
            baseRepository.reload()
            saveToServer()
            reload()
            goBack(navHostController)
        }
    }

    fun resetForm(){
        formType = FormType()
        reload()
    }

    fun doSort(newOrderList: List<String>) {
        viewModelScope.launch {
            datalist.forEach{
                val i = newOrderList.indexOf(it.uuid)
                var newItem = it
                dao.update(newItem)
            }
            reload()
        }
    }

    fun copyOne(navHostController: NavHostController) {
        viewModelScope.launch {
            val msg = editItem.isValid()
            if(isOk(msg)){
                //校验
                baseRepository.loading()
                mlog("start check")
                //判断是否存在相同节点
                if(!datalist.filter{it.nodeInfo() == editItem.nodeInfo()}.isEmpty()) {
                    baseRepository.toast("该平台已经绑定${editItem.username}账号了")
                    baseRepository.finish()
                    return@launch
                }

                var flag = withContext(Dispatchers.IO) {
                    MyWebdavTool.check(editItem)
                }
                if(!flag){
                    baseRepository.toast("服务器连接失败")
                    return@launch
                }
                if(editItem.masterNode){
                    //更新其他的节点为false
                    datalist.filter{it.uuid != editItem.uuid}.forEach{
                        dao.update(it.copy(masterNode = false))
                    }
                }

                saveToServer()
                dao.insert(editItem.copy(bind = true, uuid = uuid()))
                baseRepository.reload()
                formType = FormType()
                goBack(navHostController)
            }
            else{
                baseRepository.toast(msg)
            }
        }
    }



    fun saveToServer() {
        viewModelScope.launch {
            if(baseRepository.isLogin()){
                val cloudConfig = dao.list().filter{it.cloud}.toJSONString()
                baseRepository.api.updateCloudInfo(baseRepository.loginUser.copy(cloudConfig=cloudConfig))
                mlog("update cloud info", cloudConfig)
            }
        }
    }

}