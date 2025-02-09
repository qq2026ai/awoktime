package cn.jianyun.worktime.module.timework.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData

@Dao
interface TimeworkAwardDataDao {

    @Query("SELECT * FROM TimeworkAwardData WHERE projectUuid = :projectUuid AND day Between :gmtBegin AND :gmtEnd")
    suspend fun listByPeriod(projectUuid: String, gmtBegin: String, gmtEnd: String): List<TimeworkAwardData>

    @Query("SELECT * FROM TimeworkAwardData")
    suspend fun list(): List<TimeworkAwardData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimeworkAwardData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: TimeworkAwardData)

    @Delete
    suspend fun delete(item: TimeworkAwardData)

    @Query("SELECT count(1) from TimeworkAwardData WHERE awardUuid = :awardUuid")
    suspend fun findAward(awardUuid: String): Int

    @Query("DELETE FROM TimeworkAwardData")
    suspend fun clearAll()


    @Query("SELECT * FROM TimeworkAwardData WHERE projectUuid = :projectUuid")
    suspend fun listByProject(projectUuid: String): List<TimeworkAwardData>

    @Query("DELETE FROM TimeworkAwardData WHERE projectUuid = :projectUuid")
    suspend fun deleteByProject(projectUuid: String)

}