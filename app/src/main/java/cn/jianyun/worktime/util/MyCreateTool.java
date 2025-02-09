package cn.jianyun.worktime.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyCreateTool {


    //创建map
    public static Map<String, Object> createMap(Object... argv) {
        Map<String, Object> params = new HashMap<>();
        if (argv == null || argv.length == 0) {
            return params;
        }
        for (int i = 0; i + 1 < argv.length; i += 2) {
            if (argv[i] == null) {
                continue;
            }
            params.put(argv[i].toString(), argv[i + 1]);
        }
        return params;
    }

    //创建string map
    public static Map<String, String> createStringMap(String... argv) {
        Map<String, String> params = new HashMap<>();
        if (argv == null || argv.length == 0) {
            return params;
        }
        for (int i = 0; i < argv.length; i += 2) {
            if (argv[i] == null) {
                continue;
            }
            params.put(argv[i], argv[i + 1]);
        }
        return params;
    }

    public static int[] createArray(int n){
        int[] arr = new int[n];
        Random r = new Random();
        for(int i=0;i<n;i++){
            arr[i] = r.nextInt(100);
        }

        return arr;
    }


    public static List<Integer> createIntegerList(int n){
        List<Integer> ds = new ArrayList<>();
        Random r = new Random();
        for(int i=0;i<n;i++){
            ds.add(r.nextInt(1000));
        }

        return ds;
    }

    public static List<Double> createDoubleList(int n){
        List<Double> ds = new ArrayList<>();
        Random r = new Random();
        for(int i=0;i<n;i++){
            ds.add(r.nextDouble() * 1000);
        }
        return ds;
    }


}
