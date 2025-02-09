package cn.jianyun.worktime.vm

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.util.isValidColor
import cn.jianyun.worktime.util.mlog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ColorViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    @ApplicationContext val context: Context
): ViewModel() {

    var defaultColorMode by mutableStateOf("")
    var collectColors by mutableStateOf("")

    var formType by mutableStateOf(FormType())

    init{
        viewModelScope.launch {
            defaultColorMode = baseRepository.getCache("defaultColorMode", "chinese")
            collectColors = baseRepository.getCache("collectColors", "")
        }
    }

    fun toast(msg: String){
        baseRepository.toast(msg)
    }

    fun batchAddCollectColor(colors: String){
        var s = mutableListOf<String>()
        collectColors.replace("，", ",").split(",").forEach{
            if(!s.contains(it)){
                s.add(it)
            }
        }

        colors.replace("\n", ",").split(",").forEach{
            if(it.isValidColor() && !s.contains(it)){
                s.add(it)
            }
        }

        collectColors = s.joinToString(",")
    }

    fun addCollectColor(color: String): Boolean{
        if(collectColors.contains(color)){
            return false
        }
        var s = mutableListOf<String>()
        collectColors.split(",").forEach{
            if(it != color && !s.contains(color)){
                s.add(it)
            }
        }
        s.add(color)
        collectColors = s.joinToString(",")
        viewModelScope.launch {
            baseRepository.cache("collectColors", collectColors)
        }
        return true
    }

    fun removeCollectColor(color: String){
        var s = mutableListOf<String>()
        collectColors.split(",").forEach{
            if(color != it){
                s.add(it)
            }
        }
        collectColors = s.joinToString(",")
        mlog("remove color", color, collectColors)

        viewModelScope.launch {
            baseRepository.cache("collectColors", collectColors)
        }
    }

    fun listColor(): List<String> {
        var s = mutableListOf<String>()
        collectColors.split(",").forEach{
            if(it != ""){
                s.add(it)
            }
        }
        return s
    }

    fun isForm(type: String): Boolean{
        return formType.type == type
    }

    fun reset() {
        formType = FormType()
    }
}

