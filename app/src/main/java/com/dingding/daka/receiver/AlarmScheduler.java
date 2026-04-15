package com.dingding.daka.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.dingding.daka.data.AppDatabase;
import com.dingding.daka.data.DailyPlan;
import com.dingding.daka.data.PlanDao;
import com.dingding.daka.util.LogUtil;
import com.dingding.daka.util.TimeUtils;

/**
 * 闹钟调度器 - 负责设置精确闹钟
 */
public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";
    public static final String ACTION_MORNING_ALARM = "com.dingding.daka.MORNING_ALARM";
    public static final String ACTION_EVENING_ALARM = "com.dingding.daka.EVENING_ALARM";
    public static final String EXTRA_ALARM_TYPE = "alarm_type";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";

    public static final int TYPE_MORNING = 1;
    public static final int TYPE_EVENING = 2;

    // 固定的请求码，确保能取消之前的闹钟
    private static final int MORNING_REQUEST_CODE = 1001;
    private static final int EVENING_REQUEST_CODE = 1002;

    public static void scheduleNextAlarm(Context context) {
        LogUtil.d(TAG, "========== 开始调度闹钟 ==========");
        LogUtil.d(TAG, "【Scheduler】检查闹钟服务可用性...");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            LogUtil.e(TAG, "【Scheduler】无法获取 AlarmManager");
            return;
        }

        // 检查闹钟权限（Android 12+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean canSchedule = alarmManager.canScheduleExactAlarms();
            LogUtil.d(TAG, "【Scheduler】精确闹钟权限: " + (canSchedule ? "已授权" : "未授权"));
            if (!canSchedule) {
                LogUtil.w(TAG, "【Scheduler】缺少精确闹钟权限，闹钟可能不精确");
            }
        }

        // 先取消所有闹钟
        LogUtil.d(TAG, "【Scheduler】取消旧闹钟...");
        cancelAllAlarms(context);

        String today = TimeUtils.getToday();
        String tomorrow = TimeUtils.getTomorrow();
        LogUtil.d(TAG, "【Scheduler】今天: " + today + ", 明天: " + tomorrow);

        AppDatabase db = AppDatabase.getInstance(context);
        PlanDao planDao = db.planDao();

        DailyPlan todayPlan = planDao.getByDate(today);
        DailyPlan tomorrowPlan = planDao.getByDate(tomorrow);

        LogUtil.d(TAG, "【Scheduler】今日计划: " + (todayPlan != null ? "存在" : "不存在"));
        LogUtil.d(TAG, "【Scheduler】明日计划: " + (tomorrowPlan != null ? "存在" : "不存在"));

        if (todayPlan != null) {
            LogUtil.d(TAG, "【Scheduler】今日计划启用状态: " + todayPlan.isEnabled());
        }
        if (tomorrowPlan != null) {
            LogUtil.d(TAG, "【Scheduler】明日计划启用状态: " + tomorrowPlan.isEnabled());
        }

        long currentTime = System.currentTimeMillis();
        LogUtil.d(TAG, "【Scheduler】当前时间戳: " + currentTime);

        int alarmCount = 0;

        // 设置今天的闹钟
        if (todayPlan != null && todayPlan.isEnabled()) {
            // 早班
            String morningTime = todayPlan.getMorningTime();
            LogUtil.d(TAG, "【Scheduler】今日早班时间: " + morningTime);
            if (morningTime != null && !morningTime.isEmpty()) {
                long morningTrigger = calculateTriggerTime(today, morningTime);
                LogUtil.d(TAG, "【Scheduler】早班触发时间戳: " + morningTrigger);
                if (morningTrigger > currentTime) {
                    scheduleExactAlarm(context, TYPE_MORNING, today, morningTime, MORNING_REQUEST_CODE);
                    alarmCount++;
                } else {
                    LogUtil.d(TAG, "【Scheduler】早班时间已过，跳过");
                }
            }

            // 晚班
            String eveningTime = todayPlan.getEveningTime();
            LogUtil.d(TAG, "【Scheduler】今日晚班时间: " + eveningTime);
            if (eveningTime != null && !eveningTime.isEmpty()) {
                long eveningTrigger = calculateTriggerTime(today, eveningTime);
                LogUtil.d(TAG, "【Scheduler】晚班触发时间戳: " + eveningTrigger);
                if (eveningTrigger > currentTime) {
                    scheduleExactAlarm(context, TYPE_EVENING, today, eveningTime, EVENING_REQUEST_CODE);
                    alarmCount++;
                } else {
                    LogUtil.d(TAG, "【Scheduler】晚班时间已过，跳过");
                }
            }
        }

        // 如果今天的闹钟都过了，设置明天的
        if (tomorrowPlan != null && tomorrowPlan.isEnabled()) {
            String morningTime = tomorrowPlan.getMorningTime();
            LogUtil.d(TAG, "【Scheduler】明日早班时间: " + morningTime);
            if (morningTime != null && !morningTime.isEmpty()) {
                long morningTrigger = calculateTriggerTime(tomorrow, morningTime);
                LogUtil.d(TAG, "【Scheduler】明日早班触发时间戳: " + morningTrigger);
                if (morningTrigger > currentTime) {
                    scheduleExactAlarm(context, TYPE_MORNING, tomorrow, morningTime, MORNING_REQUEST_CODE);
                    alarmCount++;
                }
            }
        }

        LogUtil.d(TAG, "【Scheduler】闹钟调度完成，共设置 " + alarmCount + " 个闹钟");
        LogUtil.d(TAG, "========== 闹钟调度结束 ==========");
    }

    private static void scheduleExactAlarm(Context context, int type, String date, String time, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(type == TYPE_MORNING ? ACTION_MORNING_ALARM : ACTION_EVENING_ALARM);
        intent.putExtra(EXTRA_ALARM_TYPE, type);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_TIME, time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = calculateTriggerTime(date, time);

        try {
            // 使用 setExactAndAllowWhileIdle 确保在省电模式下也能触发
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            }
            Log.d(TAG, "闹钟已设置: " + date + " " + time + ", 触发时间戳: " + triggerTime);
        } catch (SecurityException e) {
            Log.e(TAG, "设置闹钟失败: " + e.getMessage());
        }
    }

    public static void cancelAllAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // 取消早班闹钟
        Intent intent1 = new Intent(context, AlarmReceiver.class);
        intent1.setAction(ACTION_MORNING_ALARM);
        PendingIntent pi1 = PendingIntent.getBroadcast(
                context, MORNING_REQUEST_CODE, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pi1);
        pi1.cancel();

        // 取消晚班闹钟
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        intent2.setAction(ACTION_EVENING_ALARM);
        PendingIntent pi2 = PendingIntent.getBroadcast(
                context, EVENING_REQUEST_CODE, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pi2);
        pi2.cancel();

        Log.d(TAG, "已取消所有闹钟");
    }

    private static long calculateTriggerTime(String date, String time) {
        try {
            String dateTimeStr = date + " " + time;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.CHINA);
            java.util.Date targetTime = sdf.parse(dateTimeStr);
            if (targetTime != null) {
                return targetTime.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() + 60000;
    }
}
