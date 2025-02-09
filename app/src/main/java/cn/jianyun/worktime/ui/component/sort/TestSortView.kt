package cn.jianyun.worktime.ui.component.sort

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.util.SelectDO
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mainBg
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.theme.ThemeColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update



@Composable
fun DragNDropItemsList(
    items: List<SelectDO>,
    renderAction:@Composable ((String) -> Unit)? = null,
    onChange: (List<SelectDO>) -> Unit
) {
    val mutableUiState =
        MutableStateFlow(items)
    val uiState = mutableUiState.asStateFlow().collectAsState()

    fun swapItems(from: Int, to: Int) {
        val fromItem = mutableUiState.value[from]
        val toItem = mutableUiState.value[to]
        val newList = mutableUiState.value.toMutableList()
        newList[from] = toItem
        newList[to] = fromItem
        mutableUiState.update { newList }
        onChange(newList)
    }

    DragDropColumn(
        items = uiState.value,
        onSwap = ::swapItems
    ) { item, isDrag ->
        TwoColumnView(modifier=Modifier.mainBg(bg=ifv(isDrag, ThemeColor.copy(0.2f), MaterialTheme.colorScheme.surface)).padding(12.dp, 8.dp)) {
            if(renderAction != null){
                renderAction(item.value)
            }
            else{
                Text(text = item.label)
            }
            IconView(icon = IconFont.list_indefinite)
        }
    }
}