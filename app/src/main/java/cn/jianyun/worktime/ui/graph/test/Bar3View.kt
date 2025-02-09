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
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.DateFormatSymbols
import java.util.Locale
import kotlin.random.Random

@Composable
fun Bar3View(modifier:Modifier = Modifier){

    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction { columnSeries { series(List(47) { 2 + Random.nextFloat() * 18 }) } }
                delay(2000L)
            }
        }
    }

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        color = Color(0xffff5500),
                        thickness = 16.dp,
                        shape = remember { Shape.rounded(allPercent = 40) },
                    ),
                ),
            ),
            startAxis = rememberStartAxis(),
            bottomAxis =
            rememberBottomAxis(
                valueFormatter = bottomAxisValueFormatter,
                itemPlacer =
                remember { AxisItemPlacer.Horizontal.default(spacing = 3, addExtremeLabelPadding = true) },
            ),
            decorations = listOf(rememberComposeHorizontalLine()),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )

}



@Composable
private fun rememberComposeHorizontalLine(): HorizontalLine {
    val color = Color(HORIZONTAL_LINE_COLOR)
    return rememberHorizontalLine(
        y = { HORIZONTAL_LINE_Y },
        line = rememberLineComponent(color, HORIZONTAL_LINE_THICKNESS_DP.dp),
        labelComponent =
        rememberTextComponent(
            background = rememberShapeComponent(Shape.Pill, color),
            padding =
            Dimensions.of(
                HORIZONTAL_LINE_LABEL_HORIZONTAL_PADDING_DP.dp,
                HORIZONTAL_LINE_LABEL_VERTICAL_PADDING_DP.dp,
            ),
            margins = Dimensions.of(HORIZONTAL_LINE_LABEL_MARGIN_DP.dp),
            typeface = Typeface.MONOSPACE,
        ),
    )
}


private fun getViewHorizontalLine() =
    HorizontalLine(
        y = { HORIZONTAL_LINE_Y },
        line = LineComponent(HORIZONTAL_LINE_COLOR, HORIZONTAL_LINE_THICKNESS_DP),
        labelComponent =
        TextComponent.build {
            background = ShapeComponent(Shape.Pill, HORIZONTAL_LINE_COLOR)
            padding =
                Dimensions(
                    HORIZONTAL_LINE_LABEL_VERTICAL_PADDING_DP,
                    HORIZONTAL_LINE_LABEL_HORIZONTAL_PADDING_DP,
                )
            margins = Dimensions(HORIZONTAL_LINE_LABEL_MARGIN_DP)
            typeface = Typeface.MONOSPACE
        },
    )



private const val HORIZONTAL_LINE_Y = 14f
private const val HORIZONTAL_LINE_COLOR = -2893786
private const val HORIZONTAL_LINE_THICKNESS_DP = 2f
private const val HORIZONTAL_LINE_LABEL_HORIZONTAL_PADDING_DP = 8f
private const val HORIZONTAL_LINE_LABEL_VERTICAL_PADDING_DP = 2f
private const val HORIZONTAL_LINE_LABEL_MARGIN_DP = 4f


private val monthNames = DateFormatSymbols.getInstance(Locale.US).shortMonths
private val bottomAxisValueFormatter =
    CartesianValueFormatter { x, _, _ -> "${monthNames[x.toInt() % 12]} ’${20 + x.toInt() / 12}" }