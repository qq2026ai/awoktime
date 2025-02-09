package cn.jianyun.worktime.api

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
}