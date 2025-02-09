package cn.jianyun.worktime.ui.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cn.jianyun.worktime.ui.graph.model.LineGraphData
import cn.jianyun.worktime.util.Blank
import cn.jianyun.worktime.util.color
import cn.jianyun.worktime.util.ifv
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.component.nav.BoxView
import cn.jianyun.worktime.ui.component.nav.CenterRow
import cn.jianyun.worktime.ui.component.nav.EmptyDataView
import cn.jianyun.worktime.ui.component.nav.SmallLinkText
import cn.jianyun.worktime.ui.component.nav.TwoColumnView
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupLineView(gid: Int, monthStatDatas: List<LineGraphData>){

    val modelProducer = remember { CartesianChartModelProducer.build() }
    var step by remember {
        mutableStateOf(1f)
    }

    if(monthStatDatas.size > 0){
        LaunchedEffect(gid) {
            withContext(Dispatchers.Default) {
                modelProducer.tryRunTransaction { lineSeries {
                    monthStatDatas.map{
                        series(x = it.fetchXData(), y = it.fetchYData())
                    }
                }}
                step = monthStatDatas.get(0).step()
            }
        }

        val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ ->
            if(x.toInt() >= 0 && monthStatDatas[0].datalist.size > x.toInt()) {
                monthStatDatas[0].datalist[x.toInt()].showDay
            }
            else{
                ""
            }
        }
        val marker = rememberMarker(valueFormatter = { ctx, dlist ->
            mlog("start", )
            if(dlist.isNotEmpty() && dlist[0] is LineCartesianLayerMarkerTarget){
                val target = dlist[0] as LineCartesianLayerMarkerTarget
                "${monthStatDatas[0].fetchMarker(target.points[0].entry.x.toInt())}\n" +
                 "${monthStatDatas[1].fetchMarker(target.points[0].entry.x.toInt())}"
            }
            else{
                ""
            }
        })

        Box(contentAlignment = Alignment.TopStart){
            TwoColumnView(padding=5.dp) {
                FlowRow {
                    monthStatDatas.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier= Modifier
                            .radius(4.dp)
                            .padding(6.dp, 1.dp)
                        ){
                            BoxView(width = 6.dp, height = 6.dp, background = it.color.color(), radius = 3.dp)
                            Blank(2.dp)
                            Text(it.name, fontSize = 12.sp, lineHeight = 12.sp)
                        }
                    }
                }
            }
            CartesianChartHost(
                chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lines = listOf(rememberLineSpec(DynamicShader.color(monthStatDatas.get(0).color.color())),
                            rememberLineSpec(DynamicShader.color(monthStatDatas.get(1).color.color()))),
                        spacing = 2.dp
                    ),
                    startAxis = rememberStartAxis( tickLength = 2.dp),
                    bottomAxis = rememberBottomAxis(
                        label=rememberAxisLabelComponent(padding = Dimensions.of(0.dp)),
                        guideline = null,
                        tickLength = 2.dp, valueFormatter = bottomAxisValueFormatter)
                ),

                modifier= Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                modelProducer = modelProducer,
                marker = marker,
                horizontalLayout = HorizontalLayout.fullWidth(unscalableStartPadding = 8.dp, unscalableEndPadding=8.dp),
                getXStep = {step},
                zoomState = rememberVicoZoomState(zoomEnabled = false),
            )
        }


    }
    else{
        EmptyDataView(label = "暂无数据")
    }

}
