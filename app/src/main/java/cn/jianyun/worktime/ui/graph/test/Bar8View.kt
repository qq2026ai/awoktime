package cn.jianyun.worktime.ui.graph.test


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.ui.graph.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEndAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.random.Random


/**
 * 多曲线面积图
 */
@Composable
fun Bar8View(modifier: Modifier = Modifier){

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
            rememberColumnCartesianLayer(
                columnProvider =
                ColumnCartesianLayer.ColumnProvider.series(
                    columnChartColors.map { color ->
                        rememberLineComponent(
                            color = color,
                            thickness = 8.dp,
                            shape = Shape.rounded(40),
                        )
                    },
                ),
                mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                verticalAxisPosition = AxisPosition.Vertical.Start,
            ),
            rememberLineCartesianLayer(
                lines =
                listOf(
                    rememberLineSpec(shader = DynamicShader.color(color4)),
                ),
                verticalAxisPosition = AxisPosition.Vertical.End,
            ),
            startAxis = rememberStartAxis(guideline = null),
            endAxis = rememberEndAxis(guideline = null),
            bottomAxis = rememberBottomAxis(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        runInitialAnimation = false,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}



private val color1 = Color(0xffa55a5a)
private val color2 = Color(0xffd3756b)
private val color3 = Color(0xfff09b7d)
private val color4 = Color(0xffffc3a1)
private val columnChartColors = listOf(color1, color2, color3)

