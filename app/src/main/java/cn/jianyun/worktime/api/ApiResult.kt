package cn.jianyun.worktime.api


data class ApiResult<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val result: T?
) {

    companion object {
        fun <T> success(result: T): ApiResult<T>{
            return ApiResult(true, "200", "", result)
        }

        fun <T> fail(message: String): ApiResult<T>{
            return ApiResult<T>(false, "200", message, null)
        }
    }

    fun fetchResult(): T {
        return result!!
    }
}




