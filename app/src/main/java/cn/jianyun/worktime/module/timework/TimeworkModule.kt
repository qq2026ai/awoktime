package cn.jianyun.worktime.module.timework

import android.content.Context
import androidx.room.Room
import cn.jianyun.worktime.module.timework.dao.TimeworkAppConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDefaultConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkProjectDao
import cn.jianyun.worktime.module.timework.dao.TimeworkSalaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimeworkModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext appContext: Context): TimeworkDatabase {
        return Room.databaseBuilder(
            appContext,
            TimeworkDatabase::class.java,
            "TimeworkDb22"
        ).build()
    }

    @Provides
    fun provideDao1(appDatabase: TimeworkDatabase): TimeworkSalaryDao {
        return appDatabase.salaryDao()
    }

    @Provides
    fun provideDao2(appDatabase: TimeworkDatabase): TimeworkAwardDao {
        return appDatabase.awardDao()
    }

    @Provides
    fun provideDao3(appDatabase: TimeworkDatabase): TimeworkDefaultConfigDao {
        return appDatabase.configDao()
    }

    @Provides
    fun provideDao4(appDatabase: TimeworkDatabase): TimeworkDataDao {
        return appDatabase.dataDao()
    }

    @Provides
    fun provideDao5(appDatabase: TimeworkDatabase): TimeworkAwardDataDao {
        return appDatabase.awardDataDao()
    }

    @Provides
    fun provideDao6(appDatabase: TimeworkDatabase): TimeworkAppConfigDao {
        return appDatabase.appConfigDao()
    }

    @Provides
    fun provideDao7(appDatabase: TimeworkDatabase): TimeworkProjectDao {
        return appDatabase.projectDao()
    }
}


