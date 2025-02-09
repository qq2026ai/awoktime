package cn.jianyun.worktime.plugin.razerdp.widget.animatedpieview.data;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * Created by 大灯泡 on 2017/11/7.
 */

public interface IPieInfo {

    double getValue();

    @ColorInt
    int getColor();

    String getDesc();

    @Nullable
    PieOption getPieOption();
}
