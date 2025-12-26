package cn.jianyun.worktime.module.timework.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig

@Dao
interface TimeworkDefaultConfigDao {

    @Query("SELECT * FROM TimeworkDefaultConfig order by ordinal asc")
    suspend fun list(): List<TimeworkDefaultConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimeworkDefaultConfig)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: TimeworkDefaultConfig)

    @Delete
    suspend fun delete(item: TimeworkDefaultConfig)

    @Query("SELECT count(1) FROM TimeworkDefaultConfig WHERE config like '%' || :salaryUuid || '%' ")
    suspend fun findSalary(salaryUuid: String): Int

    @Query("DELETE FROM TimeworkDefaultConfig")
    suspend fun clearAll()

    @Query("SELECT * FROM TimeworkDefaultConfig WHERE projectUuid = :projectUuid and uuid != '' order by ordinal asc")
    suspend fun listByProject(projectUuid: String): List<TimeworkDefaultConfig>

    @Query("DELETE FROM TimeworkDefaultConfig WHERE projectUuid = :projectUuid")
    suspend fun deleteByProject(projectUuid: String)
}