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
import cn.jianyun.worktime.ui.component.nav.EmptyDataView
import cn.jianyun.worktime.ui.graph.model.LineGraphData
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.util.mlog
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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random


@Composable
fun GraphBarView(monthStatData: LineGraphData){

    val modelProducer = remember { CartesianChartModelProducer.build() }

    var step by remember { mutableStateOf(monthStatData.step()) }

    if(monthStatData.datalist.size > 0){
        LaunchedEffect(monthStatData.datalist) {
            mlog("xData", monthStatData.unit,  monthStatData.datalist.size,  monthStatData.fetchXData(), monthStatData.fetchYData())


            withContext(Dispatchers.Default) {
                modelProducer.tryRunTransaction { columnSeries {
                    series(x = monthStatData.fetchXData(), y = monthStatData.fetchYData())
                } }
//                modelProducer.tryRunTransaction { columnSeries { series(x= listOf(1,2,3), y= listOf(4,5,6)) } }



                step = monthStatData.step()
            }
        }

        val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ ->
            if(x.toInt() >= 0 && monthStatData.datalist.size > x.toInt()) {
                monthStatData.datalist[x.toInt()].showDay
            }
            else{
                ""
            }
        }
        val marker = rememberMarker(valueFormatter = { ctx, dlist -> monthStatData.fetchMarker(dlist.first().x.toInt()) })
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider =
                    ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            color = ThemeColor,
                            thickness = 5.dp,
                            shape =
                            Shape.rounded(
                                topLeftDp = 10.2f,
                                topRightDp = 10.2f,
                            ),
                        ),
                    )
                ),
                startAxis = rememberStartAxis( tickLength = 2.dp),
//                bottomAxis = rememberBottomAxis(
//                    label= rememberAxisLabelComponent(padding = Dimensions.of(0.dp)),
//                    guideline = null,
//                    tickLength = 2.dp,
//                    valueFormatter = bottomAxisValueFormatter)
            ),

            modifier= Modifier
                .fillMaxWidth()
                .height(250.dp),
            modelProducer = modelProducer,
//            marker = marker,
            horizontalLayout = HorizontalLayout.fullWidth(unscalableStartPadding = 8.dp, unscalableEndPadding=8.dp),
//            getXStep = {step},
//            getXStep = ,
            zoomState = rememberVicoZoomState(zoomEnabled = false),
        )
    }
    else{
        EmptyDataView(label = "暂无数据")
    }


}
