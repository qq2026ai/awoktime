package cn.jianyun.worktime.ui.graph.test


import android.graphics.PorterDuff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.jianyun.worktime.ui.graph.rememberMarker
import cn.jianyun.worktime.util.randomColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shader.component
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.compose.common.shape.dashed
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.TopBottomShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun Bar9View(modifier: Modifier = Modifier){

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


    val colors = chartColors
    val marker = rememberMarker()
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lines =
                listOf(
                    rememberLineSpec(
                        shader =
                        TopBottomShader(
                            DynamicShader.color(colors[0]),
                            DynamicShader.color(colors[1]),
                        ),
                        backgroundShader =
                        TopBottomShader(
                            DynamicShader.compose(
                                DynamicShader.component(
                                    componentSize = 6.dp,
                                    component =
                                    rememberShapeComponent(
                                        shape = Shape.Pill,
                                        color = colors[0],
                                        margins = Dimensions.of(1.dp),
                                    ),
                                ),
                                DynamicShader.verticalGradient(
                                    arrayOf(Color.Black, Color.Transparent),
                                ),
                                PorterDuff.Mode.DST_IN,
                            ),
                            DynamicShader.compose(
                                DynamicShader.component(
                                    componentSize = 5.dp,
                                    component =
                                    rememberShapeComponent(
                                        shape = Shape.Rectangle,
                                        color = colors[1],
                                        margins = Dimensions.of(horizontal = 2.dp),
                                    ),
                                    checkeredArrangement = false,
                                ),
                                DynamicShader.verticalGradient(
                                    arrayOf(Color.Transparent, Color.Black),
                                ),
                                PorterDuff.Mode.DST_IN,
                            ),
                        ),
                    ),
                ),
            ),
            startAxis =
            rememberStartAxis(
                label =
                rememberAxisLabelComponent(
                    color = MaterialTheme.colorScheme.onBackground,
                    background =
                    rememberShapeComponent(
                        shape = Shape.Pill,
                        color = Color.Transparent,
                        strokeColor = MaterialTheme.colorScheme.outlineVariant,
                        strokeWidth = 1.dp,
                    ),
                    padding = Dimensions.of(horizontal = 6.dp, vertical = 2.dp),
                    margins = Dimensions.of(end = 8.dp),
                ),
                axis = null,
                tick = null,
                guideline =
                rememberLineComponent(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape =
                    remember {
                        Shape.dashed(
                            shape = Shape.Pill,
                            dashLength = 4.dp,
                            gapLength = 8.dp,
                        )
                    },
                ),
                itemPlacer = remember { AxisItemPlacer.Vertical.count(count = { 4 }) },
            ),
            bottomAxis =
            rememberBottomAxis(
                guideline = null,
                itemPlacer =
                remember {
                    AxisItemPlacer.Horizontal.default(
                        spacing = 3,
                        addExtremeLabelPadding = true,
                    )
                },
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = marker,
        runInitialAnimation = false,
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}




private val chartColors
    @ReadOnlyComposable
    @Composable
    get() =
        listOf(
            randomColor(102),
            randomColor(202)
        )

private val x = (1..100).toList()