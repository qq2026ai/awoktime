package cn.jianyun.worktime.ui.graph.test


import android.annotation.SuppressLint
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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Defaults.AXIS_LABEL_ROTATION_DEGREES
import com.patrykandpatrick.vico.core.common.Defaults.COLUMN_ROUNDNESS_PERCENT
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * 叠加图
 */
@SuppressLint("RestrictedApi")
@Composable
fun Bar5View(modifier:Modifier = Modifier){

    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    columnSeries {
                        repeat(3) {
                            series(
                                List(10) {
                                    10 +
                                            Random.nextFloat() * 200
                                },
                            )
                        }
                    }
                }
                delay(2000L)
            }
        }
    }

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider =
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        color = color1,
                        thickness = COLUMN_THICKNESS_DP.dp,
                        shape =
                        Shape.rounded(
                            bottomLeftPercent = COLUMN_ROUNDNESS_PERCENT,
                            bottomRightPercent = COLUMN_ROUNDNESS_PERCENT,
                        ),
                    ),
                    rememberLineComponent(
                        color = color2,
                        thickness = COLUMN_THICKNESS_DP.dp,
                    ),
                    rememberLineComponent(
                        color = color3,
                        thickness = COLUMN_THICKNESS_DP.dp,
                        shape =
                        Shape.rounded(
                            topLeftPercent = COLUMN_ROUNDNESS_PERCENT,
                            topRightPercent = COLUMN_ROUNDNESS_PERCENT,
                        ),
                    ),
                ),
                mergeMode = { ColumnCartesianLayer.MergeMode.Grouped },
            ),
            startAxis =
            rememberStartAxis(
                itemPlacer = startAxisItemPlacer,
                labelRotationDegrees = AXIS_LABEL_ROTATION_DEGREES,
            ),
            bottomAxis = rememberBottomAxis(labelRotationDegrees = AXIS_LABEL_ROTATION_DEGREES),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        runInitialAnimation = false,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}




private const val COLUMN_ROUNDNESS_PERCENT: Int = 40
private const val COLUMN_THICKNESS_DP: Int = 10
private const val AXIS_LABEL_ROTATION_DEGREES = 45f

private val color1 = Color(0xff6438a7)
private val color2 = Color(0xff3490de)
private val color3 = Color(0xff73e8dc)
private val startAxisItemPlacer = AxisItemPlacer.Vertical.count({ 3 })