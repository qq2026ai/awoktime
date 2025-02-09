package cn.jianyun.worktime.ui.graph.test


import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.ui.graph.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun Bar7View(modifier: Modifier = Modifier){

    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    lineSeries {
                        repeat(3) {
                            series(
                                List(10) {
                                   100 +
                                            Random.nextFloat() *  200
                                },
                            )
                        }
                    }
                }
                delay(3000L)
            }
        }
    }

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lines =
                chartColors.map { color ->
                    rememberLineSpec(
                        shader = DynamicShader.color(color),
                        backgroundShader = null,
                    )
                },
            ),
            startAxis =
            rememberStartAxis(
                label = rememberStartAxisLabel(),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
            ),
            bottomAxis = rememberBottomAxis(),
            legend = rememberLegend(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        runInitialAnimation = false,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )

}


@Composable
private fun rememberStartAxisLabel() =
    rememberAxisLabelComponent(
        color = Color.Black,
        background =
        rememberShapeComponent(
            shape = Shape.rounded(4.dp),
            color = Color(0xfffab94d),
        ),
        padding = Dimensions.of(horizontal = 8.dp, vertical = 2.dp),
        margins = Dimensions.of(all = 4.dp),
    )

@Composable
private fun rememberLegend() =
    rememberVerticalLegend<CartesianMeasureContext, CartesianDrawContext>(
        items =
        chartColors.mapIndexed { index, chartColor ->
            rememberLegendItem(
                icon = rememberShapeComponent(Shape.Pill, chartColor),
                label =
                rememberTextComponent(
                    color = vicoTheme.textColor,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = "x:${index + 1}",
            )
        },
        iconSize = 8.dp,
        iconPadding = 8.dp,
        spacing = 4.dp,
        padding = Dimensions.of(top = 8.dp),
    )

private val chartColors = listOf(Color(0xffb983ff), Color(0xff91b1fd), Color(0xff8fdaff))