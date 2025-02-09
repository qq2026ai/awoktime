package cn.jianyun.worktime.vm

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.api.BaseApi
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FestivalViewModel @Inject constructor(
    val baseRepository: BaseRepository,
    val festivalApi: BaseApi,
    @ApplicationContext val context: Context
): ViewModel() {

    var formType by mutableStateOf(FormType())

    fun isForm(type: String):Boolean{
        return formType.type == type
    }

    fun toast(msg: String){
        baseRepository.toast(msg)
    }

    fun init(){
        viewModelScope.launch {


        }
    }

}
