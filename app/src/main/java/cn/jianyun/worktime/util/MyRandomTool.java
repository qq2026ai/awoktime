package cn.jianyun.worktime.util;


import java.util.UUID;

public class MyRandomTool {

    public static String uuids(){
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
    }

    public static int randomNumber(int begin, int end){
        return (int)Math.floor(Math.random() * (end - begin) + begin);
    }

    public static int randomNumber(){
        return randomNumber(0, 1000);
    }

    public static int randomNumber(int max){
        return randomNumber(0,max);
    }

    public static String random(int len){
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < len; i++) {
            int m = randomNumber(65, 91);
            if(m % 3 == 1){
                m = randomNumber(48,58);
            }
            char x = (char) m;
            sbf.append(x);
        }
        return sbf.toString().toLowerCase();
    }




}
