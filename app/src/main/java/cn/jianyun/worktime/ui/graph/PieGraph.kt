package cn.jianyun.worktime.ui.graph

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import cn.jianyun.worktime.plugin.razerdp.widget.animatedpieview.AnimatedPieView
import cn.jianyun.worktime.plugin.razerdp.widget.animatedpieview.AnimatedPieViewConfig
import cn.jianyun.worktime.plugin.razerdp.widget.animatedpieview.data.SimplePieInfo
import cn.jianyun.worktime.ui.graph.model.PieGraphData
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.util.toFloatData
import cn.jianyun.worktime.ui.component.nav.BoxView
import cn.jianyun.worktime.ui.component.nav.EmptyDataView
import cn.jianyun.worktime.ui.component.nav.VerticalRow
import cn.jianyun.worktime.ui.theme.ThemeColor


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PieGraph(gid: Int, pieData: PieGraphData) {

    var focusItem by remember { mutableStateOf(value ="") }

    var sid by remember { mutableStateOf(gid) }

    LaunchedEffect(gid){
        sid = gid
        mlog("gid changed")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if(pieData.isValid()){
            AndroidView(factory = {
                val graph = AnimatedPieView(it)
                graph
            }, modifier= Modifier
                .offset(y = -5.dp)
                .fillMaxWidth()
                .height(280.dp)
                .padding(10.dp, 0.dp), update = {
                var config = AnimatedPieViewConfig()
                config.startAngle(-90f)// 起始角度偏移

                var i = 0
                pieData.datalist.forEach{
                    config.addData(SimplePieInfo(it.name, it.value.toFloatData(2).toDouble(), it.color.color().toArgb(), it.label(i ++, pieData.datalist)))
                }
                config.drawText(true)
                config.textSize(30f)
                config.duration(100)
                config.guideLineWidth(5)
                config.guideLineMarginStart(12)
                config.cubicGuide(true)
                config.textMargin(1)
                config.textGravity(AnimatedPieViewConfig.ABOVE)
//                config.setOnPieSelectListener<SimplePieInfo>(OnPieSelectListener{k, v ->
//                    if(v){
//                        focusItem = k.uuid
//                    }
//                    else{
//                        focusItem = ""
//                    }
//                })
                it.applyConfig(config)
                mlog("gid", gid, sid)
                it.start()
            })

            FlowRow {
                pieData.datalist.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier= Modifier
                        .radius(4.dp)
                        .fillMaxWidth(0.5f)
                        .padding(6.dp, 1.dp)
                    ){
                        BoxView(width = 6.dp, height = 6.dp, background = it.color.color(), radius = 3.dp)
                        Blank(2.dp)
                        Text(it.showName(), fontSize = 12.sp, lineHeight = 12.sp)
                        Blank(2.dp)
                        Text("${it.value}${pieData.unit}", fontSize = 12.sp, lineHeight = 12.sp)
                        Blank(2.dp)
                        Text("(${it.percentValue()})", fontSize = 10.sp, lineHeight = 10.sp, color= Color.Gray)
                    }
                }
            }
        }
        else{
            EmptyDataView(label = "暂无数据")
        }
    }
}