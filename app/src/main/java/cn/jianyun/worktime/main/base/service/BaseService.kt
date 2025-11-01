package cn.jianyun.worktime.main.base.service

import cn.jianyun.worktime.api.ApiResult
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.main.base.model.BasicCloudData
import cn.jianyun.worktime.model.CloudFile
import cn.jianyun.worktime.module.base.dto.BaseBackupData
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.util.MyFileUtil
import cn.jianyun.worktime.util.MyWebdavTool
import cn.jianyun.worktime.util.ifv
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface BaseService {

    fun getRepository(): BaseRepository

    suspend fun setProjectId(pid: String) {
        getRepository().cache(getBizName() + "_pid", pid)
    }

    suspend fun getProjectId(): String {
        return getRepository().getCache(getBizName() + "_pid", "default")
    }

    suspend fun isCloudOk(): Boolean {
        return getRepository().getWebDAVUser().bind
    }

    //业务名
    fun getBizName(): String

    fun getNotifyBizName(): String{
        return "Plan_${getBizName()}"
    }

    //业务路径
    fun getWorkSpace(): String {
        return "App/${getBizName()}/"
    }

    //刷新小组件
    suspend fun notifyWidget()

    //设置提醒
    suspend fun makeNotify()

    //准备数据
    suspend fun prepareBackupData(): CloudFile

    //本地备份
    suspend fun makeLocalBackup(force: Boolean = false) {
        val config = getRepository().getLocalBackupConfig()
        if(config.autoBackup || force){
            val localPath = config.path
            if(localPath != ""){
                val prepareData = prepareBackupData()
                withContext(Dispatchers.IO) {
                    MyFileUtil.writeFile(context = getRepository().context, localPath, getWorkSpace() + prepareData.name, prepareData.content)
                }
            }
        }
    }

    //加载本地备份
    suspend fun loadLocalBackupData(): List<BasicCloudData> {
        val files = MyFileUtil.readDirFiles(getRepository().context, getRepository().getLocalPath(), getWorkSpace())
        if(files.isEmpty()){
            return listOf()
        }
        return changeFileFormat(files, "本地备份")
    }

    //清空云备份
    suspend fun cleanWebdavCloud(webDAVUser: WebDAVUser){
        MyWebdavTool.clean(webDAVUser, getWorkSpace())
    }

    suspend fun deleteWebdavFile(webDAVUser: WebDAVUser, fileName: String) {
        MyWebdavTool.delete(webDAVUser, getWorkSpace(), fileName)
    }

    suspend fun deleteLocalFile(fileName: String) {
        MyFileUtil.deleteFile(getRepository().context, getRepository().getLocalPath(), getWorkSpace() + fileName)
    }

    //云端备份
    suspend fun makeWebDavCloudBackup(webDAVUser: WebDAVUser) {
        val cloudFile = prepareBackupData()
        MyWebdavTool.put(webDAVUser, "${getWorkSpace()}/" + cloudFile.name, cloudFile.content)
    }

    //加载云端备份
    suspend fun loadWebDavCloudFiles(webDAVUser: WebDAVUser): List<BasicCloudData>  {
        val rst = MyWebdavTool.list(webDAVUser, getWorkSpace(), fileOnly = true)
        if(rst.success){
            val sources = rst.fetchResult() as List<DavResource>
            val fileNames = sources.map{it.name}
            return changeFileFormat(fileNames, webDAVUser.platform)
        }
        else{
            //异常
            return emptyList()
        }
    }

    //加载云数据
    suspend fun loadCloudData(webDAVUser: WebDAVUser, file: BasicCloudData): ApiResult<Any> {
        return MyWebdavTool.read(webDAVUser, "${getWorkSpace()}/${file.fileName}")
    }

    suspend fun loadLocalData(cloudData: BasicCloudData): ApiResult<Any> {
        return MyFileUtil.readFileContent(getRepository().context, getRepository().getLocalPath(), getWorkSpace() + cloudData.fileName)
    }

    fun changeFileFormat(files: List<String>, platform: String): List<BasicCloudData> {
        val suffix = ".worktime.${getBizName().lowercase()}"
        return files.filter{it.endsWith(suffix)}.map {
            var name = it.substring(0, it.length - suffix.length)
            val ds = name.split("_")
            if(ds.size == 3){
                BasicCloudData(fileName = it, count=ds[0], size = ds[1], date = ds[2], source = platform)
            }
            else{
                BasicCloudData()
            }
        }.filter{it.date != ""}.sortedWith{v1, v2 -> ifv(v1.date < v2.date, 1,-1) }
    }

    suspend fun clearAll() :CloudFile

    suspend fun writeAll(backupData: BaseBackupData)
    suspend fun isEmpty(): Boolean
    suspend fun cleanLocalData() {
        MyFileUtil.cleanDir(getRepository().context, getRepository().getLocalPath(), getWorkSpace())
    }


}