package cn.jianyun.worktime.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class WeekTool {
    public static String getWeekName(String weekday){
        Optional<SelectDO> oo = SelectUtil.WEEKDAYS.stream().filter(one -> one.value.equals(weekday)).findFirst();
        if(oo.isPresent()){
            return oo.get().label;
        }
        return "异常";
    }

    public static boolean isContinue(List<Integer> fixWeeks, int step){
       if(fixWeeks.isEmpty() || fixWeeks.size() < 2){
           return false;
       }
       int last = fixWeeks.get(0);
       for(int i = 1;i < fixWeeks.size();i ++){
           int temp = fixWeeks.get(i);
           if(temp - step != last){
               return false;
           }
           last = temp;
       }
       return true;
    }

    public static String getWeekInfo(List<Integer> fixWeeks, String defaultLabel){
        if(fixWeeks.isEmpty()){
            return defaultLabel;
        }
        if(fixWeeks.size() < 2){
            return fixWeeks.get(0) + "周";
        }
        fixWeeks = fixWeeks.stream().sorted().collect(Collectors.toList());
        int first = fixWeeks.get(0);
        int last = fixWeeks.get(fixWeeks.size() -1);
        if (isContinue(fixWeeks, 2)) {
            return first + "-" + last + (first % 2 == 0 ? "双周" : "单周");
        }
        List<String> kk = new ArrayList<>();

        for (int i = 1; i < fixWeeks.size(); i++) {
            int pre = fixWeeks.get(i-1);
            int cur = fixWeeks.get(i);
            if(cur - pre != 1) {
                //继续
                if(first != pre){
                    kk.add(first + "-" + pre);
                }
                else{
                    kk.add(first + "");
                }
                first = cur;
                if(i == fixWeeks.size() - 1){
                    kk.add(cur + "");
                }
            }
            else{
                if(i == fixWeeks.size() - 1){
                    kk.add(first + "-" + cur);
                }
            }
        }
        String weeks = MyCollectionTool.join(kk);
        return weeks + "周";
    }




}
