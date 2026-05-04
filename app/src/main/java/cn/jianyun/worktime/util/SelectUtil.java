package cn.jianyun.worktime.util;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SelectUtil {



    public static List<SelectDO> initValues(String...args){
        List<SelectDO> opts = new ArrayList<>();
        for (int i = 0; i < args.length; ) {
            if(i < args.length - 1){
                opts.add(new SelectDO(args[i], args[i+1]));
            }
            i += 2;
        }
        return opts;
    }

    public static List<SelectDO> initValuesAndColor(String...args){
        List<SelectDO> opts = new ArrayList<>();
        for (int i = 0; i < args.length; ) {
            if(i < args.length - 2){
                opts.add(new SelectDO(args[i], args[i+1], args[i+2]));
            }
            i += 3;
        }
        return opts;
    }
    public static List<SelectDO> initSingleValues(String...args){
        List<SelectDO> opts = new ArrayList<>();
        for (int i = 0; i < args.length; i ++) {
            opts.add(new SelectDO(args[i], args[i]));
        }
        return opts;
    }

    public static List<SelectDO> getFromDays(){
        List<SelectDO> one = new ArrayList<>();
        one.add(new SelectDO("本月1号到月底", "1"));
        for (int i = 2; i <= 28; i++) {
            one.add(new SelectDO("本月" + i + "号到次月" + (i-1) + "号", "" + i));
        }
        return one;
    }

    public static final List<SelectDO> BACK_TYPES = SelectUtil.initValues("本地备份", "local", "云备份", "cloud");
    public static final List<SelectDO> FEEDBACK_TYPES = SelectUtil.initValues("功能缺陷", "bug", "产品建议", "advice");
    public static final List<SelectDO> DATE_CHOOSE_TYPES = initValues("月度", "month", "年度", "year", "自定义", "self");
    public static final List<SelectDO> HOME_STAT_TYPES = initValues(
            "正班工时", "baseHour",
            "加班工时", "overHour",
            "总工时", "totalHour",
            "日结工时", "dayHour",
            "补扣金额", "awardMoney",
            "补贴金额", "pureAwardMoney",
            "扣款金额", "fineMoney",
            "日结收入", "dayMoney",
            "日结次数", "dayCount",
            "出勤天数", "totalDay",
            "总收入", "totalMoney",
            "已结算", "settledMoney",
            "待结算", "unSettledMoney"
    );

    public static final List<SelectDO> MONDAY_OR_SUNDAY = initValues("周一", "monday", "周日", "sunday");
    public static final List<SelectDO> EDIT_TYPES = initValues("编辑模式", "edit", "检查模式", "check");
    public static final List<SelectDO> ALL_SIGN_TYPES = initValues("工时", "hour","日结", "day", "时间", "time", "休息", "rest", "请假", "leave");
    public static final List<SelectDO> WORK_DATA_TYPES = initValues("按工时", "hour","按日结", "day", "按时间", "time", "按补扣", "award");
    public static final List<SelectDO> SIGN_TYPES = initValues("工时", "hour","日结", "day", "时间", "time");
    public static final List<SelectDO> LEAVE_TYPES = initValues("请假", "leave","休息", "rest");
    public static final List<SelectDO> DEFAULT_TYPES = initValuesAndColor("打卡", "sign", "#776AD4", "补贴", "award", "#1974E8", "扣款", "fine", "#F87679");
    public static final List<SelectDO> AWARD_TYPES = initValues("补贴", "award", "扣款", "fine");
    public static final List<SelectDO> SALARY_TYPES = initValues("正班薪水", "normal", "加班薪水", "over");
    public static final List<SelectDO> SALARY_CALC_TYPES = initValues("固定工时费", "fix", "按月计算", "month");
    public static final List<SelectDO> OVER_SALARY_CALC_TYPES = initValues( "按倍数", "times", "固定工时费", "fix");
    public static final List<SelectDO> REST_TYPES = initValues("单休(25.75天)", "25.75", "单休(26天)", "26", "单双休(23.75天)", "23.75", "单双休(24天)", "24", "双休(21.75天)", "21.75", "双休(22天)", "22");
    public static final List<SelectDO> GIFT_TYPES = initValues( "收礼", "receive", "送礼", "send");
    public static final List<SelectDO> LOGIN_TYPES = initValues("登录", "login", "注册", "regist");
    public static final List<SelectDO> GIFT_ALL_TYPES = initValues("全部", "", "送礼", "send", "收礼", "receive");
    public static final List<SelectDO> BORROW_TYPES = initValues("借出", "send", "借进", "receive");
    public static final List<SelectDO> RELATIONS = initSingleValues("亲属", "同学", "朋友", "领导", "同事");
    public static final List<SelectDO> GIFT_PAY_TYPES = initSingleValues("支付宝", "微信", "银行卡", "现金");
    public static final List<SelectDO> BORROW_TIMES = initWithUnit("", "次", 1, 360);
    public static final List<SelectDO> BORROW_NOTIFY_TYPES = initValues("无提醒", "none", "分期", "period", "自定义", "self");
    public static final List<SelectDO> GIFT_REASON_TYPES = initSingleValues("结婚", "乔迁", "生日");
    public static final List<SelectDO> IDENTITYS = initSingleValues("本科", "硕士", "专科", "教师");
    public static final List<SelectDO> COLOR_MODES = initValues("中国色", "chinese", "自定义", "self", "我收藏的", "collect");
    public static final List<SelectDO> JOBS = initValues("学生", "student", "教师", "teacher", "其他", "other");
    public static final List<SelectDO> THEMES = initValues("亮白主题", "light", "暗黑主题", "dark", "追随系统", "auto");
    public static final List<SelectDO> WEEKDAYS = initValues("周一", "2", "周二", "3", "周三", "4", "周四", "5", "周五", "6", "周六", "7", "周日", "1");

    public static final List<SelectDO> ALL_WEEKS = initWithUnit("", "周", 1, 60);
    public static final List<SelectDO> START_DAYS = initValues("周一", "1", "周日", "7");
    public static final List<SelectDO> CHOOSE_COURSES = initWithUnit("", "节课", 0, 10);
    public static final List<SelectDO> EARLY_CHOOSE_COURSES = initWithUnit("", "节课", 0, 10);
    public static final List<SelectDO> CHOOSE_PERIODS = initWithUnit("", "分钟", "1","2","5","10","20", "25", "30", "35","40","45","50","55","60","70","80","85","90","100","120");
    public static final List<SelectDO> CHOOSE_PERIOD_GAPS = initWithUnit("", "分钟", "0","1","2", "5","10","15","20","25","30","40","50","60");
    public static final List<SelectDO> BEFORE_NOTIFY_TIMES = initWithUnit("", "分钟", "0", "1", "2", "5", "10","15","20", "30","50");
    public static final List<SelectDO> WORK_TYPES = initValues("普通班次", "simple", "组合班次", "combine");
    public static final List<SelectDO> BEFORE_NOTIFY_DAYS = initValues("当天", "0", "第二天", "1", "提前1天", "-1");
    public static final List<SelectDO> VIP_TYPES = initValues("每月", "month", "每年", "year", "永久", "forever");
    public static final List<SelectDO> CHOOSE_TIMES = initTime();
    public static final List<SelectDO> CHOOSE_WEEK_TYPES = initValues("自定义","self", "所有周", "all", "单周","single", "双周", "double");
    public static final List<SelectDO> CHOOSE_FONT_SIZES = initWithUnit("", "", 8, 30);
    public static final List<SelectDO> CHOOSE_WIDGET_STYLES = initValues("单行","0", "双行", "1");
    public static final List<SelectDO> CHOOSE_WIDTH_SIZES = initWithUnitAndGap("", "", 100, 200, 5);
    public static final List<SelectDO> CHOOSE_WIDTH_SIZES2 = initWithUnitAndGap("", "", 40, 120, 5);


    public static final List<SelectDO> initWithUnit(String prefix, String unit, String...args){
        List<SelectDO> all = new ArrayList<>();
        for(String one: args){
            all.add(new SelectDO(prefix + one + unit, one));
        }
        return all;
    }

    public static final List<SelectDO> initWithUnit(String prefix, String unit, int begin, int end){
        return initWithUnitAndGap(prefix, unit, begin, end, 1);
    }



    public static final List<SelectDO> initWithUnitAndGap(String prefix, String unit, int begin, int end, int gap){
        List<SelectDO> all = new ArrayList<>();
        for (int i = begin; i <= end;) {
            all.add(new SelectDO(prefix + i + unit, String.valueOf(i)));
            i += gap;
        }
        return all;
    }

    public static int findPosition(List<SelectDO> options, String value){
        if(value == null){
            return 0;
        }
        for (int i = 0; i < options.size(); i++) {
            if(value.equals(options.get(i).value)) {
                return i;
            }
        }
        return 0;
    }

    public static final List<SelectDO> initTime(){
        List<SelectDO> selects = new ArrayList<>();
        int minute = 0;
        for (int i = 0; i < 24;) {
            String m1 = getTwo(i) + ":" + getTwo(minute);
            selects.add(new SelectDO(m1, m1));
            minute += 5;
            if(minute >= 60){
                minute = 0;
                i += 1;
            }
        }
        return selects;
    }

    public static final List<String> initHour(){
        List<String> selects = new ArrayList<>();
        for (int i = 0; i < 24;i++) {
            selects.add(getTwo(i));
        }
        return selects;
    }

    public static final List<String> initMinutes(){
        List<String> selects = new ArrayList<>();
        for (int i = 0; i < 60;i++) {
            selects.add(getTwo(i));
        }
        return selects;
    }

    public static String getTwo(int k){
        return k < 10 ? ("0" + k) : String.valueOf(k);
    }

    @NotNull
    public static List<SelectDO> makeCourseNumbers(int maxValue) {
        List<SelectDO> all = new ArrayList<>();
        for (int i = 0;i <= maxValue; i++) {
            all.add(new SelectDO(i+ "节课", i + ""));
        }
        return all;
    }


    public static int getDefaultIdx(List<SelectDO> options, String defaultValue){
        for (int i = 0; i < options.size(); i++) {
            if(options.get(i).value.equals(defaultValue)){
                return i;
            }
        }
        return 0;
    }

    public static boolean contains(List<SelectDO> options, String value){
        for (int i = 0; i < options.size(); i++) {
            if(options.get(i).value.equals(value)){
                return true;
            }
        }
        return false;
    }

    public static String getLabel(List<SelectDO> options, String value){
        for (int i = 0; i < options.size(); i++) {
            if(options.get(i).value.equals(value)){
                return options.get(i).label;
            }
        }
        return "未知";
    }

    public static String getColor(List<SelectDO> options, String value){
        for (int i = 0; i < options.size(); i++) {
            if(options.get(i).value.equals(value)){
                return options.get(i).backgroundColor;
            }
        }
        return "#776AD4";
    }

    public static String getLabel(List<SelectDO> options, String value, String defaultValue){
        for (int i = 0; i < options.size(); i++) {
            if(options.get(i).value.equals(value)){
                return options.get(i).label;
            }
        }
        return defaultValue;
    }

}
