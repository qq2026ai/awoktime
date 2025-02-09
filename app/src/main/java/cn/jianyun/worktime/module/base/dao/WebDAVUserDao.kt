package cn.jianyun.worktime.module.base.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.jianyun.worktime.module.base.model.WebDAVUser

@Dao
interface WebDAVUserDao {

    @Query("SELECT * FROM WebDAVUser")
    suspend fun list(): List<WebDAVUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WebDAVUser)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: WebDAVUser)

    @Delete
    suspend fun delete(item: WebDAVUser)

    suspend fun getMasterOne(): WebDAVUser {
        val all = list()
        if(all.isEmpty()){
            return WebDAVUser()
        }
        val k = all.find{it.masterNode}
        if(k != null){
            return k
        }
        return all[0]
    }

}