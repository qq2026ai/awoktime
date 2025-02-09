package cn.jianyun.worktime.module.timework.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.jianyun.worktime.module.timework.model.TimeworkSalary

@Dao
interface TimeworkSalaryDao {

    @Query("SELECT * FROM TimeworkSalary ORDER BY shown desc, ordinal asc")
    suspend fun list(): List<TimeworkSalary>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimeworkSalary)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: TimeworkSalary)

    @Delete
    suspend fun delete(item: TimeworkSalary)

    @Query("SELECT count(1) from TimeworkSalary where refSalary = :salaryUuid")
    suspend fun findSalary(salaryUuid: String): Int

    @Query("DELETE FROM TimeworkSalary")
    suspend fun clearAll()

    @Query("SELECT * FROM TimeworkSalary WHERE projectUuid = :projectUuid order by ordinal")
    suspend fun listByProject(projectUuid: String): List<TimeworkSalary>

    @Query("DELETE FROM TimeworkSalary WHERE projectUuid = :projectUuid")
    suspend fun deleteByProject(projectUuid: String)
}