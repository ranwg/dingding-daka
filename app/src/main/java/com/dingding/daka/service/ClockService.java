package com.dingding.daka.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Looper;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.dingding.daka.MainActivity;
import com.dingding.daka.R;
import com.dingding.daka.data.AppDatabase;
import com.dingding.daka.data.ClockRecord;
import com.dingding.daka.data.DailyPlan;
import com.dingding.daka.data.PlanDao;
import com.dingding.daka.receiver.AlarmScheduler;
import com.dingding.daka.util.LogUtil;
import com.dingding.daka.util.TimeUtils;

/**
 * 打卡服务 - 前台服务，确保闹钟可靠触发
 */
public class ClockService extends Service {

    private static final String TAG = "ClockService";
    private static final String CHANNEL_ID = "clock_service_channel";
    private static final int NOTIFICATION_ID = 1001;

    public static final String ACTION_START = "com.dingding.daka.action.START_SERVICE";
    public static final String ACTION_STOP = "com.dingding.daka.action.STOP_SERVICE";
    public static final String ACTION_CHECK_NOW = "com.dingding.daka.action.CHECK_NOW";
    public static final String ACTION_EXECUTE_CLOCK = "com.dingding.daka.action.EXECUTE_CLOCK";

    private Handler handler;
    private Runnable checkRunnable;
    private boolean isRunning = false;

    private PowerManager.WakeLock wakeLock;
    private PowerManager.WakeLock screenWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "服务 onCreate");
        createNotificationChannel();
        handler = new Handler(Looper.getMainLooper());
        acquireWakeLock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "服务 onStartCommand, action=" + (intent != null ? intent.getAction() : "null"));

        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            LogUtil.d(TAG, "收到停止命令");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (ACTION_EXECUTE_CLOCK.equals(action)) {
            // 闹钟触发的直接执行
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            int type = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_TYPE, AlarmScheduler.TYPE_MORNING);
            LogUtil.d(TAG, "收到执行命令: " + date + " " + time + ", type=" + type);
            executeClockFromAlarm(date, time, type);
            return START_STICKY;
        }

        if (ACTION_CHECK_NOW.equals(action)) {
            LogUtil.d(TAG, "收到立即检查命令");
            handler.post(() -> checkAndExecuteClock());
            return START_STICKY;
        }

        // 启动前台服务
        startForeground(NOTIFICATION_ID, createNotification());
        isRunning = true;

        // 设置闹钟
        AlarmScheduler.scheduleNextAlarm(this);

        // 开始定时检查（作为备用，每分钟检查一次）
        startChecking();

        LogUtil.d(TAG, "服务已启动，前台通知已显示");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopChecking();
        releaseWakeLock();
        isRunning = false;
        LogUtil.d(TAG, "服务已停止");
    }

    private void acquireWakeLock() {
        LogUtil.d(TAG, "【WakeLock】正在获取 WakeLock...");
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK,
                        "ClockService::WakeLock"
                );
                LogUtil.d(TAG, "【WakeLock】WakeLock 对象已创建");
            } else {
                LogUtil.e(TAG, "【WakeLock】PowerManager 为空");
            }
        }
        if (wakeLock != null && !wakeLock.isHeld()) {
            try {
                wakeLock.acquire(10 * 60 * 60 * 1000L); // 10小时
                LogUtil.d(TAG, "【WakeLock】PartialWakeLock 已获取");
            } catch (Exception e) {
                LogUtil.e(TAG, "【WakeLock】获取 WakeLock 失败: " + e.getMessage());
            }
        } else if (wakeLock != null && wakeLock.isHeld()) {
            LogUtil.d(TAG, "【WakeLock】WakeLock 已持有，无需重复获取");
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            try {
                wakeLock.release();
                LogUtil.d(TAG, "【WakeLock】PartialWakeLock 已释放");
            } catch (Exception e) {
                LogUtil.e(TAG, "【WakeLock】释放 WakeLock 失败: " + e.getMessage());
            }
        } else {
            LogUtil.d(TAG, "【WakeLock】WakeLock 未持有，无需释放");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "打卡服务",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("保持打卡服务在后台运行");
            channel.setShowBadge(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("叮叮打卡")
                .setContentText("后台运行中，自动打卡已启用")
                .setSmallIcon(R.drawable.ic_open_app)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void startChecking() {
        stopChecking();

        checkRunnable = new Runnable() {
            @Override
            public void run() {
                checkAndExecuteClock();
                // 每60秒检查一次
                handler.postDelayed(this, 60000);
            }
        };
        // 立即执行一次检查
        handler.post(checkRunnable);
        LogUtil.d(TAG, "轮询检查已启动，每60秒一次");
    }

    private void stopChecking() {
        if (checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
            checkRunnable = null;
        }
    }

    /**
     * 轮询检查（备用机制）
     */
    private void checkAndExecuteClock() {
        LogUtil.d(TAG, "--- 轮询检查 ---");
        String today = TimeUtils.getToday();
        AppDatabase db = AppDatabase.getInstance(this);
        PlanDao planDao = db.planDao();

        DailyPlan plan = planDao.getByDate(today);
        if (plan == null || !plan.isEnabled()) {
            LogUtil.d(TAG, "今日无打卡计划");
            return;
        }

        String currentTime = TimeUtils.getCurrentTime();
        LogUtil.d(TAG, "当前时间: " + currentTime);

        // 检查早班
        if (plan.getMorningTime() != null && currentTime.equals(plan.getMorningTime())) {
            LogUtil.d(TAG, "早班时间到达（轮询触发）");
            executeClock(today, plan.getMorningTime(), AlarmScheduler.TYPE_MORNING);
        }

        // 检查晚班
        if (plan.getEveningTime() != null && currentTime.equals(plan.getEveningTime())) {
            LogUtil.d(TAG, "晚班时间到达（轮询触发）");
            executeClock(today, plan.getEveningTime(), AlarmScheduler.TYPE_EVENING);
        }
    }

    /**
     * 从闹钟触发的执行
     */
    private void executeClockFromAlarm(String date, String time, int type) {
        LogUtil.d(TAG, "========== 闹钟触发执行打卡 ==========");
        LogUtil.d(TAG, "日期: " + date + ", 时间: " + time + ", 类型: " + type);

        executeClock(date, time, type);
    }

    /**
     * 执行打卡
     */
    private void executeClock(String date, String time, int type) {
        LogUtil.d(TAG, "开始执行打卡...");

        // 唤醒屏幕
        wakeUpScreen();

        // 延迟启动钉钉
        handler.postDelayed(() -> {
            boolean success = openDingTalk();
            LogUtil.d(TAG, "打开钉钉结果: " + success);

            // 保存打卡记录
            saveClockRecord(date, time, type, success);

            // 发送通知
            showResultNotification(success, type);

        }, 2000);
    }

    /**
     * 唤醒屏幕
     */
    private void wakeUpScreen() {
        try {
            LogUtil.d(TAG, "正在唤醒屏幕...");

            // 点亮屏幕
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                screenWakeLock = powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "ClockService::ScreenWakeLock"
                );
                screenWakeLock.acquire(20000); // 20秒
                LogUtil.d(TAG, "屏幕已唤醒，WakeLock已获取");
            }

            // 解锁
            dismissKeyguard();

        } catch (Exception e) {
            LogUtil.e(TAG, "唤醒屏幕失败: " + e.getMessage());
        }
    }

    /**
     * 解锁屏幕
     */
    private void dismissKeyguard() {
        LogUtil.d(TAG, "【Screen】尝试禁用锁屏...");
        try {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager == null) {
                LogUtil.w(TAG, "【Screen】KeyguardManager为null");
                return;
            }

            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("ClockService");
            if (keyguardLock != null) {
                keyguardLock.disableKeyguard();
                LogUtil.d(TAG, "【Screen】锁屏已禁用");
            } else {
                LogUtil.w(TAG, "【Screen】KeyguardLock 为空");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "【Screen】解锁失败: " + e.getMessage());
        }
    }

    private boolean openDingTalk() {
        LogUtil.d(TAG, "【DingTalk】开始打开钉钉...");
        try {
            // 钉钉可能的包名列表
            String[] dingtalkPackages = {
                "com.alibaba.android.rimet",           // 旧版钉钉
                "com.dingtalk",                         // 新版钉钉
                "com.alibaba.android.rimet.biz",        // 钉钉企业版
            };

            for (String packageName : dingtalkPackages) {
                LogUtil.d(TAG, "【DingTalk】尝试包名: " + packageName);
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (launchIntent != null) {
                    LogUtil.d(TAG, "【DingTalk】找到包名: " + packageName);
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(launchIntent);
                    LogUtil.d(TAG, "【DingTalk】钉钉启动成功");
                    return true;
                }
            }

            // 如果都找不到，尝试通过Uri直接打开
            LogUtil.w(TAG, "【DingTalk】所有包名都未找到，尝试Uri方式...");
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("dingtalk://"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                LogUtil.d(TAG, "【DingTalk】Uri方式启动成功");
                return true;
            } catch (Exception e) {
                LogUtil.e(TAG, "【DingTalk】Uri方式也失败");
            }

            LogUtil.w(TAG, "【DingTalk】未找到钉钉应用");
            return false;
        } catch (Exception e) {
            LogUtil.e(TAG, "【DingTalk】启动钉钉失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void saveClockRecord(String date, String time, int type, boolean success) {
        try {
            AppDatabase db = AppDatabase.getInstance(this);
            ClockRecord record = new ClockRecord();
            record.setDate(date);
            record.setExecuteTime(time);
            record.setType(type);
            record.setStatus(success ? ClockRecord.STATUS_SUCCESS : ClockRecord.STATUS_FAILED);
            record.setRemark(success ? "自动执行" : "启动失败");

            db.recordDao().insert(record);
            LogUtil.d(TAG, "打卡记录已保存: " + (success ? "成功" : "失败"));
        } catch (Exception e) {
            LogUtil.e(TAG, "保存记录失败: " + e.getMessage());
        }
    }

    private void showResultNotification(boolean success, int type) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        String title = success ? "打卡成功" : "打卡失败";
        String content = type == AlarmScheduler.TYPE_MORNING ? "早班打卡" : "晚班打卡";
        if (!success) {
            content += " - 请手动打卡";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_open_app)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(NOTIFICATION_ID + type, builder.build());
    }
}
