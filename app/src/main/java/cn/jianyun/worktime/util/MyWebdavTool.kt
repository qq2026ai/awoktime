package cn.jianyun.worktime.util

import cn.jianyun.worktime.api.ApiResult
import cn.jianyun.worktime.module.base.model.WebDAVUser
import cn.jianyun.worktime.R
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import java.io.BufferedReader
import java.io.InputStream
import java.nio.charset.Charset

object MyWebdavTool {

    fun check(webDAVUser: WebDAVUser): Boolean{
        val sardine = getClient(webDAVUser)
        try{
            val url = getRealUrl(webDAVUser, "")
            sardine.list(url)
            return true
        }
        catch (ee: Exception){
            mlog("error", ee)
            return false
        }
    }

    fun list(webDAVUser: WebDAVUser,path: String = "/", fileOnly: Boolean = false): ApiResult<Any> {
        val rootPath = getDefaultPath(webDAVUser)
        val client = getClient(webDAVUser)
        try{
            val newUrl = getRealUrl(webDAVUser, path)
            var data = client.list(newUrl).filter{it.path != rootPath && it.path != "$rootPath/"}
            if(fileOnly){
                data = data.filter{!it.isDirectory}
            }
            mlog("list", path, data)
            //文件排序
            var aa = data.sortedWith{v1, v2 ->
                if(v1.isDirectory == v2.isDirectory){
                    var k1 = getParentPath(v1.path).lastIndexOf("/")
                    var k2 = getParentPath(v2.path).lastIndexOf("/")
                    if(k1 != k2){
                        k1 - k2
                    }
                    else{
                        v1.name.compareTo(v2.name)
                    }
                }
                else {
                    ifv(v1.isDirectory, -1, 1)
                }
            }
            mlog("list2",  path,  aa)

            return ApiResult.success(aa)
        }
        catch (ee: Exception){
            mlog("error", ee)
            return ApiResult.fail("加载失败")
        }
    }

    fun getRealUrl(webDAVUser: WebDAVUser, path: String, withDefault: Boolean = true): String{
        var baseUrl = webDAVUser.url
        var newPath = (ifv(withDefault, getDefaultPath(webDAVUser), "") + "/" + path).replace("//", "/")

        mlog("newPath", newPath)
        if(newPath.startsWith("/dav/")) {
            newPath = newPath.substring(4)
        }
        if(newPath.startsWith("/")){
            newPath = newPath.substring(1)
        }
        if(!baseUrl.endsWith("/")){
            baseUrl += "/";
        }
        mlog("finalPath", baseUrl + newPath)

        return baseUrl + newPath
    }

    fun getParentPath(path:String): String {
        //如果文件名包含/，可能需要特殊处理
        val i = path.lastIndexOf("/")
        if(i > 0){
            if(i == path.length - 1){
                return getParentPath(path.substring(0, i))
            }
            return path.substring(0, i)
        }
        return ""
    }
//
//    fun exist(webDAVUser: WebDAVUser, path: String): Boolean {
//        val realPath = getRealUrl(webDAVUser, path)
//        try{
//            val sardine: Sardine = OkHttpSardine()
//            sardine.setCredentials(webDAVUser.username, webDAVUser.password)
//            val data =  sardine.exists(realPath)
//            mlog("exist path: ", realPath, data)
//            return data
//        }
//        catch (e: Exception){
//            mlog("not exist path: ", realPath, e)
//            return false
//        }
//    }

    fun exist(sardine: Sardine, path: String): Boolean {
        try{
            val data =  sardine.list(path)
            mlog("exist path: ", path, data)
            return true
        }
        catch (e: Exception){
            mlog("not exist path: ", path, e)
            return false
        }
    }

    fun createDir(sardine: Sardine, path: String){
        mlog("create dir", path)
        if(exist(sardine, path)){
            return
        }
        var parentPath = getParentPath(path)
        if(exist(sardine, parentPath)) {
            mlog("real create dir", path)
            sardine.createDirectory(path)
            return
        }
        mlog("prepare real create parent dir", parentPath)
        createDir(sardine, parentPath)
        createDir(sardine, path)
    }

    fun getClient(webDAVUser: WebDAVUser): Sardine{
        val sardine: Sardine = OkHttpSardine()
        sardine.setCredentials(webDAVUser.username, webDAVUser.resolvePassword())
        return sardine
    }

    fun put(webDAVUser: WebDAVUser, path: String, content: String): ApiResult<Any> {
        var client = getClient(webDAVUser)
        val newUrl = getRealUrl(webDAVUser, path)
        try{
            createDir(client, getParentPath(newUrl))
            client.put(newUrl, content.toByteArray(Charset.forName("UTF-8")))
            mlog("write file success", newUrl)
            return ApiResult.success(true)
        }
        catch (ee: Exception){
            mlog("write file error", newUrl, ee)
            return ApiResult.fail("加载失败")
        }
    }

    fun clean(webDAVUser: WebDAVUser, dir: String) {
        var client = getClient(webDAVUser)
        val newUrl = getRealUrl(webDAVUser, dir)
        try{
            val files = client.list(newUrl)
            files.forEachIndexed{i, v ->
                if(i > 0){
                    if(v.name.contains(".worktime.")){
                        //remove default path
                        val url = getRealUrl(webDAVUser, v.path, withDefault = false)
                        try{
                            client.delete(url)
                            mlog("remove file", url)
                        }
                        catch (e2: Exception){
                            mlog("remove file error", url, e2)
                        }
                    }
                }
            }

        }
        catch (e: Exception){

        }
    }

    fun delete(webDAVUser: WebDAVUser, dir: String, fileName: String) {
        var client = getClient(webDAVUser)
        val newUrl = getRealUrl(webDAVUser, dir + "/" + fileName)
        try{
            client.delete(newUrl)
        }
        catch (e: Exception){
            mlog("delete file error", newUrl, e)
        }
    }

    fun read(webDAVUser: WebDAVUser, path: String): ApiResult<Any> {
        val client = getClient(webDAVUser)
        val realUrl = getRealUrl(webDAVUser, path)
        try{
            val inputStream = client.get(realUrl)
            return ApiResult.success(inputStreamToString(inputStream))
        }
        catch (e:Exception){
            return ApiResult.fail("读取失败")
        }
    }

    fun inputStreamToString(inputStream: InputStream):  String{
        val reader = BufferedReader(inputStream.reader())
        val content = StringBuilder()
        try {
            var line = reader.readLine()
            while (line != null) {
                content.append(line)
                line = reader.readLine()
            }
        } finally {
            reader.close()
        }
        return content.toString()
    }

    fun getWebDAVServerUrl(platform: String): String {
        when(platform){
            "坚果云" -> return "https://dav.jianguoyun.com/dav/"
        }
        return "https://app.koofr.net/dav/"
    }

    fun getWebDAVIcon(platform: String): Int {
        when(platform){
            "坚果云" -> return R.mipmap.jgy
            "Koofr" -> return R.mipmap.koofr
            "GoogleDrive" -> return R.mipmap.cloud
        }
        return R.mipmap.cloud
    }

    fun getDefaultPath(user: WebDAVUser): String {
        when(user.platform){
            "坚果云" -> return "/dav"
            "Koofr" -> return "/dav/Koofr"
            "GoogleDrive" -> return "/dav/GoogleDrive"
            "OneDrive" -> return "/dav/OneDrive"
            "Dropbox" -> return "/dav/Dropbox"
        }
        return "/dav"
    }

}