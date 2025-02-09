package cn.jianyun.worktime.api

import cn.jianyun.worktime.module.timework.dto.share.TimeworkShareData
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ShareApi {

    @GET("/api/export/all")
    suspend fun fetchWechatData(@Query("userUuid") userUuid: String): ApiResult<TimeworkShareData>


}