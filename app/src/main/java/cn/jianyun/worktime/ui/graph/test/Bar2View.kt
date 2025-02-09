package cn.jianyun.worktime.ui.graph.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.jianyun.worktime.ui.graph.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun Bar2View(modifier: Modifier = Modifier){

    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.tryRunTransaction { lineSeries {
                series(x = listOf(1, 2, 3, 4), y = listOf(2, 5, 3, 4))
            } }
        }
    }

    val marker = rememberMarker()
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(listOf(rememberLineSpec(DynamicShader.color(Color(0xffa485e0))))),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(guideline = null)
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = marker,
        zoomState = rememberVicoZoomState(zoomEnabled = true),
    )

}

