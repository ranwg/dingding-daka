package com.dingding.daka;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dingding.daka.data.AppDatabase;
import com.dingding.daka.data.ClockRecord;
import com.dingding.daka.data.DailyPlan;
import com.dingding.daka.data.PlanDao;
import com.dingding.daka.data.PreferencesManager;
import com.dingding.daka.receiver.AlarmScheduler;
import com.dingding.daka.service.ClockService;
import com.dingding.daka.util.LogUtil;
import com.dingding.daka.util.TimeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String DINGTALK_PACKAGE = "com.alibaba.android.rimet";

    private SwitchMaterial switchAutoClock;
    private TextView tvSwitchStatus;
    private TextView tvCountdown;
    private TextView tvNextClockInfo;
    private TextView tvMorningTime;
    private TextView tvMorningStatus;
    private TextView tvEveningTime;
    private TextView tvEveningStatus;
    private MaterialButton btnOpenDingding;
    private MaterialButton btnSettings;
    private MaterialButton btnHistory;

    private PreferencesManager prefsManager;
    private AppDatabase db;
    private PlanDao planDao;

    private Handler handler;
    private Runnable updateRunnable;
    private boolean isUpdatingUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化日志系统
        LogUtil.init(this);
        LogUtil.d("MainActivity", "应用已启动");

        initViews();
        initDatabase();
        setupListeners();
        checkBatteryOptimization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        startCountdown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountdown();
    }

    private void initViews() {
        switchAutoClock = findViewById(R.id.switchAutoClock);
        tvSwitchStatus = findViewById(R.id.tvSwitchStatus);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvNextClockInfo = findViewById(R.id.tvNextClockInfo);
        tvMorningTime = findViewById(R.id.tvMorningTime);
        tvMorningStatus = findViewById(R.id.tvMorningStatus);
        tvEveningTime = findViewById(R.id.tvEveningTime);
        tvEveningStatus = findViewById(R.id.tvEveningStatus);
        btnOpenDingding = findViewById(R.id.btnOpenDingding);
        btnSettings = findViewById(R.id.btnSettings);
        btnHistory = findViewById(R.id.btnHistory);

        handler = new Handler(Looper.getMainLooper());
    }

    private void initDatabase() {
        prefsManager = PreferencesManager.getInstance(this);
        db = AppDatabase.getInstance(this);
        planDao = db.planDao();
    }

    private void setupListeners() {
        switchAutoClock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                prefsManager.setAutoEnabled(isChecked);
                updateSwitchStatus(isChecked);
                if (isChecked) {
                    startClockService();
                } else {
                    stopClockService();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnOpenDingding.setOnClickListener(v -> openDingTalk());

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalendarSettingsActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void updateUI() {
        boolean isEnabled = prefsManager.isAutoEnabled();
        switchAutoClock.setChecked(isEnabled);
        updateSwitchStatus(isEnabled);

        String today = TimeUtils.getToday();
        DailyPlan todayPlan = planDao.getByDate(today);

        if (todayPlan != null && todayPlan.isEnabled()) {
            tvMorningTime.setText(todayPlan.getMorningTime());
            tvEveningTime.setText(todayPlan.getEveningTime());
        } else {
            tvMorningTime.setText("--:--");
            tvEveningTime.setText("--:--");
        }

        updateTodayStatus(today);
    }

    private void updateSwitchStatus(boolean isEnabled) {
        tvSwitchStatus.setText(isEnabled ? "已开启" : "已关闭");
    }

    private void updateTodayStatus(String today) {
        ClockRecord morningRecord = db.recordDao().getByDateAndType(today, ClockRecord.TYPE_MORNING);
        ClockRecord eveningRecord = db.recordDao().getByDateAndType(today, ClockRecord.TYPE_EVENING);

        if (morningRecord != null && ClockRecord.STATUS_SUCCESS.equals(morningRecord.getStatus())) {
            tvMorningStatus.setText(R.string.morning_done);
            tvMorningStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else {
            tvMorningStatus.setText(R.string.morning_pending);
            tvMorningStatus.setTextColor(ContextCompat.getColor(this, R.color.text_hint));
        }

        if (eveningRecord != null && ClockRecord.STATUS_SUCCESS.equals(eveningRecord.getStatus())) {
            tvEveningStatus.setText(R.string.evening_done);
            tvEveningStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else {
            tvEveningStatus.setText(R.string.evening_pending);
            tvEveningStatus.setTextColor(ContextCompat.getColor(this, R.color.text_hint));
        }
    }

    private void startCountdown() {
        stopCountdown();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateCountdown();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);
    }

    private void stopCountdown() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    private void updateCountdown() {
        String today = TimeUtils.getToday();
        String tomorrow = TimeUtils.getTomorrow();
        DailyPlan plan = planDao.getByDate(today);

        long earliestTime = Long.MAX_VALUE;

        if (plan != null && plan.isEnabled()) {
            long morningDiff = TimeUtils.getTimeDiffToNow(today, plan.getMorningTime());
            if (morningDiff > 0 && morningDiff < earliestTime) {
                earliestTime = morningDiff;
            }

            long eveningDiff = TimeUtils.getTimeDiffToNow(today, plan.getEveningTime());
            if (eveningDiff > 0 && eveningDiff < earliestTime) {
                earliestTime = eveningDiff;
            }
        }

        DailyPlan tomorrowPlan = planDao.getByDate(tomorrow);
        if (tomorrowPlan != null && tomorrowPlan.isEnabled()) {
            long morningDiff = TimeUtils.getTimeDiffToNow(tomorrow, tomorrowPlan.getMorningTime());
            if (morningDiff > 0 && morningDiff < earliestTime) {
                earliestTime = morningDiff;
            }
        }

        if (earliestTime == Long.MAX_VALUE) {
            tvCountdown.setText("00:00:00");
            tvNextClockInfo.setText("暂无打卡计划");
        } else {
            tvCountdown.setText(TimeUtils.formatCountdown(earliestTime));

            String nextTime = "";
            DailyPlan nextPlan = planDao.getByDate(today);
            if (nextPlan != null && nextPlan.isEnabled()) {
                long morningDiff = TimeUtils.getTimeDiffToNow(today, nextPlan.getMorningTime());
                long eveningDiff = TimeUtils.getTimeDiffToNow(today, nextPlan.getEveningTime());

                if (morningDiff > 0 && morningDiff <= earliestTime) {
                    nextTime = "早班 " + nextPlan.getMorningTime();
                } else if (eveningDiff > 0 && eveningDiff <= earliestTime) {
                    nextTime = "晚班 " + nextPlan.getEveningTime();
                } else if (tomorrowPlan != null) {
                    nextTime = "明早 " + tomorrowPlan.getMorningTime();
                }
            } else if (tomorrowPlan != null) {
                nextTime = "明早 " + tomorrowPlan.getMorningTime();
            }

            tvNextClockInfo.setText("下次打卡：" + nextTime);
        }

        updateTodayStatus(today);
    }

    private void openDingTalk() {
        // 钉钉可能的包名列表
        String[] dingtalkPackages = {
            "com.alibaba.android.rimet",
            "com.dingtalk",
            "com.alibaba.android.rimet.biz",
        };

        for (String packageName : dingtalkPackages) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        }

        // 尝试Uri方式
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("dingtalk://"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.dingding_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBatteryOptimization() {
        if (!prefsManager.isAutoEnabled()) return;

        String manufacturer = Build.MANUFACTURER.toLowerCase();
        boolean isHuawei = manufacturer.contains("huawei") || manufacturer.contains("honor");

        if (isHuawei) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.battery_optimization_title)
                    .setMessage(R.string.battery_optimization_message)
                    .setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new android.content.ComponentName(
                                    "com.huawei.systemmanager",
                                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                            ));
                            startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.later, null)
                    .show();
        }
    }

    private void startClockService() {
        try {
            Intent intent = new Intent(this, ClockService.class);
            intent.setAction(ClockService.ACTION_START);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "启动服务失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopClockService() {
        Intent intent = new Intent(this, ClockService.class);
        intent.setAction(ClockService.ACTION_STOP);
        startService(intent);
    }
}
