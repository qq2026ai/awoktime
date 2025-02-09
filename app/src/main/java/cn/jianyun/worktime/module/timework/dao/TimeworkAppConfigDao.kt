package cn.jianyun.worktime.module.timework.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.jianyun.worktime.module.timework.dto.TimeworkAppConfigDTO
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import com.alibaba.fastjson2.JSON

@Dao
interface TimeworkAppConfigDao {

    @Query("SELECT * FROM TimeworkAppConfig limit 1")
    suspend fun find(): TimeworkAppConfig?

    suspend fun get(): TimeworkAppConfigDTO {
        var one = find() ?: TimeworkAppConfig()
        if(one.config == ""){
            return TimeworkAppConfigDTO()
        }
        return JSON.parseObject(one.config, TimeworkAppConfigDTO::class.java)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(item: TimeworkAppConfig)

    @Query("DELETE FROM TimeworkAppConfig")
    suspend fun clearAll()

}