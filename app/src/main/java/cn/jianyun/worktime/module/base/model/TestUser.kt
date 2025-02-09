package cn.jianyun.worktime.module.base.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestUser(
    @PrimaryKey
    var uuid: String = "",
    var name: String = ""
)