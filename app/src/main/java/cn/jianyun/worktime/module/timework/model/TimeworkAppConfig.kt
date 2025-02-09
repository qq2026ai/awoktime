package cn.jianyun.worktime.module.timework.model



import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "TimeworkAppConfig")
data class TimeworkAppConfig(
    @PrimaryKey
    var uuid:String = "only",
    var config: String = "",
)