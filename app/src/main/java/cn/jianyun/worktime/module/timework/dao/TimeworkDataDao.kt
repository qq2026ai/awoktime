package cn.jianyun.worktime.module.timework.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig

@Dao
interface TimeworkDataDao {

    @Query("SELECT * FROM TimeworkData WHERE projectUuid = :projectUuid AND day Between :gmtBegin AND :gmtEnd")
    suspend fun listByPeriod(projectUuid: String, gmtBegin: String, gmtEnd: String): List<TimeworkData>

    @Query("SELECT * FROM TimeworkData")
    suspend fun list(): List<TimeworkData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimeworkData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: TimeworkData)

    @Query("DELETE FROM TimeworkData")
    suspend fun clearAll()

    @Delete
    suspend fun delete(item: TimeworkData)

    @Query("SELECT count(1) from TimeworkData WHERE salaryUuid = :salaryUuid or overSalaryUuid = :salaryUuid")
    suspend fun findSalary(salaryUuid: String): Int

    @Query("SELECT min(gmtCreate) FROM TimeworkData")
    suspend fun findMinDate(): String


    @Query("SELECT * FROM TimeworkData WHERE projectUuid = :projectUuid")
    suspend fun listByProject(projectUuid: String): List<TimeworkData>

    @Query("SELECT count(1) FROM TimeworkData WHERE projectUuid = :projectUuid")
    suspend fun sizeByProject(projectUuid: String): Int


    @Query("DELETE FROM TimeworkData WHERE projectUuid = :projectUuid")
    suspend fun deleteByProject(projectUuid: String)

}