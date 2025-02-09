package cn.jianyun.worktime.util

import android.content.Context
import android.net.Uri
import androidx.compose.ui.util.fastJoinToString
import androidx.documentfile.provider.DocumentFile
import cn.jianyun.worktime.api.ApiResult
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URLDecoder





/**
 * 本地文件存储工具类
 */
object MyFileUtil {

    /**
     * bashPath 必须存在，不然不支持后续操作
     */
    fun createDir(context: Context, basePath: String, path: String): Boolean{
        if("".equals(path)){
            return true
        }
        //basePath
        var documentFile = DocumentFile.fromTreeUri(context, Uri.parse(basePath))

        mlog("root file", documentFile)

        if(documentFile == null){
            mlog("file not exits1", basePath)
            return false
        }
        if(!documentFile.exists()){
            mlog("file not exits2", basePath)
            return false
        }
        if(!documentFile.canWrite()){
            mlog("file not write", basePath)
            return false
        }
        mlog("root file exist", documentFile)

        var startI = 0
        var flag = true
        while(flag){
            var nextI = path.indexOf("/", startI)
            if(nextI < 0) {
                //到底了
                nextI = path.length
                flag = false
            }
            var nextDirName = path.substring(startI, nextI)
            //逐一创建目录
            val dirPath = path.substring(0, nextI)
            mlog("next path", dirPath)
            val childFile = getNextFile(documentFile!!, nextDirName)
            if(childFile == null){
                documentFile = documentFile!!.createDirectory(nextDirName)
                mlog("new file", documentFile?.uri?.path, documentFile?.canWrite())
            }
            else{
                if(childFile.isFile){
                    //删除
                    childFile.delete()
                    documentFile = documentFile!!.createDirectory(nextDirName)
                    mlog("new file", documentFile?.uri?.path, documentFile?.canWrite())
                }
                else{
                    mlog("child dir exists ", dirPath)
                    documentFile = childFile;
                }
            }
            startI = nextI + 1
        }
        return true;
    }

    private fun getNextFile(dir: DocumentFile, fileName: String): DocumentFile?{
        return dir.listFiles().firstOrNull{it.name == fileName}
    }

    private fun getParentPath(path: String): String {
        val i = path.lastIndexOf("/")
        if(i > 0){
            return path.substring(0, i)
        }
        return ""
    }

    private fun getFileName(path: String): String {
        val i = path.lastIndexOf("/")
        if(i > 0){
            return path.substring(i + 1)
        }
        return path
    }

    private fun findWriteableDocumentFile(context: Context, basePath: String, path: String): DocumentFile? {
        mlog("start", basePath, path)

        var newPath = path
        if(path.endsWith("/")){
            newPath = path.substring(0, path.length - 1)
        }
        var documentFile = DocumentFile.fromTreeUri(context, Uri.parse(basePath))
        var startI = 0
        var flag = true
        while(flag) {
            var nextI = newPath.indexOf("/", startI)
            if (nextI < 0) {
                //到底了
                nextI = newPath.length
                flag = false
            }
            var nextDirName = newPath.substring(startI, nextI)
            //查找到下一个文件
//            mlog(documentFile, "child files is", startI, nextI, nextDirName)
//            documentFile!!.listFiles().forEach { mlog(it.name) }
            documentFile = getNextFile(documentFile!!, nextDirName)
            if(documentFile == null){
                return null
            }
            if(!flag){
                return documentFile
            }
            mlog("read next file", nextDirName, documentFile, documentFile?.exists())
            startI = nextI + 1
        }
        return documentFile!!
    }

    private fun getRealPath(basePath: String, path: String):Uri{
        return Uri.parse(basePath + "%2F" + path.replace("/", "%2F"))
    }


    fun readDirRelativePath(path: String): String {
        val suffix = "documents/tree/primary%3A"
        val index = path.indexOf(suffix)
        if(index > 0){
            var dd = path.substring(index + suffix.length).replace("%2F", "/")
            var decodedStr = URLDecoder.decode(dd, "UTF-8")
            return "/$decodedStr"
        }
        return path
    }


    /**
     * context: 上下文
     * basePath: 用户授权目录
     * path: 自定义写入文件路径
     * data: 写入数据
     */
    fun writeFile(context: Context, basePath: String, path: String, data: String){
        mlog("start write file", basePath, path)
        var fileOutputStream: FileOutputStream? = null
        try {
            //先把要写入文件的目录先创建好
            createDir(context, basePath, getParentPath(path))
            val parentPath = basePath + "%2F" + getParentPath(path).replace("/", "%2F")
            //得到父文件
            val parentDir = findWriteableDocumentFile(context, basePath, getParentPath(path))
            mlog("parent file", parentPath, parentDir?.canWrite())
            if(parentDir != null){
                val realFile = parentDir.createFile("text/plan", getFileName(path))!!
                val os = context.contentResolver.openOutputStream(realFile.uri)
                os?.write(data.toByteArray())
            }
            mlog("write success")
        } catch (e: Exception) {
            mlog("write error", e)
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e: Exception) {
                    mlog("write error2", e)
                }
            }
        }
    }

    fun readDirFiles(context: Context, basePath: String, dir: String): List<String>{
        try{
            val parentDir = findWriteableDocumentFile(context, basePath, getParentPath(dir))
            if(parentDir != null){
                return parentDir.listFiles().map{ it.name ?: ""}
            }
        }
        catch (e: Exception){

        }
        return listOf()
    }

    fun readFileContent(context: Context, basePath: String, path: String): ApiResult<Any> {
        try{
            val originFile = findWriteableDocumentFile(context, basePath, path)
            if(originFile != null){
                try {
                    val inputStream = context.contentResolver.openInputStream(originFile.uri)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val lines = reader.readLines()
                    reader.close()
                    return ApiResult.success(lines.fastJoinToString("\n"));
                } catch (e: Exception) {
                    e.printStackTrace();
                }
                finally {

                }
            }
        }
        catch (e: Exception){

        }
        return ApiResult.fail("文件不存在")
    }

    fun cleanDir(context: Context, basePath: String, workSpace: String) {
        val originFile = findWriteableDocumentFile(context, basePath, workSpace)
        mlog("origin dir", originFile, workSpace)
        if(originFile != null){
            originFile.listFiles().forEach {
                it.delete()
            }
        }

    }

    fun deleteFile(context: Context, basePath: String, file: String) {
        val originFile = findWriteableDocumentFile(context, basePath, file)
        if(originFile != null){
            originFile.delete()
        }
    }

}