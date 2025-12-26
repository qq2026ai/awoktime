package cn.jianyun.worktime.module.timework.views.biz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jianyun.worktime.module.timework.dto.TimeworkShownData
import cn.jianyun.worktime.ui.component.nav.IconFont
import cn.jianyun.worktime.ui.component.nav.IconView
import cn.jianyun.worktime.util.radius


@Composable
fun TimeworkCellView(data: TimeworkShownData){

    //判断数据情况
    if(!data.isEmpty()) {
        Column(modifier=Modifier.padding(horizontal = 3.dp).padding(vertical =  2.dp).fillMaxWidth().radius(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            if(data.showHour){
                Text(data.fetchShownHour(), color= Color.White,  maxLines = 1, fontSize = data.fetchFontSize(), lineHeight = 12.sp, modifier=Modifier.background(data.fetchRealFirstColor()).fillMaxWidth().height((data.hourSize + 6).dp).wrapContentSize())
            }
            if(data.showMoney){
                Text(data.getTotalMoney(),
                    lineHeight = 12.sp,
                    maxLines = 1,
                    color= Color.White,
                    modifier=Modifier.background(data.moneyColor).padding(3.dp, 1.dp).radius(2.dp).height((data.moneySize + 6).dp).fillMaxWidth().wrapContentSize(),
                    fontSize = data.moneySize.sp)
            }

        }
    }
    else{
        if(data.leave || data.rest){
            Column(modifier=Modifier.padding(horizontal = 3.dp).radius(2.dp).background(data.fetchRealFirstColor()).padding(vertical =  2.dp).fillMaxWidth().wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                Text(data.fetchShownHour(), color= Color.White,  maxLines = 1, fontSize = data.fetchFontSize(), lineHeight = 11.sp, modifier=Modifier.height((data.hourSize + 6).dp).wrapContentHeight())
            }
        }
    }


}