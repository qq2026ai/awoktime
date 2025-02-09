package cn.jianyun.worktime.module.timework

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.jianyun.worktime.module.timework.dao.TimeworkAppConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDao
import cn.jianyun.worktime.module.timework.dao.TimeworkAwardDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDataDao
import cn.jianyun.worktime.module.timework.dao.TimeworkDefaultConfigDao
import cn.jianyun.worktime.module.timework.dao.TimeworkProjectDao
import cn.jianyun.worktime.module.timework.dao.TimeworkSalaryDao
import cn.jianyun.worktime.module.timework.model.TimeworkAppConfig
import cn.jianyun.worktime.module.timework.model.TimeworkAward
import cn.jianyun.worktime.module.timework.model.TimeworkAwardData
import cn.jianyun.worktime.module.timework.model.TimeworkData
import cn.jianyun.worktime.module.timework.model.TimeworkDefaultConfig
import cn.jianyun.worktime.module.timework.model.TimeworkProject
import cn.jianyun.worktime.module.timework.model.TimeworkSalary


@Database(entities = [TimeworkSalary::class, TimeworkProject::class, TimeworkData::class, TimeworkAward::class, TimeworkAwardData::class, TimeworkDefaultConfig::class, TimeworkAppConfig::class], version = 1)
abstract class TimeworkDatabase : RoomDatabase() {
 abstract fun salaryDao(): TimeworkSalaryDao
 abstract fun awardDao(): TimeworkAwardDao
 abstract fun configDao(): TimeworkDefaultConfigDao
 abstract fun dataDao(): TimeworkDataDao
 abstract fun awardDataDao(): TimeworkAwardDataDao
 abstract fun appConfigDao(): TimeworkAppConfigDao
 abstract fun projectDao(): TimeworkProjectDao

}