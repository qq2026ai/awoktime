package cn.jianyun.worktime.util;

public class MyTimeTool {

    public static String gapTime(String begin, int gap){
        if(begin.length() != 5){
            return "0";
        }
        String[] bs = begin.split(":");
        int hour = MyDataTool.toInteger(bs[0], 0);
        int minute = MyDataTool.toInteger(bs[1], 0);
        minute += gap;

        if(minute < 0){
            minute += 60;
            hour -= 1;
        }
        int newHour = minute / 60;
        minute = minute % 60;
        hour += newHour;
        if(hour > 23){
            hour -= 24;
        }
        if(hour < 0){
            hour += 24;
        }
        return MyDataTool.getTwo(hour) + ":" + MyDataTool.getTwo(minute);
    }

    public static boolean isPassHours(long t, int hours) {
        return (System.currentTimeMillis() - t) / 1000 / 3600 > hours;
    }

    public static boolean isPassDays(long t, int days) {
        return (System.currentTimeMillis() - t) / 1000 / 3600 / 24> days;
    }

//    public static boolean isPassHoursByCacheKey(Context context, String key, int hours) {
//        long t = CacheUtil.getLong(context, key, 0L);
//        return isPassHours(t, hours);
//    }
//
//    public static boolean isPassDaysByCacheKey(Context context, String key, int hours) {
//        long t = CacheUtil.getLong(context, key, 0L);
//        return isPassDays(t, hours);
//    }
}
