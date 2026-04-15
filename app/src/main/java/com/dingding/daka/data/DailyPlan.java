package com.dingding.daka.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 每日打卡计划实体
 */
@Entity(tableName = "daily_plans")
public class DailyPlan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // 日期格式：yyyy-MM-dd
    private String date;

    // 早班打卡时间 HH:mm
    private String morningTime;

    // 晚班打卡时间 HH:mm
    private String eveningTime;

    // 是否启用
    private boolean enabled;

    public DailyPlan() {
    }

    @Ignore
    public DailyPlan(String date, String morningTime, String eveningTime, boolean enabled) {
        this.date = date;
        this.morningTime = morningTime;
        this.eveningTime = eveningTime;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMorningTime() {
        return morningTime;
    }

    public void setMorningTime(String morningTime) {
        this.morningTime = morningTime;
    }

    public String getEveningTime() {
        return eveningTime;
    }

    public void setEveningTime(String eveningTime) {
        this.eveningTime = eveningTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
