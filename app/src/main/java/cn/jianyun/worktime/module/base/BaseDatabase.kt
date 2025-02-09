package cn.jianyun.worktime.module.base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.jianyun.worktime.module.base.dao.WebDAVUserDao
import cn.jianyun.worktime.module.base.model.TestUser
import cn.jianyun.worktime.module.base.model.WebDAVUser


@Database(entities = [WebDAVUser::class, TestUser::class], version = 2)
abstract class BaseDatabase : RoomDatabase() {
 abstract fun WebDAVUserDao(): WebDAVUserDao

}


val MIGRATION_1_2: Migration = object : Migration(1, 2) {
 override fun migrate(database: SupportSQLiteDatabase) {
  database.execSQL(
   "CREATE TABLE if not exists `TestUser` (`uuid` TEXT not null, "
           + "`name` TEXT not null, PRIMARY KEY(`uuid`))"
  )
 }
}