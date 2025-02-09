package cn.jianyun.worktime.ui.component.model.kt


data class RangeDate(
    var beginDate: String = "",
    var endDate: String = ""
){
    fun isValid(): Boolean{
        return beginDate != "" && endDate != ""
    }

    fun showBeginDate(): String {
        if(beginDate == ""){
            return "开始日期"
        }
        return beginDate
    }

    fun showEndDate(): String {
        if(endDate == ""){
            return "结束日期"
        }
        return endDate
    }
}



