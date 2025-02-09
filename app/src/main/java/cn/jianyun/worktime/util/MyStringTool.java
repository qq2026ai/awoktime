package cn.jianyun.worktime.util;

public class MyStringTool {


    public static boolean isBlank(String s) {
        return s == null || s.trim().equals("");
    }

    public static boolean isNotBlank(String s){
        return !isBlank(s);
    }

    public static boolean isAnyBlank(String...ss){
        for (int i = 0; i < ss.length; i++) {
            if(isBlank(ss[i])) {
                return true;
            }
        }
        return false;
    }

    public static String capitalize(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static boolean contains(String value, String subValue) {
        if(isBlank(value)){
            return false;
        }
        return value.contains(subValue);
    }


    public static boolean equals(String value, String otherValue) {
        if (isBlank(value) && isBlank(otherValue)) {
            return true;
        }
        if (isBlank(value) || isBlank(otherValue)) {
            return false;
        }
        return value.equals(otherValue);
    }

    public static String get(String value, String defaultValue){
        return MyStringTool.isBlank(value) ? defaultValue : value;
    }
}
