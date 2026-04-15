# 钉钉自动打卡 APP

一款为华为Mate 20 Pro（鸿蒙3.0.0）优化的自动打卡钉钉Android应用。

## 功能特性

### 核心功能
- **日历化打卡计划**：按月份设置每天的打卡时间
- **每天两次打卡**：
  - 早班：8:30 - 9:00 随机
  - 晚班：18:00 - 18:10 随机
- **随机生成+手动调整**：一键随机生成当月所有打卡时间，也支持单独修改
- **实时倒计时**：主界面显示距离下次打卡的剩余时间
- **打卡记录**：自动记录每次打卡执行情况

### UI设计
- 简洁优雅的Material Design 3界面
- 直观的日历视图
- 原生TimePickerDialog选择时间
- 实时倒计时显示

## 项目结构

```
app/src/main/
├── java/com/dingding/daka/
│   ├── MainActivity.java              # 主界面
│   ├── CalendarSettingsActivity.java  # 日历打卡设置
│   ├── HistoryActivity.java           # 历史记录
│   ├── HistoryAdapter.java            # 记录列表适配器
│   ├── data/
│   │   ├── AppDatabase.java            # Room数据库
│   │   ├── DailyPlan.java             # 每日打卡计划实体
│   │   ├── ClockRecord.java           # 打卡记录实体
│   │   ├── PlanDao.java               # 计划数据访问
│   │   ├── RecordDao.java             # 记录数据访问
│   │   └── PreferencesManager.java    # SharedPreferences管理
│   ├── receiver/
│   │   ├── AlarmReceiver.java         # 闹钟广播接收
│   │   ├── AlarmScheduler.java        # 闹钟调度器
│   │   └── BootReceiver.java         # 开机广播接收
│   └── util/
│       └── TimeUtils.java             # 时间工具类
└── res/
    ├── layout/                        # 布局文件
    ├── values/                        # 资源文件
    └── drawable/                      # 图标资源
```

## 技术栈

- **语言**：Java
- **最低SDK**：API 26 (Android 8.0)
- **目标SDK**：API 33 (Android 13)
- **UI框架**：Material Design 3 + XML布局
- **本地存储**：Room数据库 + SharedPreferences
- **定时任务**：AlarmManager

## 使用说明

### 1. 安装与首次使用

1. 在Android Studio中打开项目
2. 连接华为Mate 20 Pro设备
3. 运行项目安装APP

### 2. 设置打卡计划

1. 打开APP，点击右下角「设置打卡」按钮
2. 进入日历界面，点击「随机生成」一键生成当月所有打卡时间
3. 也可以点击任意日期，单独编辑早班和晚班时间
4. 点击「保存设置」完成

### 3. 开启自动打卡

1. 在主界面打开「自动打卡」开关
2. 系统会在设定的时间自动打开钉钉APP

### 4. 华为鸿蒙系统设置（重要）

为确保定时任务正常执行，请按以下步骤设置：

#### 4.1 关闭电池优化

1. 进入「设置」→「电池」→「应用启动管理」
2. 找到「钉钉打卡」APP
3. 关闭「自动管理」，手动开启以下选项：
   - ✅ 自动启动
   - ✅ 关联启动
   - ✅ 后台活动

#### 4.2 允许后台活动

1. 进入「设置」→「应用」→「钉钉打卡」
2. 设置「电池」→「省电方式」→「无限制」

#### 4.3 开启自启动权限

1. 进入「手机管家」→「启动管理」
2. 找到「钉钉打卡」
3. 开启所有自动管理选项

### 5. 查看打卡记录

1. 在主界面点击「打卡记录」按钮
2. 可以按月份筛选查看历史打卡记录

## 钉钉包名

```
com.alibaba.android.rimet
```

APP通过启动钉钉的Launch Intent来实现自动打卡。

## 权限说明

| 权限 | 用途 |
|------|------|
| RECEIVE_BOOT_COMPLETED | 开机自启，恢复定时任务 |
| SCHEDULE_EXACT_ALARM | 精确闹钟定时 |
| POST_NOTIFICATIONS | 打卡提醒通知 |
| QUERY_ALL_PACKAGES | 查询钉钉应用 |
| REQUEST_IGNORE_BATTERY_OPTIMIZATIONS | 电池优化白名单 |

## 注意事项

1. **手机必须安装钉钉APP**
2. **需要保持APP后台运行**
3. **建议将APP锁定在最近任务中**
4. **如果钉钉被手动关闭，需要重新打开钉钉**

## 故障排除

### Q: 打卡时间到了但没反应？
A: 检查以下设置：
1. APP是否开启了自启动权限
2. 是否被系统省电策略限制
3. 电池优化是否设置为无限制

### Q: 倒计时显示不正确？
A: 确保已经设置了当天的打卡计划

### Q: 打卡记录丢失？
A: 检查数据库是否正常，必要时可以重新生成打卡计划

## 版本信息

- **版本号**：1.0
- **更新日期**：2026-04-10
- **适用系统**：Android 8.0+ / 鸿蒙3.0.0
