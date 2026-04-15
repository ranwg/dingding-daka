package com.dingding.daka.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 管理类
 */
public class PreferencesManager {

    private static final String PREF_NAME = "dingding_daka_prefs";

    // Keys
    private static final String KEY_AUTO_ENABLED = "auto_enabled";
    private static final String KEY_LAST_ALARM_TIME = "last_alarm_time";

    // 随机时间范围
    private static final String KEY_MORNING_START = "morning_start";
    private static final String KEY_MORNING_END = "morning_end";
    private static final String KEY_EVENING_START = "evening_start";
    private static final String KEY_EVENING_END = "evening_end";

    private final SharedPreferences prefs;

    private static volatile PreferencesManager INSTANCE;

    private PreferencesManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PreferencesManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PreferencesManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public boolean isAutoEnabled() {
        return prefs.getBoolean(KEY_AUTO_ENABLED, false);
    }

    public void setAutoEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_ENABLED, enabled).apply();
    }

    public long getLastAlarmTime() {
        return prefs.getLong(KEY_LAST_ALARM_TIME, 0);
    }

    public void setLastAlarmTime(long time) {
        prefs.edit().putLong(KEY_LAST_ALARM_TIME, time).apply();
    }

    // 早班开始时间
    public String getMorningStart() {
        return prefs.getString(KEY_MORNING_START, "08:30");
    }

    public void setMorningStart(String time) {
        prefs.edit().putString(KEY_MORNING_START, time).apply();
    }

    // 早班结束时间
    public String getMorningEnd() {
        return prefs.getString(KEY_MORNING_END, "09:00");
    }

    public void setMorningEnd(String time) {
        prefs.edit().putString(KEY_MORNING_END, time).apply();
    }

    // 晚班开始时间
    public String getEveningStart() {
        return prefs.getString(KEY_EVENING_START, "18:00");
    }

    public void setEveningStart(String time) {
        prefs.edit().putString(KEY_EVENING_START, time).apply();
    }

    // 晚班结束时间
    public String getEveningEnd() {
        return prefs.getString(KEY_EVENING_END, "18:10");
    }

    public void setEveningEnd(String time) {
        prefs.edit().putString(KEY_EVENING_END, time).apply();
    }
}
