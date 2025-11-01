package cn.jianyun.worktime.api
data class TraceInfo (
    var type: String = "",
    var code: String = "",
    var uid: String = "",
    var fields: Map<String, String> = mapOf()
){
    fun addField(key: String, value: String){
        var myFields = fields.toMutableMap()
        myFields[key] = value
        this.fields = myFields
    }
}