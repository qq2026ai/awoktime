package cn.jianyun.worktime.module.timework.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData

@Dao
interface TimeworkAwardDao {

    @Query("SELECT * FROM TimeworkAward  ORDER BY type asc, ordinal asc")
    suspend fun list(): List<TimeworkAward>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimeworkAward)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: TimeworkAward)

    @Delete
    suspend fun delete(item: TimeworkAward)

    @Query("DELETE FROM TimeworkAward")
    suspend fun clearAll()


    @Query("SELECT * FROM TimeworkAward WHERE projectUuid = :projectUuid order by ordinal")
    suspend fun listByProject(projectUuid: String): List<TimeworkAward>

    @Query("DELETE FROM TimeworkAward WHERE projectUuid = :projectUuid")
    suspend fun deleteByProject(projectUuid: String)


}