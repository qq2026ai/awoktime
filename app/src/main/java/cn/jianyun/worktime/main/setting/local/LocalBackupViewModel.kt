package cn.jianyun.worktime.main.setting.local


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.model.FormType
import cn.jianyun.worktime.model.LocalBackupConfig
import cn.jianyun.worktime.module.base.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalBackupViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : BaseViewModel() {

    var localPath by mutableStateOf("")
    var localBackupConfig by mutableStateOf(LocalBackupConfig())
    init {
        reload()
    }

    override fun getRepository(): BaseRepository {
        return baseRepository
    }

    override fun reload(){
        viewModelScope.launch {
            oldSid = baseRepository.sid
            localBackupConfig = baseRepository.getLocalBackupConfig()
            localPath = baseRepository.getLocalPath()
        }
    }

    override fun reloadData(dataChanged: Boolean) {

    }

    fun loadFile() {
        viewModelScope.launch {
            formType = FormType("pickFile")
        }
    }

    fun loadDir(){
        viewModelScope.launch {
            formType = FormType("pickDir")
        }
    }

    fun saveLocalInfo() {
        viewModelScope.launch {
            baseRepository.setLocalBackupConfig(localBackupConfig)
        }
    }


}