package cn.jianyun.worktime.model


const val DELETE = "DELETE"
const val ADD = "ADD"
const val DETAIL = "DETAIL"
const val UPDATE = "UPDATE"
const val DELETE_ONE = "DELETE_ONE"
const val LONG_PRESS = "LONG_PRESS"
const val PRESS = "PRESS"
const val SORT = "SORT"
const val CLEAR = "CLEAR"

data class FormType(
    var type: String = "",
    var editItem: Any? = null,
    var message: String = ""
) {


    companion object {

        fun add(item: Any? = null): FormType{
            return FormType(type = ADD, editItem = item)
        }
        fun edit(item: Any? = null): FormType{
            return FormType(type = UPDATE, editItem = item)
        }
        fun delete(item: Any? = null): FormType{
            return FormType(type = DELETE, editItem = item)
        }
        fun clear(message: String = "确定要清空", item: Any? = null): FormType{
            return FormType(type = CLEAR, message=message,  editItem = item)
        }
        fun detail(item: Any): FormType{
            return FormType(type = DETAIL, editItem = item)
        }
        fun deleteOne(item: Any): FormType{
            return FormType(type = DELETE_ONE, editItem = item)
        }

        fun longPress(item: Any): FormType{
            return FormType(type = LONG_PRESS, editItem = item)
        }
        fun press(item: Any): FormType{
            return FormType(type = PRESS, editItem = item)
        }
        fun sort(): FormType{
            return FormType(type = SORT)
        }
    }

    fun reset(){
        this.type = ""
        this.editItem = null
    }

    fun isForm(newType: String):Boolean{
        return type == newType
    }

    fun isBizForm(newType: String):Boolean{
        return type.startsWith(newType)
    }


    fun isAdd(): Boolean{
        return type == ADD
    }
    fun isSort(): Boolean{
        return type == SORT
    }
    fun isUpdate(): Boolean{
        return type == UPDATE
    }
    fun isDelete():Boolean {
        return type == DELETE
    }
    fun isClear():Boolean {
        return type == CLEAR
    }
}