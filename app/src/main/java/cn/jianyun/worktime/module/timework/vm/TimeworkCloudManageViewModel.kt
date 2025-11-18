package cn.jianyun.worktime.module.timework.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.main.base.model.BasicCloudData
import cn.jianyun.worktime.main.base.service.BaseService
import cn.jianyun.worktime.module.base.vm.BaseCloudViewModel
import cn.jianyun.worktime.module.timework.model.TimeworkBackupData
import cn.jianyun.worktime.module.timework.service.TimeworkService
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.util.mlog
import com.alibaba.fastjson2.JSON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class TimeworkCloudManageViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val baseService: TimeworkService
) : BaseCloudViewModel() {

    var currentBackupData by mutableStateOf(TimeworkBackupData())

    init {
        reload()
    }

    override fun getCloudService(): BaseService {
        return baseService
    }

    override fun changeBackType(type: String) {
        viewModelScope.launch {
            backType = type
            if(type == "local"){
                datalist = localDatalist
            }
            else{
                if(!hasLoadCloud){
                    baseRepository.loading(true)
                    cloudDatalist =withContext(Dispatchers.IO){
                        getCloudService().loadWebDavCloudFiles(webDavUser)
                    }
                    baseRepository.finish()
                    hasLoadCloud = true
                }
                datalist = cloudDatalist
            }
        }
    }

    override fun doBatchImport() {
        TODO("Not yet implemented")
    }

    fun doCover(navHostController: NavHostController){
        //替换
        viewModelScope.launch {
            baseRepository.loading(true)
            val backupRst = loadBackData(formType.editItem as BasicCloudData)
            if(backupRst.success){
                val backupData = TimeworkBackupData.parse(backupRst.fetchResult())
                //先备份
//                withContext(Dispatchers.IO){
//                    baseService.makeWebDavCloudBackup(webDavUser)
//                }

                mlog("backupData: ", JSON.toJSONString(backupData))

                //后清空
                baseService.clearAll()
                //再写入
                baseService.writeAll(backupData)
                toast("操作成功")
                baseRepository.reload()
                goBack(navHostController)
            }
            else{
                baseRepository.finish()
                toast("读取失败")
            }
        }

    }

}
