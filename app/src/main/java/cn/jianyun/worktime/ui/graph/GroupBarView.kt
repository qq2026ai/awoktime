package cn.jianyun.worktime.ui.graph

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.ui.graph.model.LineGraphData
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GroupBarView(monthStatDatas: List<LineGraphData>){

    val modelProducer = remember { CartesianChartModelProducer.build() }
    var step by remember {
        mutableStateOf(1f)
    }

    if(monthStatDatas.size > 0){
        LaunchedEffect(monthStatDatas.size) {
            withContext(Dispatchers.Default) {
                modelProducer.tryRunTransaction { columnSeries {
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
            if(dlist.isNotEmpty() && dlist[0] is ColumnCartesianLayerMarkerTarget){
                val target = dlist[0] as ColumnCartesianLayerMarkerTarget
//                mlog(target.columns[0].entry.x, target.columns[0].entry.y)
//                "${target.canvasX}"

                "${monthStatDatas[0].fetchMarker(target.columns[0].entry.x.toInt())}\n" +
                 "${monthStatDatas[1].fetchMarker(target.columns[0].entry.x.toInt())}"
            }
            else{
                ""
            }
        })


        CartesianChartHost(
            chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider =
                    ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            color = ThemeColor,
                            thickness = 5.dp,
                            shape =
                            Shape.rounded(
                                bottomLeftPercent = 0,
                                bottomRightPercent = 0,
                            )
                        ),
                        rememberLineComponent(
                            color = DeleteColor,
                            thickness = 5.dp,
                            shape =
                            Shape.rounded(
                                topLeftPercent = 20,
                                topRightPercent = 20,
                            ),
                        ),
                    ),
                    spacing = 2.dp,
                    mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
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


private const val COLUMN_THICKNESS_DP: Int = 10

private val startAxisItemPlacer = AxisItemPlacer.Vertical.count({ 2 })