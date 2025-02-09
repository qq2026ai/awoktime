package cn.jianyun.worktime.ui.component.nav

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.jianyun.worktime.hilt.respo.BaseRepository
import cn.jianyun.worktime.util.mlog
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.vm.AppViewModel
import cn.jianyun.worktime.ui.component.form.DeleteDialog
import cn.jianyun.worktime.ui.component.form.GroupView
import cn.jianyun.worktime.ui.theme.DeleteColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class SwipeViewModel @Inject constructor(
    val baseRepository: BaseRepository
): ViewModel() {

    var focusId by mutableStateOf("")

    init {
        focusId = baseRepository.focusId
    }
    var sid by mutableStateOf(0)

    fun tryReload(){
        if(sid != baseRepository.sid){
            reload()
        }
    }

    fun reload(){
        viewModelScope.launch {
            sid = baseRepository.sid
            focusId = baseRepository.focusId
        }
    }

    fun change(fid: String){
        this.baseRepository.focusId = fid
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeRowView(
    sid: String = "",
    modifier: Modifier = Modifier,
    actionWidth: Dp, //锚点宽度
    startAction: List<@Composable BoxScope.() -> Unit> = listOf(), //左侧展开锚点
    startFillAction: (@Composable BoxScope.() -> Unit)? = null, //左侧全展开锚点
    endAction: List<@Composable BoxScope.() -> Unit> = listOf(), //右侧展开锚点
    endFillAction: (@Composable BoxScope.() -> Unit)? = null, //右侧全展开锚点
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val actionWidthPx = with(density) {
        actionWidth.toPx()
    }
    var vm = hiltViewModel<SwipeViewModel>()
    vm.tryReload()
    val startWidth = actionWidthPx * startAction.size //左侧锚点宽度
    val startActionSize = startAction.size + 1 //左侧锚点总数 = startAction + startFillAction
    val endWidth = actionWidthPx * endAction.size  //右侧锚点宽度
    val endActionSize = endAction.size + 1 //右侧锚点总数 =  endAction + endFillAction
    var contentWidth by remember { mutableFloatStateOf(0f) } //内容组件宽度
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var state = remember(startWidth, endWidth, contentWidth) {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            animationSpec = TweenSpec(durationMillis = 350),
            anchors = DraggableAnchors {
                DragAnchors.Start at (if (startFillAction != null) actionWidthPx else 0f) + startWidth //左侧全展开锚点宽度 + 左侧展开锚点宽度
                DragAnchors.StartFill at (if (startFillAction != null) contentWidth else 0f) + startWidth // 内容组件宽度 + 左侧展开锚点宽度
                DragAnchors.Center at 0f
                DragAnchors.End at (if (endFillAction != null) -actionWidthPx else 0f) - endWidth //右侧全展开锚点宽度 + 右侧展开锚点宽度
                DragAnchors.EndFill at (if (endFillAction != null) -contentWidth else 0f) - endWidth // 内容组件宽度 + 右侧展开锚点宽度
            },
            confirmValueChange = {
                mlog("move", it.name)
                if(it != DragAnchors.Start) {
                    vm.change(sid)
                }
                true
            },
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 50.dp.toPx() } },
        )
    }

    LaunchedEffect(vm.baseRepository.focusId) {
        withContext(Dispatchers.Default) {
            if(vm.baseRepository.focusId != sid && state.offset != 0f){
                mlog("reload", "sid", sid, "fid", vm.focusId, "offset", state.offset)
                state.animateTo(DragAnchors.Start, 0f)
            }
        }
    }

    Box(
        modifier = modifier
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Horizontal,
            )
            .clipToBounds()
    ) {
        startAction.forEachIndexed { index, action ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(actionWidth)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset <= actionWidthPx * startActionSize) {
                                (-actionWidthPx + state.offset / startActionSize * (startActionSize - index)).roundToInt()
                            } else {
                                (-actionWidthPx * (index + 1) + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                action()
            }
        }
        startFillAction?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset <= actionWidthPx * startActionSize) {
                                (-contentWidth + state.offset / startActionSize).roundToInt()
                            } else {
                                (-contentWidth - startWidth + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                it()
            }
        }
        endAction.forEachIndexed { index, action ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(actionWidth)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset >= -(actionWidthPx * endActionSize)) {
                                (actionWidthPx + state.offset / endActionSize * (endActionSize - index)).roundToInt()
                            } else {
                                (actionWidthPx * (index + 1) + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                action()
            }
        }
        endFillAction?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(with(density) {
                        contentHeight.toDp()
                    })
                    .offset {
                        IntOffset(
                            x = if (state.offset >= -(actionWidthPx * endActionSize)) {
                                (contentWidth + state.offset / endActionSize).roundToInt()
                            } else {
                                (contentWidth + endWidth + state.offset).roundToInt()
                            },
                            y = 0,
                        )
                    }
            ) {
                it()
            }
        }
        Box(
            modifier = Modifier
                .onSizeChanged {
                    contentWidth = it.width.toFloat()
                    contentHeight = it.height.toFloat()
                }
                .offset {
                    IntOffset(
                        x = state.offset.roundToInt(),
                        y = 0,
                    )
                }
        ) {
            content()
        }
    }
}

@Composable
fun SwipeDeleteView(dialog: Boolean = false, sid: String = "",  onEdit: () -> Unit, onDelete: () -> Unit, deleteMessage: String = "真的要删除吗？",  content: @Composable () -> Unit) {
    var showDeleteDialog by remember{ mutableStateOf(false) }
    var app = hiltViewModel<AppViewModel>()
    Box {
        GroupView(dialog=dialog, verticalPadding = 0.dp, horizonPadding = 0.dp, modifier = Modifier.clickable {
            onEdit()
        }) {
            SwipeRowView(
                sid=sid,
                modifier = Modifier
                    .radius(10.dp)
                    .fillMaxSize(),
                actionWidth = 100.dp, endAction = listOf{
                    Box(
                        modifier = Modifier
                            .background(DeleteColor)
                            .fillMaxSize()
                            .clickable {
                                showDeleteDialog = true
                            }
                    ) {
                        Text(
                            text = "删除",
                            modifier = Modifier.align(Alignment.Center),
                            color= Color.White
                        )
                    }
                }
            ) {
                content()
            }
        }
        if(showDeleteDialog){
            DeleteDialog(title=deleteMessage, okAction = {
                onDelete()
                app.baseRepository.focusId = ""
                showDeleteDialog = false
            }) {
                app.baseRepository.focusId = ""
                showDeleteDialog = false
            }
        }
    }
    
}

enum class DragAnchors { Start, StartFill, Center, End, EndFill }
