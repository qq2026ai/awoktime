package cn.jianyun.worktime.ui.graph.test


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@SuppressLint("RestrictedApi")
@Composable
fun BarTestView(modifier: Modifier = Modifier){

    Column(modifier = modifier){
        Bar2View()
        Bar3View()
        Bar4View()
        Bar5View()
        Bar6View()
        Bar7View()
        Bar8View()
        Bar9View()
    }


}


