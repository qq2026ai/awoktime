package cn.jianyun.worktime.ui.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.util.radius
import cn.jianyun.worktime.ui.theme.DeleteColor
import cn.jianyun.worktime.ui.theme.PrimaryColor
import cn.jianyun.worktime.ui.theme.ThemeColor
import cn.jianyun.worktime.ui.theme.VipColor


@Composable
fun LinkButton(label: String = "确定", width: Dp = 60.dp, height: Dp = 25.dp, fontSize: TextUnit = 13.sp, background: Color= ThemeColor, color:Color = Color.White, action: () -> Unit) {

    Text(label,modifier = Modifier.radius(4.dp).clickable {
        action()
    }.width(width).height(height).background(background).wrapContentHeight().wrapContentWidth(), lineHeight=fontSize, color=color, fontSize=fontSize)

}

@Composable
fun LongOkButton(label: String = "确定", action: () -> Unit) {

    Button(onClick = action, modifier = Modifier.fillMaxWidth(),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= ThemeColor, contentColor = Color.White)
        ) {
        Text(label, modifier = Modifier.padding(vertical = 5.dp))
    }

}

@Composable
fun LongOk2Button(label: String = "确定", action: () -> Unit) {

    Button(onClick = action, modifier = Modifier.fillMaxWidth(),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= PrimaryColor, contentColor = Color.White)
    ) {
        Text(label, modifier = Modifier.padding(vertical = 5.dp))
    }

}


@Composable
fun LongVipButton(label: String = "确定", action: () -> Unit) {

    Button(onClick = action, modifier = Modifier.fillMaxWidth(),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= VipColor, contentColor = Color.Black)
    ) {
        Text(label, modifier = Modifier.padding(vertical = 5.dp))
    }

}


@Composable
fun LongCancelButton(label: String = "取消", fration:Float = 1f, action: () -> Unit) {
    Button(onClick = action, modifier = Modifier.fillMaxWidth(fration),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(label, modifier = Modifier.padding(vertical = 5.dp))
    }
}

@Composable
fun CancelButton(label: String = "取消", modifier:Modifier=Modifier, action: () -> Unit) {
    Button(onClick = action, modifier = Modifier.then(modifier),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(label, modifier = Modifier.padding(vertical = 3.dp))
    }
}
@Composable
fun Cancel2Button(label: String = "取消", modifier:Modifier=Modifier, action: () -> Unit) {
    Button(onClick = action, modifier = Modifier.then(modifier),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(label, modifier = Modifier.padding(vertical = 3.dp))
    }
}


@Composable
fun OkButton(label: String = "确认", modifier:Modifier=Modifier, action: () -> Unit) {
    Button(onClick = action, modifier = Modifier.then(modifier),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= ThemeColor, contentColor = Color.White)
    ) {
        Text(label, modifier = Modifier.padding(vertical = 3.dp))
    }
}


@Composable
fun LongDeleteButton(label: String = "删除", action: () -> Unit) {

    Button(onClick = action, modifier = Modifier.fillMaxWidth(),
        shape= RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.error, contentColor = Color.White)
    ) {
        Text(label, modifier = Modifier.padding(vertical = 5.dp))
    }

}


@Composable
fun DeleteOrHideButton(hideLabel: String = "隐藏",deleteLabel: String = "删除", hideAction: () -> Unit,deleteAction: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = hideAction, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= PrimaryColor)
        ) {
            Text(hideLabel, color = Color.White, modifier = Modifier.padding(vertical = 5.dp))
        }
        Spacer(Modifier.size(10.dp))
        Button(onClick = deleteAction, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= DeleteColor)
        ) {
            Text(deleteLabel, color = Color.White, modifier = Modifier.padding(vertical = 5.dp))
        }
    }

}

@Composable
fun OkAndCancelButtonGroup(okLabel: String = "确定", cancelLabel: String = "取消", padding:Dp = 0.dp,  hpadding:Dp = 0.dp,  okAction: () -> Unit, cancelAction: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(padding).padding(horizontal = hpadding)) {
        Button(onClick = cancelAction, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(cancelLabel, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 2.dp))
        }
        Spacer(Modifier.size(10.dp))
        Button(onClick = okAction, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= ThemeColor, contentColor = Color.White)
        ) {
            Text(okLabel, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

@Composable
fun DoubleButtonGroup(label1: String = "按钮1", label2: String = "按钮2", okAction1: () -> Unit, okAction2: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = okAction1, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(label1, modifier = Modifier.padding(vertical = 5.dp))
        }
        Spacer(Modifier.size(10.dp))
        Button(onClick = okAction2, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(label2, modifier = Modifier.padding(vertical = 5.dp))
        }
    }
}

@Composable
fun OkAndDeleteButtonGroup(okLabel: String = "确定", deleteLabel: String = "删除", okAction: () -> Unit, cancelAction: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = cancelAction, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.error, contentColor = Color.White)
        ) {
            Text(deleteLabel, modifier = Modifier.padding(vertical = 5.dp))
        }
        Spacer(Modifier.size(10.dp))
        Button(onClick = okAction, modifier = Modifier.weight(1f, true),
            shape= RoundedCornerShape(12.dp),
            colors= ButtonDefaults.buttonColors(containerColor= ThemeColor, contentColor = Color.White)
        ) {
            Text(okLabel, modifier = Modifier.padding(vertical = 5.dp))
        }
    }

}



fun Modifier.tap(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}