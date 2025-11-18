package cn.jianyun.worktime.module.timework.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.jianyun.worktime.module.timework.model.TimeworkProject

@Dao
interface TimeworkProjectDao {

    @Query("SELECT * FROM TimeworkProject order by ordinal")
    suspend fun list(): List<TimeworkProject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimeworkProject)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: TimeworkProject)


    @Delete
    suspend fun delete(item: TimeworkProject)

    @Query("DELETE FROM TimeworkProject")
    suspend fun clearAll()
}