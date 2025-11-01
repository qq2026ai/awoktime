package cn.jianyun.worktime.api

import cn.jianyun.worktime.module.timework.vm.ShareDataDO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface TraceApi {

    @POST("/api/event/post")
    suspend fun post(@Body data: TraceInfo, @Header("app") app:String = "ajjgs"): ApiResult<String>

    @GET("/api/application/version")
    suspend fun getAppVersion(@Query("app") app:String): ApiResult<String>

    @POST("/api/shareData/share")
    suspend fun share(@Body shareDataDO: ShareDataDO): ApiResult<String>

    @GET("/api/shareData/fetch")
    suspend fun fetch(@Query("shareId") shareId: String, @Query("appId") app:String = "jgs"): ApiResult<ShareDataDO>

}