package cn.jianyun.worktime.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MyDataTool {


    public static Integer toInteger(Object obj) {
        return toInteger(obj, null);
    }

    public static Integer toInteger(Object obj, Integer defVal) {
        try {
            return Integer.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    public static Long toLong(Object obj) {
        return toLong(obj, null);
    }

    public static Long toLong(Object obj, Long defVal) {
        try {
            return Long.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    public static boolean toBoolean(Object obj){
        try{
            return Boolean.parseBoolean(obj.toString());
        } catch (Exception e){
            return false;
        }
    }

    public static Double toDouble(Object obj) {
        return toDouble(obj, null);
    }

    public static Double toDouble(Object obj, Double defVal) {
        try {
            return Double.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    public static String toFixed(float x, int len){
        String v = new BigDecimal(x).setScale(len, BigDecimal.ROUND_HALF_UP).toString();
        if(v.endsWith(".00") || v.endsWith(".0")) {
            return toFixed(x, 0);
        }
        return v;
    }

    public static String getFileSize(String fileSize){
        int size = toInteger(fileSize, 0);
        if(size < 1024){
            return size + "B";
        }
        else if(size < 1024 * 1024){
            float ns = (float) (size * 1.0 / 1024);
            return MyDataTool.toFixed(ns, 2) + "KB";
        }
        else{
            float ns = (float) (size * 1.0 / 1024 / 1024);
            return MyDataTool.toFixed(ns, 2) + "MB";
        }
    }
    public static String leftPad(String data,int len, String leftChar){
        if(data == null){
            return "";
        }
        if(data.length() >= len){
            return data;
        }
        StringBuffer sbf = new StringBuffer();
        sbf.append(data);
        while(sbf.length() < len){
            sbf.insert(0, leftChar);
        }
        return sbf.toString();
    }

    public static String leftPadZero(String data,int len){
        return leftPad(data, len, "0");
    }

    public static String leftPadZero(int data,int len){
        return leftPad(String.valueOf(data), len, "0");
    }


    public static String getTwo(int data){
        return leftPad(String.valueOf(data), 2, "0");
    }

    public static String toString(Object text, String defaultValue) {
        if(text == null){
            return defaultValue;
        }
        return text.toString();
    }

    public static String join(List<Integer> fixWeeks) {
        if(fixWeeks == null || fixWeeks.isEmpty()){
            return "empty";
        }
        StringBuffer sbf = new StringBuffer();
        fixWeeks.stream().sorted().forEach(one -> {
            sbf.append(one + ",");
        });
        return sbf.toString();
    }


    /**
     * 保留几位小数
     * @param price 原始价格
     * @param decimal 小数位数
     */
    public static String getShownPrice(String price, int decimal){
        if("".equals(price)){
            return "无";
        }
        try{
            return new BigDecimal(price).setScale(decimal, RoundingMode.HALF_UP).toString();
        }
        catch (Exception e){
            return "无";
        }
    }

    public static String getRealPrice(String price, int decimal){
        if("".equals(price)){
            return "0";
        }
        try{
            return new BigDecimal(price).setScale(decimal, RoundingMode.HALF_UP).toString();
        }
        catch (Exception e){
            return "0";
        }
    }


    /**
     * 保留几位小数
     * @param price 原始价格
     * @param decimal 小数位数
     */
    public static float getPriceWithFloat(String price, int decimal){
        if("".equals(price)){
            return 0f;
        }
        if("-".equals(price)){
            return 0f;
        }

        try{
            return new BigDecimal(price.trim()).setScale(decimal, RoundingMode.HALF_UP).floatValue();
        }
        catch (Exception e){
            return 0f;
        }
    }

    /**
     * 保留几位小数
     * @param price 原始价格
     * @param decimal 小数位数
     */
    public static String divide(String price, String part, int decimal){
        if("".equals(part) || "0".equals(part)){
            return "无";
        }
        Double v1 = toDouble(price);
        Double v2 = toDouble(part);
        if(v1 == null || v2 == null){
            return "无";
        }
        return new BigDecimal(v1).divide(new BigDecimal(v2), decimal, RoundingMode.HALF_UP).toString();
    }

    public static float divideWithFloat(String price, String part, int decimal){
        if("".equals(part) || "0".equals(part)){
            return 0F;
        }
        Double v1 = toDouble(price);
        Double v2 = toDouble(part);
        if(v1 == null || v2 == null){
            return 0F;
        }
        return new BigDecimal(v1).divide(new BigDecimal(v2), decimal, RoundingMode.HALF_UP).floatValue();
    }


    public static String plusTime(String t1, String t2) {
        var k0s = t1.split(":");
        var k1s = t2.split(":");
        if(k0s.length == 2 && k1s.length == 2){
            int hour = plusNum(k0s[0], k1s[0]);
            int minute = plusNum(k0s[1], k1s[1]);
            if(minute >= 60){
                hour += minute / 60;
                minute = minute % 60;
            }
            return hour + ":" + minute;
        }
        if("".equals(t1)){
            return t2;
        }
        return t1;
    }

    public static int plusNum(String t1, String t2) {
       return (int) (getPriceWithFloat(t1, 0) + getPriceWithFloat(t2, 0));
    }

    public static float plusPrice(String t1, String t2) {
        return getPriceWithFloat(t1, 2) + getPriceWithFloat(t2, 2);
    }

    public static float minusPrice(String t1, String t2) {
        return getPriceWithFloat(t1, 2) - getPriceWithFloat(t2, 2);
    }


    public static String plusPriceWithString(String v1, String v2){
        return toFixed(plusPrice(v1, v2), 2);
    }

    public static String minusPriceWithString(String v1, String v2){
        return toFixed(minusPrice(v1, v2), 2);
    }

    public static String minusTime(String t2, String t1, boolean beyond){
        var k0s = t1.split(":");
        var k1s = t2.split(":");
        if(k0s.length == 2 && k1s.length == 2){
            int hour1 = toInteger(k0s[0], 0);
            int hour2 = toInteger(k1s[0], 0);
            if(hour2 < hour1 && beyond){
                hour2 += 24;
            }
            int minute1 = toInteger(k0s[1], 0);
            int minute2 = toInteger(k1s[1], 0);
            int min = hour2 * 60 + minute2 - hour1 * 60 - minute1;
            int hour = min / 60;
            min = min % 60;
            return hour + ":" + min;
        }
        if("".equals(t1)){
            return t2;
        }
        return t1;
    }

    public static float multipy(String v1, String v2) {
        return getPriceWithFloat(v1, 2) * getPriceWithFloat(v2, 2);
    }

    public static String multipyWithString(String v1, String v2) {
        return toFixed(multipy(v1, v2), 2);
    }

    public static String timeToDecimal(String time){
        if("".equals(time)){
            return "";
        }
        var k0s = time.split(":");
        if(k0s.length == 2){
            int hour1 = toInteger(k0s[0], 0);
            int minute1 = toInteger(k0s[1], 0);
            return toFixed((float) (hour1 + minute1 * 1.0 / 60), 2);
        }
        return "";
    }


    public static String getShownTime(String time){
        return getShownTime(time, false);
    }
    public static String getShownTime(String time, boolean small) {
        if("".equals(time)){
            return "无";
        }
        var ks = time.split(":");
        if(ks.length == 2){
            String h = ks[0];
            String m = ks[1];
            String s = "";

            if(small){
                return toFixed((float) ((MyDataTool.toInteger(h) * 60 + MyDataTool.toInteger(m)) * 1.0 / 60), 2) + "h";
            }

            if(MyDataTool.toInteger(h, 0) > 0){
                s = h + "小时";
            }
            if(MyDataTool.toInteger(m, 0) > 0){
                s += m + "分钟";
            }
            return s;
        }
        return "无";
    }

    public static String withUnit(String value, String unit){
        return withUnit(value, -1, unit);
    }

    public static String withUnit(String value, int decimal, String unit){
        if ("".equals(value) || "0".equals(value)) {
            return "无";
        }
        if(decimal >= 0){
            return toFixed(toDouble(value).floatValue(), decimal) + unit;
        }
        return value + unit;
    }
}
