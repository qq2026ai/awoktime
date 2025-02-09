package cn.jianyun.worktime.module.base

import android.content.Context
import androidx.room.Room
import cn.jianyun.worktime.module.base.dao.WebDAVUserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext appContext: Context): BaseDatabase {
        val db = Room.databaseBuilder(
            appContext,
            BaseDatabase::class.java,
            "BaseDb2"
        ).addMigrations(MIGRATION_1_2).build()
        return db
    }

    @Provides
    fun provideDao1(appDatabase: BaseDatabase): WebDAVUserDao {
        return appDatabase.WebDAVUserDao()
    }

}


