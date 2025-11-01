package cn.jianyun.worktime.util;

public class TimeModel {

    public int hour = 0;
    public int minute = 0;


    public static TimeModel parse(String time){
        TimeModel model = new TimeModel();
        if(time == null || "".equals(time)){
            return model;
        }
        String[] ts = time.split(":");
        if(ts.length == 2){
            model.hour = MyDataTool.toInteger(ts[0], 0);
            model.minute = MyDataTool.toInteger(ts[1], 0);
        }
        return model;
    }

    public static Double getTimeMoney(String time, Float money){
        TimeModel model = parse(time);
        Double m = Double.valueOf(money);
        return m * model.hour + (m * model.minute) / 60;
    }

    public static String getTimeMoneyString(String time, Float money){
        return MyDataTool.toFixed(getTimeMoney(time, money).floatValue(), 2);
    }


}
