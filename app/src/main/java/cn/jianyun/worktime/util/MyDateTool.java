package cn.jianyun.worktime.util;

import static cn.jianyun.worktime.util.CommonToolKt.mlog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.jianyun.worktime.api.FestivalData;
import cn.jianyun.worktime.model.MonthDateInfo;

public class MyDateTool {

    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_MONTH = "yyyy-MM";
    public static final String PATTERN_MONTH_CHINESE = "yyyy年MM月";
    public static final String PATTERN_YEAR_CHINESE = "yyyy年";
    public static final String PATTERN_SHORT_DATE = "MM-dd";
    public static final String PATTERN_DATE_TIME  = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_TIME_SHORT = "HH:mm";
    public static final String PATTERN_DATE_TIME_SHORT = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_DATE_BEGIN_TIME = "yyyy-MM-dd 00:00:00";
    public static final String PATTERN_DATE_END_TIME = "yyyy-MM-dd 23:59:59";

    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_YEAR_MONTH = "yyyy年MM月";
    public static final String FORMAT_SHORT_DATE = "MM-dd";
    public static final String FORMAT_SHORT_TIME = "HH:mm";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_SHORT_DATE_TIME = "yyyy-MM-dd HH:mm";

    public static final Map<Integer,Integer> WEEK_DAY_IDX_MAP = new HashMap<Integer,Integer>(){{
        put(1, 6);
        put(2, 0);
        put(3, 1);
        put(4, 2);
        put(5, 3);
        put(6, 4);
        put(7, 5);
    }};
    public static final Map<Integer,Integer> WEEK_DAY_IDX_MAP_OF_SUNDAY = new HashMap<Integer,Integer>(){{
        put(1, 0);
        put(2, 1);
        put(3, 2);
        put(4, 3);
        put(5, 4);
        put(6, 5);
        put(7, 6);
    }};

    public static final Map<Integer,String> WEEK_NAME_MAP = new HashMap<Integer,String>(){{
        put(1, "周日");
        put(2, "周一");
        put(3, "周二");
        put(4, "周三");
        put(5, "周四");
        put(6, "周五");
        put(7, "周六");
    }};

    public static Map<String, List<List<MonthDateInfo>>> monthCache = new HashMap<>();

    public static Date getNow(){
        return new Date();
    }

    public static String format(Date date, String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String toDateString(Date date){
        return format(date, PATTERN_DATE);
    }

    public static String toShortDateString(Date date){
        return format(date, PATTERN_SHORT_DATE);
    }

    public static String toDateTimeString(Date date){
        return format(date, PATTERN_DATE_TIME);
    }

    public static String parseSwiftDate(Float ts){
        //计算时区
        String zone = getTimeZoneOffsetString();
        mlog("当前偏移:" + zone);
        var first = MyDateTool.parseDateTimeString("2001-01-01 " + zone);
        first.setTime(first.getTime() + ts.longValue() * 1000);
        return MyDateTool.toDateString(first);
    }

    public static String getTimeZoneOffsetString() {
        // 获取系统默认时区
        ZoneId zone = ZoneId.systemDefault();

        // 获取当前时间在该时区的偏移量
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZoneOffset offset = now.getOffset();

        // 计算总小时数（包含小数，例如+5.5时区）
        double totalHours = offset.getTotalSeconds() / 3600.0;
        int hours = (int) totalHours;
        int minutes = (int) ((totalHours - hours) * 60);

        if(hours < 0){
            return "00:00:00";
        }
        // 格式化输出
        if (minutes == 0) {
            // 如果是整小时，格式化为"±HH:00"
            return String.format("%02d:00:00", hours);
        } else {
            // 如果有分钟偏移（如+05:30）
            return String.format("%02d:%02d:00", hours, Math.abs(minutes));
        }
    }


    public static Long toSwiftTime(Date date){
        var first = MyDateTool.parseDateTimeString("2001-01-01 00:00:00");
        return (date.getTime() - first.getTime()) / 1000;
    }

    public static String toShortDateTimeString(Date date){
        return format(date, PATTERN_DATE_TIME_SHORT);
    }

    public static String toTimeString(Date date){
        return format(date, PATTERN_TIME);
    }

    public static String toShortTimeString(Date date){
        return format(date, PATTERN_TIME_SHORT);
    }

    public static String toMonthString(Date date){
        return format(date, PATTERN_MONTH);
    }

    public static String toChineseMonthString(Date date){
        return format(date, PATTERN_MONTH_CHINESE);
    }

    public static String toChineseYearString(Date date){
        return format(date, PATTERN_YEAR_CHINESE);
    }


    public static String getDateTimeNow(){
        return toDateTimeString(getNow());
    }

    public static String getDateOfNow(){
        return toDateString(getNow());
    }

    public static Date getStartOfToday(){
        return getStartOfDay(getNow());
    }

    public static Date getEndOfToday(){
        return getEndOfDay(getNow());
    }

    public static Date getStartOfYesterday(){
        return getStartOfDay(gapDay(getNow(), -1));
    }

    public static Date getEndOfYesterday(){
        return getEndOfDay(gapDay(getNow(), -1));
    }

    public static Date parseDateString(String dateStr) {
        try{
            return new SimpleDateFormat(PATTERN_DATE).parse(dateStr);
        }
        catch (Exception e){
            return new Date();
        }
    }

    public static Date parseDateTimeString(String dateStr) {
        try{
            return new SimpleDateFormat(PATTERN_DATE_TIME).parse(dateStr);
        }
        catch (Exception e){
            return new Date();
        }
    }

    public static Date parseShortDateTimeString(String dateStr) {
        try{
            return new SimpleDateFormat(PATTERN_DATE_TIME_SHORT).parse(dateStr);
        }
        catch (Exception e){
            return new Date();
        }
    }

    public static int getField(Date date, int field){
        Calendar calendar = toCalendar(date);
        return calendar.get(field);
    }

    public static int getYear(Date date){
        return getField(date, Calendar.YEAR);
    }

    public static int getCurrentYear(){
        return getYear(new Date());
    }

    public static String getCurrentYearStr(){
        return String.valueOf(getCurrentYear());
    }
    public static int getMonth(Date date){
        return getField(date, Calendar.MONTH) + 1;
    }

    public static int getDay(Date date){
        return getField(date, Calendar.DATE);
    }

    public static String getDay(String date){
        return String.valueOf(getDay(parseDateString(date)));
    }


    public static int getHour(Date date){
        return getField(date, Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date){
        return getField(date, Calendar.MINUTE);
    }

    public static int getSecond(Date date){
        return getField(date, Calendar.SECOND);
    }

    public static int getWeekday(Date date){
        return getField(date, Calendar.DAY_OF_WEEK);
    }

    public static boolean isSaturday(Date date){
        return getWeekday(date) == 7;
    }

    public static boolean isSunday(Date date){
        return getWeekday(date) == 1;
    }

    public static int getWeekdayIdx(Date date, boolean isMondayFirst){
        int weekday = getWeekday(date);
        if(isMondayFirst){
            return WEEK_DAY_IDX_MAP.get(weekday);
        }
        return WEEK_DAY_IDX_MAP_OF_SUNDAY.get(weekday);
    }

    public static String getWeekdayName(Date date){
        return WEEK_NAME_MAP.get(getWeekday(date));
    }

    public static Date getStartOfDay(Date date){
        Calendar calendar = toCalendar(date);
        clearTime(calendar);
        return calendar.getTime();
    }

    public static void clearTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static Date clearTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date){
        Calendar calendar = toCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Calendar toCalendar(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date gap(Date date, int field, int value){
        Calendar calendar = toCalendar(date);
        calendar.add(field, value);
        return calendar.getTime();
    }

    public static Date gapDayFromNow(int gapDay){
        return gapDay(getNow(), gapDay);
    }

    public static Date gapMonth(Date date, int gapMonth){
        return gap(date, Calendar.MONTH, gapMonth);
    }

    public static Date getMonthFromNow(int gapMonth){
        return gapMonth(getNow(), gapMonth);
    }

    public static Date getStartDayOfMonth(Date date){
        Calendar calendar = toCalendar(date);
        clearTime(calendar);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date getFirstDayOfWeek(Date date, boolean isMondayFirst){
        return gapDay(date, -1 * getWeekdayIdx(date,isMondayFirst));
    }

    public static List<Date> getWeekDatesFromSomeDay(Date date, boolean isMondayFirst){
        Date monday = getFirstDayOfWeek(date, isMondayFirst);
        List<Date> weekDates = new ArrayList<>();
        weekDates.add(monday);
        for (int i = 1; i < 7; i++) {
            weekDates.add(gapDay(monday, i));
        }
        return weekDates;
    }

    public static List<Date> getCurrentWeekDates(boolean isMondayFirst){
       return getWeekDatesFromSomeDay(new Date(), isMondayFirst);
    }

    public static int getBetweenDays(Date date1, Date date2){
        return (int) Math.abs(Math.floor((date2.getTime() - date1.getTime()) / 1000.0 / 60 / 60 / 24));
    }


    public static int getBetweenDays(long t1, long t2){
        return (int) (Math.floor(t2 - t1) / 1000.0 / 60 / 60 / 24);
    }



    public static String getDayInfo(Date date){
        Date d1 = getStartOfDay(date);
        Date d2 = getStartOfDay(new Date());
        boolean future = d1.getTime() > d2.getTime();
        int gapDay = getBetweenDays(d1, d2);
        if(gapDay == 0){
            return "今天";
        }
        if(gapDay == 1){
            return future ? "明天" : "昨天";
        }
        if(gapDay == 2){
            return future ? "后天" : "前天";
        }
        return gapDay + "天" + (future ? "后" : "前");
    }


    /**
     * 已知某天refDate是第refWeek周，问另一个targetDate日期是第几周?
     */
    public static int getWeekFromRefWeek(Date refDate, int refWeek, Date targetDate, boolean isMondayFirst){
        Date refMonday = getFirstDayOfWeek(refDate, isMondayFirst);
        Date targetMonday = getFirstDayOfWeek(targetDate, isMondayFirst);
        int day = getBetweenDays(refMonday, targetMonday);
        int weeks = day / 7;
        int result = weeks + refWeek;
        return result;
    }

    /**
     * 已知某天refDate是第refWeek周，获取第targetWeek周的一周日期
     */
    public static List<Date> getWeekDatesFromRefWeek(Date refDate, int refWeek, int targetWeek, boolean isMondayFirst){
        Date refMonday = getFirstDayOfWeek(refDate, isMondayFirst);
        int gapWeek = targetWeek - refWeek;
        int gapDay = gapWeek * 7;
        Date targetMonday = gapDay(refMonday, gapDay);
        return getWeekDatesFromSomeDay(targetMonday, isMondayFirst);
    }


    public static String toStringDate(int year, int month, int day){
        String date = year + "-" + MyDataTool.getTwo(month + 1) + "-" +  MyDataTool.getTwo(day);
        return date;
    }

    public static Date make(@NotNull String chooseYear, @Nullable String chooseMonth, @NotNull String beginDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.YEAR, MyDataTool.toInteger(chooseYear, 2024));
        calendar.set(Calendar.MONTH, MyDataTool.toInteger(chooseMonth, 2) - 1);
        calendar.set(Calendar.DATE, MyDataTool.toInteger(beginDay, 1));
        return calendar.getTime();
    }


    public static String getStartDayStringOfMonth(Date date){
        return MyDateTool.toDateString(getStartDayOfMonth(date));
    }

    public static Date getLastDayOfMonth(Date date){
        Calendar calendar = toCalendar(date);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static String getLastDayStringOfMonth(Date date){
        return MyDateTool.toDateString(getLastDayOfMonth(date));
    }


    public static List<List<MonthDateInfo>> getMonthInfo(Date date, boolean mondayFirst, Map<String, FestivalData> holidayMap,boolean fill){
        Date firstDay = getStartDayOfMonth(date);

//        String month = toMonthString(date);
//        if(!holidayMap.isEmpty()){
//            List<List<MonthDateInfo>> olds = monthCache.get(month);
//            if(olds != null){
//                mlog("read month cache", month);
//                return olds;
//            }
//        }
        List<List<MonthDateInfo>> all = new ArrayList<>();
        int position = getFirstDayPosition(firstDay, mondayFirst);
        List<MonthDateInfo> flat = new ArrayList<>();
        for(int i = 0;i < position;i ++){
            flat.add(new MonthDateInfo());
        }
        String today = toDateString(new Date());
        Date lastDay = getLastDayOfMonth(date);
        for(int i = 0;i < 32;i ++){
            Date day = gap(firstDay, Calendar.DATE, i);
            if(day.getTime() > lastDay.getTime()){
                break;
            }
            String k = toDateString(day);
            boolean isToday = today.equals(k);
            String lunarDay = "";
            boolean holiday = false;
            boolean work = false;
            if(holidayMap.containsKey(k)){
                lunarDay = holidayMap.get(k).getName();
                holiday = holidayMap.get(k).getHoliday();
                work = !holidayMap.get(k).getHoliday();
            }
            else{
                lunarDay = LunarCalendar.getLunarDay(day);
            }
            MonthDateInfo kk = new MonthDateInfo(day, getField(day, Calendar.DATE) + "", lunarDay, isToday,false, holiday, work);
            flat.add(kk);
        }

        //逢7分段
        List<MonthDateInfo> group = new ArrayList<>();
        for (int i = 1; i <= flat.size(); i++) {
            group.add(flat.get(i-1));
            if(i % 7 == 0){
                all.add(group);
                group = new ArrayList<>();
            }
        }
        if(group.size() > 0){
            int gap = 7 - group.size();
            for (int i = 0; i < gap; i++) {
                group.add(new MonthDateInfo());
            }
            all.add(group);
        }

//        if(!holidayMap.isEmpty()){
//            monthCache.put(month, all);
//        }
        return all;
    }



    public static int getFirstDayPosition(Date date, boolean mondayFirst){
        Calendar calendar = toCalendar(date);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        if(mondayFirst){
            switch (weekday){
                case Calendar.MONDAY: return 0;
                case Calendar.TUESDAY: return 1;
                case Calendar.WEDNESDAY: return 2;
                case Calendar.THURSDAY: return 3;
                case Calendar.FRIDAY: return 4;
                case Calendar.SATURDAY: return 5;
                case Calendar.SUNDAY: return 6;
                default: return 7;
            }
        }
        switch (weekday){
            case Calendar.MONDAY: return 1;
            case Calendar.TUESDAY: return 2;
            case Calendar.WEDNESDAY: return 3;
            case Calendar.THURSDAY: return 4;
            case Calendar.FRIDAY: return 5;
            case Calendar.SATURDAY: return 6;
            case Calendar.SUNDAY: return 0;
            default: return 7;
        }
    }


    public static List<String> getMonthHeaderInfo(boolean mondayFirst){
        String[] weeks1 = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        String[] weeks2 = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        if(mondayFirst){
            return Arrays.stream(weeks1).collect(Collectors.toList());
        }
        return Arrays.stream(weeks2).collect(Collectors.toList());
    }


    public static Date parseDate(String date){
        try {
            return new SimpleDateFormat(FORMAT_DATE).parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }


    public static Date gap(Date date, String field, int value){
        switch (field){
            case "d":
                return gap(date, Calendar.DATE, value);
            case "w":
                return gap(date, Calendar.DATE, value * 7);
            case "m":
                return gap(date, Calendar.MONTH, value);
            case "y":
                return gap(date, Calendar.YEAR, value);
            default:
                return gap(date, Calendar.YEAR, value);
        }
    }

    public static Date gapDay(Date date,  int value){
        return gap(date, Calendar.DATE, value);
    }

    public static String nextDay(String date){
        return toDateString(gapDay(parseDate(date), 1));
    }


    public static Date getBeginOfToday() {
        return parseDate(toDateString(new Date()));
    }

    public static String toYearMonthString(Date date){
        return format(date, FORMAT_YEAR_MONTH);
    }


    public static boolean between(String targetDate, String beginDate, String endDate){
        return targetDate.compareTo(beginDate) >= 0 && targetDate.compareTo(endDate) <= 0;
    }

    @NotNull
    public static Date parse(@NotNull String s, @NotNull String format) {
        try {
            return new SimpleDateFormat(format).parse(s);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getTwo(int x){
        return x < 10 ? "0" + x : "" + x;
    }

    public static String getRestTime(long gap){
        StringBuffer sbf = new StringBuffer();
        gap = gap / 1000;
        long second = gap % 60;
        long minute = (gap / 60) % 60;
        long hour = gap / 60 / 60;
        if(hour > 0){
            sbf.append(getTwo((int) hour));
            sbf.append(":");
            sbf.append(getTwo((int) minute));
            sbf.append(":");
            sbf.append(getTwo((int) second));
            return sbf.toString();
        }
        if(minute > 0){
            sbf.append(getTwo((int) minute));
            sbf.append(":");
            sbf.append(getTwo((int) second));
            return sbf.toString();
        }
        sbf.append(getTwo((int) second));
        sbf.append("秒");
        return sbf.toString();
    }

    public static String getBigMonth(Date currentDate) {
        int month = getMonth(currentDate);
        return months.get(month) + "月";
    }

    public static List<String> months = List.of("0", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二");

    public static String getCurrentTime() {
        return toDateTimeString(new Date());
    }
}
