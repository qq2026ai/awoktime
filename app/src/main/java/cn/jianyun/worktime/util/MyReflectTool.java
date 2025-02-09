package cn.jianyun.worktime.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyReflectTool {


    private static final String OBJECT = "Object";

    private static final Map<String, List<String>> fieldNamesMap = new ConcurrentHashMap<>();

    public static boolean isNotSuperObjectClass(Class c){
        return !OBJECT.equals(c.getSimpleName());
    }


    //获取对象所有字段
    public static <T> List<String> getFieldNames(Class cls){
        String clsName = cls.getName();
        if(fieldNamesMap.containsKey(clsName)){
            return fieldNamesMap.get(clsName);
        }
        List<String> allFieldNames = new ArrayList<>();
        while(isNotSuperObjectClass(cls)){
            Field[] fields = cls.getDeclaredFields();
            List<String> names = MyCollectionTool.lstFilterOne(Arrays.asList(fields), "name");
            allFieldNames.addAll(names);
            cls = cls.getSuperclass();
        }
        fieldNamesMap.put(clsName, allFieldNames);
        return allFieldNames;
    }
    //获取对象所有字段
    public static <T> List<String> getFieldNames(T t){
        Class cls = t.getClass();
        return getFieldNames(cls);
    }

    public static Field getField(Class cls, String fieldName) {
        Field one = null;
        try{
            one = cls.getDeclaredField(fieldName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return one;
    }

    //获取对象所有字段
    public static <T> List<Field> getFields(Class cls){
        List<Field> allFields = new ArrayList<>();
        while(isNotSuperObjectClass(cls)){
            Field[] fields = cls.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            cls = cls.getSuperclass();
        }
        return allFields;
    }

    //获取对象所有字段
    public static <T> List<Field> getFields(T t){
        Class cls = t.getClass();
        return getFields(cls);
    }

    public static List<String> getMethodNames(Class cls){
        Method[] methods = cls.getDeclaredMethods();
        return MyCollectionTool.lstFilterOne(Arrays.asList(methods), "name");
    }

    public static List<String> getAllMethodNames(Class cls){
        List<String> all = new ArrayList<>();
        while(isNotSuperObjectClass(cls)){
            all.addAll(getMethodNames(cls));
            cls = cls.getSuperclass();
        }
        return all;
    }

    public static Map<String, Method> getMethods(Class cls){
        Method[] methods = cls.getDeclaredMethods();
        return MyCollectionTool.lst2map(Arrays.asList(methods), "name");
    }

    public static Map<String, Method> getAllMethods(Class cls){
        Map<String, Method> all = new HashMap<>();
        while(isNotSuperObjectClass(cls)){
            all.putAll(getMethods(cls));
            cls = cls.getSuperclass();
        }
        return all;
    }

    //annotation区域
    public static boolean hasAnnotation(Field field, Class annotation){
        return field.getAnnotation(annotation) != null;
    }

    //实例化
    public static Object newInstance(Class cls){
        Object result = null;
        try {
            result = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //设置对象属性值
    public static void setFieldValue(Object object, String fieldName, Object value){
        Class cls = object.getClass();
        while(isNotSuperObjectClass(cls)){
            try{
                Field field = cls.getDeclaredField(fieldName);
                if(Modifier.isFinal(field.getModifiers())){
                    return;
                }
                field.setAccessible(true);
                field.set(object, value);
                break;
            }
            catch (Exception e){
                System.out.println("field error"+ object.getClass());
                cls = cls.getSuperclass();
            }
        }
    }

    //获取对象属性值
    public static Object getFieldValue(Object obj, String fieldName) {
        Class cls = obj.getClass();
        while (isNotSuperObjectClass(cls)){
            try {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                cls = cls.getSuperclass();
            }
        }
        return null;
    }

    //调用对象方法
    public static Object invokeMethod(Object obj, String methodName, Class[] parameters, Object... paramValues){
        Class cls = obj.getClass();
        while(isNotSuperObjectClass(cls)){
            try{
                Method method;
                Object value;
                if(parameters == null){
                    method = cls.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    value = method.invoke(obj);
                }
                else{
                    method = cls.getDeclaredMethod(methodName, parameters);
                    method.setAccessible(true);
                    value = method.invoke(obj, paramValues);
                }
                return value;
            }
            catch (Exception e) {
                cls = cls.getSuperclass();
            }
        }
        return null;
    }

    //调用对象方法, 无参调用
    public static Object invokeMethod(Object obj, String methodName){
        return invokeMethod(obj, methodName, null);
    }


    public static <T> Object getValue(T v, String key){
        if(v instanceof Map){
            return ((Map)v).get(key);
        }
        else{
            return getFieldValue(v, key);
        }
    }

    public static List<String> getKeys(Object v){
        return getFieldNames(v);
    }

    public static <T> void setValue(T v, String key, Object value){
        if(v instanceof Map){
            ((Map)v).put(key, value);
        }
        else{
           setFieldValue(v, key, value);
        }
    }

    public static String getClassName(Object v){
        if(v == null){
            return null;
        }
        return v.getClass().getSimpleName();
    }


    public static <T> T copy(T source, String props){
        List<String> attrs = MyCollectionTool.str2list(props);
        try {
            T instance = (T) source.getClass().newInstance();
            for(String item: attrs){
                setValue(instance, item, getValue(source, item));
            }
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearProps(Object source, String excludes){
        Set<String> excludeFields = MyCollectionTool.str2set(excludes);
        List<Field> fields = getFields(source.getClass());
        for(Field field: fields){
            if(excludeFields.contains(field.getName())){
                continue;
            }
            String type = field.getType().getSimpleName().toLowerCase();
            if("string".equals(type)) {
                setFieldValue(source, field.getName(), "");
            }
            if("int".equals(type)){
                setFieldValue(source, field.getName(), 0);
            }
            if("list".equals(type)){
                setFieldValue(source, field.getName(), Collections.EMPTY_LIST);
            }
        }
    }

    public static <T> T copyExclude(T source, String props){
        List<String> attrs = MyCollectionTool.str2list(props);
        try {
            T instance = (T) source.getClass().newInstance();

            Set<String> fields;
            if(source instanceof Map){
                fields = ((Map)source).keySet();
            }
            else{
                fields = new HashSet<>(getFieldNames(source));
            }
            for(String item: fields){
                if(attrs.contains(item)){
                    continue;
                }
                setValue(instance, item, getValue(source, item));
            }
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

}
