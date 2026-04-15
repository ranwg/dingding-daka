package com.dingding.daka.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.dingding.daka.service.ClockService;
import com.dingding.daka.util.LogUtil;

/**
 * 闹钟广播接收器 - 精确时间触发时唤醒屏幕并执行打卡
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "========== 闹钟广播收到 ==========");
        LogUtil.d(TAG, "【Receiver】接收进程: " + (android.os.Process.myPid()));
        LogUtil.d(TAG, "【Receiver】Android版本: " + Build.VERSION.SDK_INT);

        if (intent == null) {
            LogUtil.e(TAG, "【Receiver】Intent 为空");
            return;
        }

        String action = intent.getAction();
        if (action == null) {
            LogUtil.e(TAG, "【Receiver】Action 为空");
            return;
        }

        LogUtil.d(TAG, "【Receiver】Action: " + action);

        int alarmType = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_TYPE, -1);
        String date = intent.getStringExtra(AlarmScheduler.EXTRA_DATE);
        String time = intent.getStringExtra(AlarmScheduler.EXTRA_TIME);

        LogUtil.d(TAG, "【Receiver】闹钟类型: " + alarmType + " (" + (alarmType == AlarmScheduler.TYPE_MORNING ? "早班" : alarmType == AlarmScheduler.TYPE_EVENING ? "晚班" : "未知") + ")");
        LogUtil.d(TAG, "【Receiver】日期: " + date);
        LogUtil.d(TAG, "【Receiver】时间: " + time);

        if (date == null || time == null) {
            LogUtil.e(TAG, "【Receiver】日期或时间为空，忽略");
            return;
        }

        if (alarmType != AlarmScheduler.TYPE_MORNING && alarmType != AlarmScheduler.TYPE_EVENING) {
            LogUtil.e(TAG, "【Receiver】无效的闹钟类型: " + alarmType);
            return;
        }

        LogUtil.d(TAG, "【Receiver】准备启动 ClockService...");

        // 启动服务执行打卡
        Intent serviceIntent = new Intent(context, ClockService.class);
        serviceIntent.setAction(ClockService.ACTION_EXECUTE_CLOCK);
        serviceIntent.putExtra(AlarmScheduler.EXTRA_ALARM_TYPE, alarmType);
        serviceIntent.putExtra("date", date);
        serviceIntent.putExtra("time", time);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
                LogUtil.d(TAG, "【Receiver】前台服务已启动 (O+)");
            } else {
                context.startService(serviceIntent);
                LogUtil.d(TAG, "【Receiver】服务已启动");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "【Receiver】启动服务失败: " + e.getMessage());
            e.printStackTrace();
        }

        LogUtil.d(TAG, "【Receiver】处理完成");
    }
}
