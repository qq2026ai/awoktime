package cn.jianyun.worktime.ui.graph.test


import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.ui.graph.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalBox
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun Bar6View(modifier: Modifier = Modifier){

    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    columnSeries {
                        repeat(10) {
                            series(
                                List(10) {
                                    200L +
                                            Random.nextFloat() * 300
                                },
                            )
                        }
                    }
                }
                delay(3000L)
            }
        }
    }


    val horizontalBox = rememberHorizontalBox()
    val shape = remember { Shape.cut(topLeftPercent = 50) }
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    columnColors.map { rememberLineComponent(color = it, thickness = 8.dp, shape = shape) },
                ),
            ),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
            decorations = remember(horizontalBox) { listOf(horizontalBox) },
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        runInitialAnimation = false,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )


}


@Composable
private fun rememberHorizontalBox() =
    rememberHorizontalBox(
        y = { 7f..14f },
        box = rememberShapeComponent(color = horizontalBoxColor.copy(.36f)),
        labelComponent =
        rememberTextComponent(
            color = Color.Black,
            background = rememberShapeComponent(Shape.Rectangle, horizontalBoxColor),
            padding = Dimensions.of(8.dp, 2.dp),
            margins = Dimensions.of(4.dp),
            typeface = Typeface.MONOSPACE,
        ),
    )

private val columnColors = listOf(Color(0xff3e6558), Color(0xff5e836a), Color(0xffa5ba8e))
private val horizontalBoxColor = Color(0xffe9e5af)
private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ -> daysOfWeek[x.toInt() % daysOfWeek.size] }