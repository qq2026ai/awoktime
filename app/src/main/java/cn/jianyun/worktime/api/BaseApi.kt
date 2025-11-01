package cn.jianyun.worktime.api

import cn.jianyun.worktime.main.setting.user.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BaseApi {

    /**
     * 节假日API
     */
    @GET("/api/holiday/fetch")
    suspend fun fetchHoliday(@Query("year") year: Int): ApiResult<List<FestivalData>>
    /**
     * 反馈管理API
     */
    @POST("/api/feedback/post")
    suspend fun postFeedback(@Body feedback: FeedbackModel): ApiResult<Boolean>

    @GET("/api/feedback/list")
    suspend fun listUserFeedback(@Query("userId") userId: String): ApiResult<List<FeedbackModel>>


    @GET("/payApi/alipay/makeAppPay")
    suspend fun sdkRequest(@Query("app") app: String,@Query("platform") platform: String,@Query("uid") uid: String,@Query("type") type: String,@Query("testPay") testPay: Boolean = false): ApiResult<String>

    @POST("/payApi/vipUser/check")
    suspend fun checkVip(@Query("app") app: String,@Query("platform") platform: String,@Query("uid") uid: String,@Query("type") type: String): ApiResult<String>


    /**
     * 用户模块：登录
     */
    @POST("/api/user/login")
    suspend fun login(@Body user: User): ApiResult<User>

    /**
     * 用户模块：注册
     */
    @POST("/api/user/regist")
    suspend fun regist(@Body user: User): ApiResult<User>

    /**
     * 用户模块：修改信息
     */
    @POST("/api/user/updateInfo")
    suspend fun updateInfo(@Body user: User): ApiResult<Any>

    /**
     * 更新云端账号
     */
    @POST("/api/user/updateCloudInfo")
    suspend fun updateCloudInfo(@Body user: User): ApiResult<Any>

    /**
     * 用户模块：修改密码
     */
    @POST("/api/user/updatePassword")
    suspend fun updatePassword(@Body user: User): ApiResult<Any>

    /**
     * 用户模块：获取最新信息
     */
    @POST("/api/user/fetch")
    suspend fun fetchUserInfo(@Body user: User): ApiResult<User>

    /**
     * 用户模块：退出登录
     */
    @POST("/api/user/exit")
    suspend fun exit(@Body user: User): ApiResult<Any>

    /**
     * 用户模块：注销账号
     */
    @POST("/api/user/logoff")
    suspend fun logoff(@Body user: User): ApiResult<Any>


}

data class FestivalData(
    var holiday: Boolean = false,
    var name: String = "",
    var date: String = "",
)