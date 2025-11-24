package cn.jianyun.worktime.api

import cn.jianyun.worktime.module.timework.dto.share.TimeworkShareData
import okhttp3.internal.platform.Platform
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ConfigApi {

    @GET("/api/notify/gets")
    suspend fun listConfigs(@Query("app") app: String, @Query("platform") platform: String = "android"): ApiResult<List<AppTipInfo>>

}