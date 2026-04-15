package com.dingding.daka.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.dingding.daka.data.PreferencesManager;
import com.dingding.daka.service.ClockService;
import com.dingding.daka.util.LogUtil;

/**
 * 开机广播接收器
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            LogUtil.e(TAG, "Intent 为空");
            return;
        }

        String action = intent.getAction();
        LogUtil.d(TAG, "========== 收到广播 ==========");
        LogUtil.d(TAG, "Action: " + action);
        LogUtil.d(TAG, "Android版本: " + Build.VERSION.SDK_INT);
        LogUtil.d(TAG, "设备厂商: " + Build.MANUFACTURER);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            LogUtil.d(TAG, "【BOOT】设备启动完成广播已收到");

            PreferencesManager prefsManager = PreferencesManager.getInstance(context);
            boolean isAutoEnabled = prefsManager.isAutoEnabled();
            LogUtil.d(TAG, "【BOOT】自动打卡开关状态: " + (isAutoEnabled ? "已开启" : "已关闭"));

            if (isAutoEnabled) {
                LogUtil.d(TAG, "【BOOT】准备启动服务...");

                // 延迟5秒后启动，确保系统就绪
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        // 先启动前台服务
                        Intent serviceIntent = new Intent(context, ClockService.class);
                        serviceIntent.setAction(ClockService.ACTION_START);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent);
                            LogUtil.d(TAG, "【BOOT】前台服务已启动 (O+)");
                        } else {
                            context.startService(serviceIntent);
                            LogUtil.d(TAG, "【BOOT】服务已启动");
                        }

                        // 再调度闹钟
                        AlarmScheduler.scheduleNextAlarm(context);
                        LogUtil.d(TAG, "【BOOT】闹钟已重新调度");
                    } catch (Exception e) {
                        LogUtil.e(TAG, "【BOOT】启动服务失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }, 5000);
            } else {
                LogUtil.d(TAG, "【BOOT】自动打卡未开启，不启动服务");
            }
        } else {
            LogUtil.w(TAG, "【BOOT】收到未知广播: " + action);
        }
    }
}
