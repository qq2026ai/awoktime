package cn.jianyun.worktime.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MyCollectionTool {


    public static <T> String join(List<T> lists, String joiner){
        StringBuffer sbf = new StringBuffer();
        for(T k: lists){
            sbf.append(k);
            sbf.append(joiner);
        }
        if(sbf.length() > 0){
            sbf.deleteCharAt(sbf.length() - 1);
        }
        return sbf.toString();
    }


    public static <T> String join(T[] lists, String joiner){
        StringBuffer sbf = new StringBuffer();
        for(T k: lists){
            sbf.append(k);
            sbf.append(joiner);
        }
        if(sbf.length() > 0){
            sbf.deleteCharAt(sbf.length() - 1);
        }
        return sbf.toString();
    }


    public static <T> String join(List<T> lists) {
        return join(lists, ",");
    }

    public static <T> List<T> makeList(T...data){
        List<T> result = new ArrayList<>();
        for(T t: data){
            result.add(t);
        }
        return result;
    }

    public static <T> boolean contains(Map<String, T> obj, String key){
        return obj != null && obj.containsKey(key);
    }

    public static <T> String getString(Map<String, T> obj, String key){
        return getString(obj, key, "");
    }

    public static <T> String getString(Map<String, T> obj, String key, String defaultValue){
        if(obj == null){
            return defaultValue;
        }
        Object r = obj.get(key);
        return r == null ? defaultValue : r.toString();
    }

    public static <T> int getInt(Map<String, T> obj, String key){
        return getInt(obj, key);
    }

    public static <T> int getInt(Map<String, T> obj, String key, int defaultValue){
        return MyDataTool.toInteger(getString(obj, key), defaultValue);
    }

    public static <T> boolean getBoolean(Map<String, T> obj, String key){
        return MyDataTool.toBoolean(getString(obj, key));
    }

    public static <T> boolean containsAll(Map<String, T> obj, String...keys){
        if(keys == null){
            return true;
        }
        for(String k: keys){
            if(!getBoolean(obj, k)){
                return false;
            }
        }
        return true;
    }
    public static <T> boolean containsAny(Map<String, T> obj, String...keys){
        if(keys == null){
            return true;
        }
        for(String k: keys){
            if(getBoolean(obj, k)){
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map){
        return map == null || map.isEmpty();
    }

    /**
     *
     * 例子:
     * lst = [{"uuid":"a1", "name": "n1"}, {"uuid":"a2", "name":"n2"},{"uuid":"a3", "name":"n3}]
     * key = uuid
     *
     * 结果:
     * {"a1": {"uuid":"a1", "name": "n1"}}
     * {"a2": {"uuid":"a2", "name": "n2"}}
     * {"a3": {"uuid":"a3", "name": "n3"}}
     */
    public static <K, V> Map<K, V> lst2map(List<V> lst, String key){
        if(isEmpty(lst)){
            return Collections.EMPTY_MAP;
        }
        Map<K, V> result = new HashMap<>();
        for(V t: lst){
            K mkey = (K) MyReflectTool.getValue(t, key);
            if(mkey != null){
                result.put(mkey, t);
            }
        }
        return result;
    }


    /**
     *
     * 例子:
     * lst = [{"uuid":"a1", "name": "n1"}, {"uuid":"a2", "name":"n2"},{"uuid":"a3", "name":"n3}]
     * key = name
     *
     * 结果:
     * ["n1", "n2", "n3"]
     */
    public static <K, V> List<K> lstFilterOne(List<V> lst, String key){
        if(isEmpty(lst)){
            return Collections.EMPTY_LIST;
        }
        List<K> result = new ArrayList<>();
        for(V item: lst){
            K value = (K)MyReflectTool.getValue(item, key);
            if(value != null){
                result.add(value);
            }
        }
        return result;
    }

    /**
     *
     * 例子:
     * lst = [{"uuid":"a1", "name": "n1"}, {"uuid":"a2", "name":"n2"},{"uuid":"a3", "name":"n3}]
     * key = uuid
     * valueKey = name
     *
     * 结果:
     * {"a1": "n1"}
     * {"a2": "n2"}
     * {"a3": "n3"}
     */
    public static <K,V> Map<K, Object> lst2map(List<V> lst, String key, String valueKey){
        if(isEmpty(lst)){
            return Collections.EMPTY_MAP;
        }
        Map<K, Object> result = new HashMap<>();
        for(V item: lst){
            K mkey = (K)MyReflectTool.getValue(item, key);
            if(mkey != null){
                Object value = MyReflectTool.getValue(item, valueKey);
                result.put(mkey, value);
            }
        }
        return result;
    }

    /**
     *
     * 例子:
     * lst = [{"uuid":"a1", "name": "n1", "age":10}, {"uuid":"a2", "name":"n2", "age":20},{"uuid":"a3", "name":"n3, "age":30}]
     * keys = ["uuid", "name"]
     *
     * 结果:
     * [{"uuid":"a1", "name": "n1"}, {"uuid":"a2", "name":"n2"},{"uuid":"a3", "name":"n3}]
     */
    public static <V> List<V> lstFilter(List<V> lst, String keys){
        if(isEmpty(lst)){
            return Collections.EMPTY_LIST;
        }
        if(keys == null){
            return lst;
        }
        List<String> names = MyReflectTool.getKeys(lst.get(0));
        List<String> includeKeys = str2list(keys);
        for(V item: lst){
            for(String one: names){
                if(!includeKeys.contains(one)){
                    MyReflectTool.setValue(item, one, null);
                }
            }
        }
        return lst;
    }

    /**
     *
     * 例子:
     * lst = [{"uuid":"a1", "name": "n1", "age":10}, {"uuid":"a2", "name":"n2", "age":20},{"uuid":"a3", "name":"n3, "age":30}]
     * excludeKeys = ["age", "uuid"]
     *
     * 结果:
     * [{"name": "n1"}, {"name":"n2"},{"name":"n3}]
     */
    public static <T> List<T> lstFilterExclude(List<T> lst, String excludeKeys){
        if(isEmpty(lst)){
            return Collections.EMPTY_LIST;
        }
        if(excludeKeys == null){
            return lst;
        }
        List<String> excludeKeyLists = str2list(excludeKeys);
        for(T item: lst) {
            for (String excludeKey : excludeKeyLists) {
                MyReflectTool.setValue(item, excludeKey, null);
            }
        }
        return lst;
    }

    public static List<String> str2list(String strs){
        return str2list(strs, ",");
    }

    public static Set<String> str2set(String strs){
        return new HashSet<>(str2list(strs));
    }

    public static List<String> str2list(String strs, String splitter){
        List<String> result = new ArrayList<>();
        Arrays.stream(strs.split(splitter)).forEach(one -> result.add(one.trim()));
        return result;
    }

    public static <T> T getFromList(List<T> data, String key, Object value){
        if(MyCollectionTool.isEmpty(data) || key == null){
            return null;
        }
        for(T t: data){
            Object mv = MyReflectTool.getValue(t, key);
            if(compareValue(mv, value)){
                return t;
            }
        }
        return null;
    }

    public static <T> boolean contains(List<T> data, String key, Object value){
        if(isEmpty(data) || key == null || value == null){
            return false;
        }
        for(T t: data){
            Object mv = MyReflectTool.getValue(t, key);
            if(compareValue(mv, value)){
                return true;
            }
        }
        return false;
    }

    public static <T> Map<String, T> list2map(List<T> data, String prop){
        Map<String, T> result = new HashMap<>();
        data.forEach(one -> {
            Object uuid = MyReflectTool.getFieldValue(one, prop);
            if(uuid != null){
                result.put(String.valueOf(uuid), one);
            }
        });
        return result;
    }

    public static boolean compareValue(Object o1, Object o2){
        if(o1 == o2){
            return true;
        }
        if(o1 == null || o2 == null){
            return false;
        }
        switch (o1.getClass().getSimpleName().toLowerCase()){
            case "string":
            case "double":
            case "long":
            case "boolean":
            case "int":
            case "integer":
                return Objects.equals(o1, o2);
            default:
                return false;
        }
    }

    public static Set<String> list2set(List<String> courseIds) {
        Set<String> ids = new HashSet<>();
        courseIds.forEach(one -> ids.add(one));
        return ids;
    }
}
