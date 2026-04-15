package com.dingding.daka.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 打卡记录实体
 */
@Entity(tableName = "clock_records")
public class ClockRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // 日期格式：yyyy-MM-dd
    private String date;

    // 执行时间
    private String executeTime;

    // 打卡类型：1=早班, 2=晚班
    private int type;

    // 状态：success=成功, failed=失败
    private String status;

    // 备注
    private String remark;

    public ClockRecord() {
    }

    @Ignore
    public ClockRecord(String date, String executeTime, int type, String status) {
        this.date = date;
        this.executeTime = executeTime;
        this.type = type;
        this.status = status;
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

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // 常量
    public static final int TYPE_MORNING = 1;
    public static final int TYPE_EVENING = 2;
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILED = "failed";
}
