//package cn.jianyun.worktime.ui.graph
//
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import cn.jianyun.worktime.ui.component.nav.EmptyDataView
//import cn.jianyun.worktime.ui.graph.model.LineGraphData
//import cn.jianyun.worktime.ui.theme.ThemeColor
//import cn.jianyun.worktime.util.mlog
//import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
//import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
//import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
//import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
//import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
//import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
//import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
//import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
//import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
//import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
//import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
//import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
//import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
//import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
//import com.patrykandpatrick.vico.core.common.shape.Shape
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//
//@Composable
//fun GraphBarView(monthStatData: LineGraphData){
//
//    val modelProducer = remember { CartesianChartModelProducer() }
//
//    var step by remember { mutableStateOf(monthStatData.step()) }
//    var textColor = Color.Gray
//    if(monthStatData.datalist.size > 0){
//        LaunchedEffect(monthStatData.datalist) {
//            mlog(
//                "xData",
//                monthStatData.unit,
//                monthStatData.datalist.size,
//                monthStatData.fetchXData(),
//                monthStatData.fetchYData()
//            )
//
//            withContext(Dispatchers.Default) {
//                modelProducer.runTransaction {
//                    columnSeries {
//                        series(x = monthStatData.fetchXData(), y = monthStatData.fetchYData())
//                    }
//                    step = monthStatData.step()
//                }
//            }
//        }
//
////        val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ ->
////            if(x.toInt() >= 0 && monthStatData.datalist.size > x.toInt()) {
////                monthStatData.datalist[x.toInt()].showDay
////            }
////            else{
////                ""
////            }
////        }
////        val marker = rememberMarker(valueFormatter = { ctx, dlist -> monthStatData.fetchMarker(dlist.first().x.toInt()) })
////        CartesianChartHost(
////            chart =
////            rememberCartesianChart(
////                rememberLineCartesianLayer(
////                    listOf(rememberLineSpec(DynamicShader.color(ThemeColor))),
////                    spacing = 2.dp
////                ),
////                startAxis = rememberStartAxis( tickLength = 2.dp,
////                    label = rememberAxisLabelComponent(color=textColor),
////                    axis = rememberAxisGuidelineComponent(color = textColor, shape= Shape.Rectangle)
////                ),
////                bottomAxis = rememberBottomAxis(
////                    label=rememberAxisLabelComponent(padding = Dimensions.of(0.dp), color = textColor),
////                    guideline = null,
////                    axis = rememberAxisGuidelineComponent(color = textColor, shape= Shape.Rectangle),
////                    tickLength = 2.dp, valueFormatter = bottomAxisValueFormatter)
////            ),
////            modifier= Modifier
////                .fillMaxWidth()
////                .height(250.dp),
////            modelProducer = modelProducer,
////            marker = marker,
////            horizontalLayout = HorizontalLayout.fullWidth(unscalableStartPadding = 8.dp, unscalableEndPadding=8.dp),
////            getXStep = {step},
////            zoomState = rememberVicoZoomState(zoomEnabled = false),
////        )
//          return  CartesianChartHost(
//                chart =
//                rememberCartesianChart(
//                    rememberColumnCartesianLayer(),
//                    startAxis = VerticalAxis.rememberStart(),
//                    bottomAxis = HorizontalAxis.rememberBottom(),
//                ),
//                modelProducer = modelProducer,
//            )
//    }
//    else{
//        EmptyDataView(label = "暂无数据")
//    }
//
//
//}
