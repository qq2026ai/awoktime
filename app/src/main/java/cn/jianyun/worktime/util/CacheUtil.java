package cn.jianyun.worktime.util;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CacheUtil {

    public static void remove(Context context, String key){
        FileUtil.remove(context, key);
    }

    public static void appendDebugInfo(Context context, String data, boolean debug){
        if(debug){
            String old = get(context, "debugInfo", true);
            System.out.println(old);
            old += MyDateTool.toShortTimeString(new Date()) + ": " +  data + "\n\n";
            if(old.length() > 1000){
                String[] ks = old.split("\n");
                StringBuffer sbf = new StringBuffer();
                for (int i = ks.length / 2; i < ks.length; i++) {
                    sbf.append(ks[i] + "\n");
                }
                old = sbf.toString();
            }
            set(context, "debugInfo", old);
        }
        Log.d("timetable", data);
    }

    public static void remove(Context context, String key, String module){
        remove(context,group(key, module));
    }

    public static void set(Context context, String key, String value){
        FileUtil.saveData(context, key, value);
    }
    public static void set(Context context, String key,String module, String value){
        FileUtil.saveData(context, group(key, module), value);
    }

    public static void setInt(Context context, String key, Integer k){
        set(context, key, k == null ? "" : String.valueOf(k));
    }

    public static void setInt(Context context, String key, String module, Integer k){
        set(context, key, module, k == null ? "" : String.valueOf(k));
    }

    public static void setBoolean(Context context, String key, Boolean k){
        set(context, key, k == null ? "" : String.valueOf(k));
    }

    public static void setDouble(Context context, String key, Double k){
        set(context, key, k == null ? "" : String.valueOf(k));
    }

    public static void setLong(Context context, String key, Long k){
        set(context, key, k == null ? "" : String.valueOf(k));
    }

    public static String get(Context context, String key, boolean withBreak){
        return FileUtil.loadData(context, key, withBreak);
    }

    public static String get(Context context, String key){
        return FileUtil.loadData(context, key, false);
    }

    public static String get(Context context, String key, String module){
        return get(context, group(key, module));
    }

    public static String getWithDefault(Context context, String key, String defaultValue){
        String t = get(context, key);
        if(MyStringTool.isBlank(t)){
            return defaultValue;
        }
        return t;
    }

    public static Integer getInt(Context context, String key, Integer defaultValue){
        return MyDataTool.toInteger(get(context, key), defaultValue);
    }

    public static boolean getBoolean(Context context, String key){
        return MyDataTool.toBoolean(get(context, key));
    }

    public static Double getDouble(Context context, String key, Double defaultValue){
        return MyDataTool.toDouble(get(context, key), defaultValue);
    }

    public static Long getLong(Context context, String key, Long defaultValue){
        return MyDataTool.toLong(get(context, key), defaultValue);
    }


    public static void setJson(Context context, String key, Object value){
        set(context, key, JSON.toJSONString(value));
    }

    public static <T> T getObject(Context context, String key, Class<T> cls){
        try{
            return JSON.parseObject(get(context, key), cls);
        }catch (Exception e){
            return null;
        }
    }

    public static void setJson(Context context, String key, String module, Object value){
        setJson(context, group(key, module), value);
    }

    public static <T> T getObject(Context context, String key, String module, Class<T> cls){
        return getObject(context, group(key, module), cls);
    }

    public static <T> List<T> getArrayObject(Context context, String key, Class<T> cls){
        try{
            List<T> kk = JSON.parseArray(get(context, key), cls);
            if(kk == null){
                return new ArrayList<>();
            }
            return kk;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }



    private static String group(String key, String module){
        return key + "__" + module;
    }

    public static boolean isDebug(Context context) {
        return getBoolean(context, "debug");
    }
}
