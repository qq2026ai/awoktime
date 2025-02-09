package cn.jianyun.worktime.util;

import static cn.jianyun.worktime.util.CommonToolKt.mlog;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cn.jianyun.worktime.main.base.model.NotifyInfo;


public class CalendarReminderUtils {
    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "计划师通知";
    private static String CALENDARS_ACCOUNT_NAME = "计划师通知";
    private static String CALENDARS_ACCOUNT_TYPE = "cn.jianyun.plan";
    private static String CALENDARS_DISPLAY_NAME = "计划师账户";

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    @SuppressLint("Range")
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();

            for (int i = 0; i < count; i++) {
                userCursor.moveToNext();
                int nameIdx = userCursor.getColumnIndex("name");
                String nameValue = userCursor.getString(nameIdx);
                if (CALENDARS_NAME.equals(nameValue)) {
                    return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
                }
            }
            return -1;
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */

    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }


    /**
     * 批量添加日历事件
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean batchAddCalendarEvent(Context context, List<NotifyInfo> notifyInfos) {
        if (context == null) {
            mlog("获取日历账户失败01");
            return false;
        }
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            mlog("获取日历账户失败");
            return false;
        }

        for(NotifyInfo notifyInfo: notifyInfos){
            //添加日历事件
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(notifyInfo.getRealTime());//设置开始时间
            long start = mCalendar.getTime().getTime();
            mCalendar.setTimeInMillis(start + 1000);//设置终止时间，开始时间加1秒
            long end = mCalendar.getTime().getTime();
            ContentValues event = new ContentValues();
            event.put("title", notifyInfo.getTitle());
            event.put("description", notifyInfo.getDescription());
            event.put("calendar_id", calId); //插入账户的id

//        event.put("event_id", notifyInfo.uuid());
            event.put(CalendarContract.Events.ORGANIZER, notifyInfo.getBizName());
            event.put(CalendarContract.Events.DTSTART, start);
            event.put(CalendarContract.Events.DTEND, end);
//        event.put(CalendarContract.Events.EVENT_LOCATION, );
            event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有提醒提醒
//        event.put("hasAlarmClock", notifyInfo.getAlarm() ? 1 : 0);//设置有强提醒
            event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
            String brand = CacheUtil.get(context, "brand");
            try{
                Uri newEvent = tryToCreateAlarmEvent(context, event, notifyInfo.getAlarm()); //添加事件
                if (newEvent == null) { //添加日历事件失败直接返回
                    mlog("添加日历事件失败");
                    return false;
                }
                mlog("成功添加日历事件:", notifyInfo.getTitle(), notifyInfo.getNotifyTime());
                //事件提醒的设定
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
                values.put(CalendarContract.Reminders.MINUTES, notifyInfo.getBeforeMinute() );// 提前previousMinute分钟有提醒
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
                if (uri == null) { //添加事件提醒失败直接返回
                    mlog("添加日程提醒失败");
                    continue;
                }
                mlog("添加日程提醒成功");

                if(notifyInfo.getAlarm()){
                    try{
                        Uri extendedPropUri = CalendarContract.ExtendedProperties.CONTENT_URI;
                        extendedPropUri = extendedPropUri.buildUpon()
                                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE).build();
                        ContentValues extendedProperties = new ContentValues();
                        extendedProperties.put(CalendarContract.ExtendedProperties.EVENT_ID,ContentUris.parseId(newEvent));

                        if ("小米".equals(brand)) {
                            extendedProperties.put(CalendarContract.ExtendedProperties.NAME,"agenda_info");
                            extendedProperties.put(CalendarContract.ExtendedProperties.VALUE,  "{\"need_alarm\":true}");
                        }
                        else if("vivo".equals(brand)){
                            extendedProperties.put(CalendarContract.ExtendedProperties.NAME,"reminder_alert_type");
                            extendedProperties.put(CalendarContract.ExtendedProperties.VALUE,  "1");
                        }

                        Uri uriExtended = context.getContentResolver().insert(extendedPropUri, extendedProperties);
                        if (uriExtended == null) { //添加事件提醒失败直接返回
                            mlog("添加日程闹钟失败");
                            continue;
                        }
                        mlog("添加日程闹钟成功");
                    }
                    catch (Exception ee){
                        mlog("添加日程闹钟失败嘞");
                    }
                }
            }
            catch (Exception e){
                mlog("写入日历失败", e);
            }
        }
        return true;
    }


    /**
     * 添加日历事件
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean addCalendarEvent(Context context, NotifyInfo notifyInfo) {
        if (context == null) {
            mlog("获取日历账户失败01");
            return false;
        }
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            mlog("获取日历账户失败");
            return false;
        }
        //添加日历事件
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(notifyInfo.getRealTime());//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + 1000);//设置终止时间，开始时间加1秒
        long end = mCalendar.getTime().getTime();
        ContentValues event = new ContentValues();
        event.put("title", notifyInfo.getTitle());
        event.put("description", notifyInfo.getDescription());
        event.put("calendar_id", calId); //插入账户的id
//        event.put("event_id", notifyInfo.uuid());
        event.put(CalendarContract.Events.ORGANIZER, notifyInfo.getBizName());
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
//        event.put(CalendarContract.Events.EVENT_LOCATION, );
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有提醒提醒
//        event.put("hasAlarmClock", notifyInfo.getAlarm() ? 1 : 0);//设置有强提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        String brand = CacheUtil.get(context, "brand");
        try{
            Uri newEvent = tryToCreateAlarmEvent(context, event, notifyInfo.getAlarm()); //添加事件
            if (newEvent == null) { //添加日历事件失败直接返回
                mlog("添加日历事件失败");
                return false;
            }
            mlog("成功添加日历事件:", notifyInfo.getTitle(), notifyInfo.getNotifyTime());
            //事件提醒的设定
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
            values.put(CalendarContract.Reminders.MINUTES, notifyInfo.getBeforeMinute() );// 提前previousMinute分钟有提醒
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
            if (uri == null) { //添加事件提醒失败直接返回
                mlog("添加日程提醒失败");
                return false;
            }
            mlog("添加日程提醒成功");

            if(notifyInfo.getAlarm()){
                try{
                    Uri extendedPropUri = CalendarContract.ExtendedProperties.CONTENT_URI;
                    extendedPropUri = extendedPropUri.buildUpon()
                            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE).build();
                    ContentValues extendedProperties = new ContentValues();
                    extendedProperties.put(CalendarContract.ExtendedProperties.EVENT_ID,ContentUris.parseId(newEvent));

                    if ("小米".equals(brand)) {
                        extendedProperties.put(CalendarContract.ExtendedProperties.NAME,"agenda_info");
                        extendedProperties.put(CalendarContract.ExtendedProperties.VALUE,  "{\"need_alarm\":true}");
                    }
                    else if("vivo".equals(brand)){
                        extendedProperties.put(CalendarContract.ExtendedProperties.NAME,"reminder_alert_type");
                        extendedProperties.put(CalendarContract.ExtendedProperties.VALUE,  "1");
                    }

                    Uri uriExtended = context.getContentResolver().insert(extendedPropUri, extendedProperties);
                    if (uriExtended == null) { //添加事件提醒失败直接返回
                        mlog("添加日程闹钟失败");
                        return false;
                    }
                    mlog("添加日程闹钟成功");
                }
                catch (Exception ee){
                    mlog("添加日程闹钟失败嘞");
                }
            }
        }
        catch (Exception e){
            mlog("写入日历失败", e);
            return false;
        }

        return true;
    }

    private static Uri tryToCreateAlarmEvent(Context context, ContentValues event, boolean alarm) {
        Uri uri = null;
        if(alarm){
            uri = tryToCreateAlarmEvent(context, event, "hasAlarmClock");
            if(uri == null){
                uri = tryToCreateAlarmEvent(context, event, "force_reminder");
            }
        }
        if(uri == null){
            return tryToCreateAlarmEvent(context, event, null);
        }
        else{
            return uri;
        }
    }

    private static Uri tryToCreateAlarmEvent(Context context, ContentValues event, String prop) {
        try{
            if(prop != null){
                int k = CacheUtil.getInt(context, prop, 0);
                if(k == 404){
                    return null;
                }
                event.put(prop, "1");
            }
            Uri newEvent = context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件
            if(newEvent != null){
                return newEvent;
            }
        }
        catch (Exception e){
            if(prop != null){
                CacheUtil.setInt(context, prop, 404);
            }
            mlog("addEventError", e);
            event.remove(prop);
        }
        return null;

    }

    //post 小米



    /**
     * 删除日历事件
     */
    @SuppressLint("Range")
    public static void cleanCalendarEvent(Context context, String bizName) {
        try {
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            if (eventCursor == null) { //查询返回空值
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    int nameIdx = eventCursor.getColumnIndex("account_name");
                    String nameValue = eventCursor.getString(nameIdx);
                    if (CALENDARS_NAME.equals(nameValue)) {
//                        Arrays.stream(eventCursor.getColumnNames()).forEach(it -> {
//                            mlog("eventInfo", it, eventCursor.getString(eventCursor.getColumnIndex(it)));
//                        });
                        int organIdx = eventCursor.getColumnIndex("organizer");
                        String organizer = eventCursor.getString(organIdx);
                        boolean flag = organizer.equals(bizName);
                        if(!flag){
                            continue;
                        }
                        @SuppressLint("Range") int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        mlog("事件id", id);
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows > -1) { //事件删除失败
                            mlog("删除事件成功");
                        }
                    }
                }
            }
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        catch (Exception ee){

        }
    }


    /**
     * 预览测试
     */
    @SuppressLint("Range")
    public static Map<String,String> getTestEventData(Context context) {
        Map<String,String> result = new HashMap<>();
        try {
            //先删除历史测试
            cleanCalendarEvent(context, "普通测试");
            //写入事件
            addCalendarEvent(context, new NotifyInfo("普通测试", "普通提醒", "来自计划师", MyDateTool.toDateTimeString(MyDateTool.gap(new Date(), Calendar.SECOND, 20)), false, 0));

            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            if (eventCursor == null) { //查询返回空值
                return new HashMap<>();
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    int nameIdx = eventCursor.getColumnIndex("account_name");
                    String nameValue = eventCursor.getString(nameIdx);
                    int organIdx = eventCursor.getColumnIndex("organizer");
                    String organizer = eventCursor.getString(organIdx);
                    if (CALENDARS_NAME.equals(nameValue) && "普通测试".equals(organizer)) {
                        Arrays.stream(eventCursor.getColumnNames()).forEach(it -> {
                            result.put(it, eventCursor.getString(eventCursor.getColumnIndex(it)));
                        });
                    }
                }
            }
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        catch (Exception ee){

        }
        return result;
    }


    /**
     * 预览闹钟数据
     */
    @SuppressLint("Range")
    public static Map<String,String> getTestAlarmEventData(Context context, boolean clean) {
        Map<String,String> result = new HashMap<>();
        try {
            if(clean){
                cleanCalendarEvent(context, "闹钟测试");
            }
            if(!hasTestEventData(context, "闹钟测试")) {
                //写入事件
                addCalendarEvent(context, new NotifyInfo("闹钟测试", "闹钟测试", "来自计划师", MyDateTool.toDateTimeString(MyDateTool.gap(new Date(), Calendar.SECOND, 20)), true, 0));
            }
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            if (eventCursor == null) { //查询返回空值
                return new HashMap<>();
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    int nameIdx = eventCursor.getColumnIndex("account_name");
                    String nameValue = eventCursor.getString(nameIdx);
                    int organIdx = eventCursor.getColumnIndex("organizer");
                    String organizer = eventCursor.getString(organIdx);
                    if (CALENDARS_NAME.equals(nameValue) && "闹钟测试".equals(organizer)) {
                        Arrays.stream(eventCursor.getColumnNames()).forEach(it -> {
                            mlog(it, eventCursor.getString(eventCursor.getColumnIndex(it)));
                            result.put(it, eventCursor.getString(eventCursor.getColumnIndex(it)));
                        });
                        mlog("start 闹钟测试");
                        long eventId = eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Map<String, String> extFields = getEventExtendProperties(context, eventId);
                        result.putAll(extFields);
                    }
                }
            }
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        catch (Exception ee){

        }
        Map<String, String> finalResult = new HashMap<>();
        Map<String,String> KK = getTestEventData(context);
        for(String key: result.keySet()){
            String a1 = result.get(key);
            String a2 = KK.get(key);
            if(a1 != null && a2 != null) {
                if(!a1.equals(a2)){
                    finalResult.put(key, a1 + "_" + a2);
                }
            }
            if(a1 != null && a2 == null){
                finalResult.put(key, a1 + "_" + a2);
            }
        }
        return finalResult;
    }



    /**
     * 预览闹钟数据
     */
    @SuppressLint("Range")
    public static Map<String,String> getTestAlarmEventData2(Context context) {
        Map<String,String> result = new HashMap<>();
        try {
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            if (eventCursor == null) { //查询返回空值
                return new HashMap<>();
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    int nameIdx = eventCursor.getColumnIndex("title");
                    String title = eventCursor.getString(nameIdx);
                    mlog("title", title);
                    if ("系统闹钟".equals(title)) {
                        Arrays.stream(eventCursor.getColumnNames()).forEach(it -> {
                            result.put(it, eventCursor.getString(eventCursor.getColumnIndex(it)));
                        });
                        long eventId = eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Map<String, String> extFields = getEventExtendProperties(context, eventId);
                        result.putAll(extFields);
                    }
                }
            }
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        catch (Exception ee){

        }
        Map<String, String> finalResult = new HashMap<>();
        Map<String,String> KK = getTestEventData(context);
        for(String key: result.keySet()){
            String a1 = result.get(key);
            String a2 = KK.get(key);
            if(a1 != null && a2 != null) {
                if(!a1.equals(a2)){
                    finalResult.put(key, a1 + "_" + a2);
                }
            }
            if(a1 != null && a2 == null){
                finalResult.put(key, a1 + "_" + a2);
            }
        }
        return finalResult;
    }


    @SuppressLint("Range")
    public static boolean hasTestEventData(Context context, String bizName) {
        try {
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            if (eventCursor == null) { //查询返回空值
                return false;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    int nameIdx = eventCursor.getColumnIndex("account_name");
                    String nameValue = eventCursor.getString(nameIdx);
                    int organIdx = eventCursor.getColumnIndex("organizer");
                    String organizer = eventCursor.getString(organIdx);
                    if (CALENDARS_NAME.equals(nameValue) &&  bizName.equals(organizer)) {
                        return true;
                    }
                }
            }
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        catch (Exception ee){

        }
        return false;
    }


    @SuppressLint("Range")
    private static Map<String, String> getEventExtendProperties(Context context, long eventId) {
        Map<String, String> result = new HashMap<>();
        Cursor eventCursor2 = context.getContentResolver().query(CalendarContract.ExtendedProperties.CONTENT_URI, null, null, null, null);
        if (eventCursor2 == null) { //查询返回空值
            result.put("ext_empty", "1");
            return result;
        }
        if (eventCursor2.getCount() > 0) {
            //遍历所有事件，找到title跟需要查询的title一样的项
            for (eventCursor2.moveToFirst(); !eventCursor2.isAfterLast(); eventCursor2.moveToNext()) {
                String evId = eventCursor2.getString(eventCursor2.getColumnIndex("event_id"));
                if(evId != null && evId.equals(eventId + "")){
                    Arrays.stream(eventCursor2.getColumnNames()).forEach(it -> {
                        result.put("ext_" + it, eventCursor2.getString(eventCursor2.getColumnIndex(it)));
                    });
                }
            }
        }
        if (eventCursor2 != null) {
            eventCursor2.close();
        }
        return result;
    }

}