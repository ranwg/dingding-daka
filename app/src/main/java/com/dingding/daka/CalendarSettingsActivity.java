package com.dingding.daka;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dingding.daka.data.AppDatabase;
import com.dingding.daka.data.DailyPlan;
import com.dingding.daka.data.PlanDao;
import com.dingding.daka.data.PreferencesManager;
import com.dingding.daka.receiver.AlarmScheduler;
import com.dingding.daka.util.TimeUtils;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarSettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnPrevMonth;
    private ImageButton btnNextMonth;
    private TextView tvMonth;
    private GridLayout gridCalendar;
    private MaterialButton btnRandomGenerate;
    private MaterialButton btnSave;
    private TextView tvTip;
    private TextView tvMorningStart, tvMorningEnd, tvEveningStart, tvEveningEnd;

    private AppDatabase db;
    private PlanDao planDao;
    private PreferencesManager prefsManager;

    private int currentYear;
    private int currentMonth;

    // 当月所有日期的计划数据
    private List<DailyPlan> currentPlans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_settings);

        initViews();
        initDatabase();
        setupListeners();
        initCurrentMonth();
        loadCurrentMonthPlans();
        buildCalendarGrid();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvMonth = findViewById(R.id.tvMonth);
        gridCalendar = findViewById(R.id.gridCalendar);
        btnRandomGenerate = findViewById(R.id.btnRandomGenerate);
        btnSave = findViewById(R.id.btnSave);
        tvTip = findViewById(R.id.tvTip);
        tvMorningStart = findViewById(R.id.tvMorningStart);
        tvMorningEnd = findViewById(R.id.tvMorningEnd);
        tvEveningStart = findViewById(R.id.tvEveningStart);
        tvEveningEnd = findViewById(R.id.tvEveningEnd);
    }

    private void initDatabase() {
        db = AppDatabase.getInstance(this);
        planDao = db.planDao();
        prefsManager = PreferencesManager.getInstance(this);
    }

    private void loadTimeRangeSettings() {
        tvMorningStart.setText(prefsManager.getMorningStart());
        tvMorningEnd.setText(prefsManager.getMorningEnd());
        tvEveningStart.setText(prefsManager.getEveningStart());
        tvEveningEnd.setText(prefsManager.getEveningEnd());
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            loadCurrentMonthPlans();
            buildCalendarGrid();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            loadCurrentMonthPlans();
            buildCalendarGrid();
        });

        btnRandomGenerate.setOnClickListener(v -> randomGenerateAll());
        btnSave.setOnClickListener(v -> saveAndFinish());

        // 时间范围点击
        tvMorningStart.setOnClickListener(v -> showTimePickerDialog(true, true));
        tvMorningEnd.setOnClickListener(v -> showTimePickerDialog(true, false));
        tvEveningStart.setOnClickListener(v -> showTimePickerDialog(false, true));
        tvEveningEnd.setOnClickListener(v -> showTimePickerDialog(false, false));
    }

    private void showTimePickerDialog(boolean isMorning, boolean isStart) {
        String currentTime = isMorning ? (isStart ? tvMorningStart.getText().toString() : tvMorningEnd.getText().toString())
                : (isStart ? tvEveningStart.getText().toString() : tvEveningEnd.getText().toString());

        int[] timeParts = TimeUtils.parseTimeToHourMinute(currentTime);
        int hour = timeParts[0];
        int minute = timeParts[1];

        new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute1);
            if (isMorning) {
                if (isStart) {
                    tvMorningStart.setText(time);
                    prefsManager.setMorningStart(time);
                } else {
                    tvMorningEnd.setText(time);
                    prefsManager.setMorningEnd(time);
                }
            } else {
                if (isStart) {
                    tvEveningStart.setText(time);
                    prefsManager.setEveningStart(time);
                } else {
                    tvEveningEnd.setText(time);
                    prefsManager.setEveningEnd(time);
                }
            }
            updateRandomGenerateWithNewRange();
        }, hour, minute, true).show();
    }

    private void updateRandomGenerateWithNewRange() {
        // 通知TimeUtils使用新的时间范围
        TimeUtils.updateTimeRange(
                prefsManager.getMorningStart(),
                prefsManager.getMorningEnd(),
                prefsManager.getEveningStart(),
                prefsManager.getEveningEnd()
        );
    }

    private void initCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH) + 1;
    }

    private void updateMonthTitle() {
        tvMonth.setText(String.format(Locale.CHINA, "%d年%d月", currentYear, currentMonth));
    }

    private void loadCurrentMonthPlans() {
        updateMonthTitle();
        currentPlans.clear();

        int daysInMonth = TimeUtils.getDaysInMonth(currentYear, currentMonth);
        String startDate = TimeUtils.formatDate(currentYear, currentMonth, 1);
        String endDate = TimeUtils.formatDate(currentYear, currentMonth, daysInMonth);

        try {
            List<DailyPlan> plans = planDao.getByDateRange(startDate, endDate);
            if (plans != null) {
                for (DailyPlan plan : plans) {
                    currentPlans.add(plan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 检查是否有任何计划
        boolean hasAnyPlan = false;
        for (DailyPlan plan : currentPlans) {
            if (plan != null && plan.isEnabled()) {
                hasAnyPlan = true;
                break;
            }
        }
        tvTip.setVisibility(hasAnyPlan ? View.GONE : View.VISIBLE);
    }

    private void buildCalendarGrid() {
        gridCalendar.removeAllViews();

        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth - 1, 1);

        // 计算第一天是星期几（周一=1，周日=7）
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        firstDayOfWeek = (firstDayOfWeek == Calendar.SUNDAY) ? 7 : firstDayOfWeek - 1;

        int daysInMonth = TimeUtils.getDaysInMonth(currentYear, currentMonth);

        int row = 0;
        int col = firstDayOfWeek - 1;

        for (int day = 1; day <= daysInMonth; day++) {
            String dateStr = TimeUtils.formatDate(currentYear, currentMonth, day);

            // 查找该天的计划
            DailyPlan dayPlan = findPlanForDate(dateStr);

            View dayView = createDayView(day, dateStr, dayPlan, row, col);
            gridCalendar.addView(dayView);

            col++;
            if (col >= 7) {
                col = 0;
                row++;
            }
        }
    }

    private DailyPlan findPlanForDate(String date) {
        for (DailyPlan plan : currentPlans) {
            if (plan != null && date.equals(plan.getDate())) {
                return plan;
            }
        }
        return null;
    }

    private View createDayView(final int day, final String dateStr, DailyPlan plan, int row, int col) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setPadding(4, 8, 4, 8);
        container.setMinimumHeight(dpToPx(64));

        // 判断是否是今天
        boolean isToday = TimeUtils.isToday(dateStr);
        boolean isWeekend = isWeekend(col);

        // 日期
        TextView tvDay = new TextView(this);
        tvDay.setText(String.valueOf(day));
        tvDay.setTextSize(14);
        tvDay.setGravity(Gravity.CENTER);
        tvDay.setPadding(0, 4, 0, 4);

        if (isToday) {
            tvDay.setBackgroundColor(getColor(R.color.primary));
            tvDay.setTextColor(Color.WHITE);
            tvDay.setTypeface(Typeface.DEFAULT_BOLD);
        } else if (isWeekend) {
            tvDay.setTextColor(getColor(R.color.text_hint));
        } else {
            tvDay.setTextColor(getColor(R.color.text_primary));
        }

        container.addView(tvDay);

        // 早班时间
        TextView tvMorning = new TextView(this);
        tvMorning.setTextSize(10);
        tvMorning.setGravity(Gravity.CENTER);
        if (plan != null && plan.getMorningTime() != null && !plan.getMorningTime().isEmpty()) {
            tvMorning.setText(plan.getMorningTime());
            tvMorning.setTextColor(getColor(R.color.morning_color));
        } else {
            tvMorning.setText("");
        }
        container.addView(tvMorning);

        // 晚班时间
        TextView tvEvening = new TextView(this);
        tvEvening.setTextSize(10);
        tvEvening.setGravity(Gravity.CENTER);
        if (plan != null && plan.getEveningTime() != null && !plan.getEveningTime().isEmpty()) {
            tvEvening.setText(plan.getEveningTime());
            tvEvening.setTextColor(getColor(R.color.evening_color));
        } else {
            tvEvening.setText("");
        }
        container.addView(tvEvening);

        // 点击事件
        container.setClickable(true);
        container.setFocusable(true);
        container.setOnClickListener(v -> showEditTimeDialog(day, dateStr, plan));

        // GridLayout参数
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(col, 1f);
        params.rowSpec = GridLayout.spec(row);
        params.setMargins(2, 2, 2, 2);

        container.setLayoutParams(params);
        return container;
    }

    private boolean isWeekend(int col) {
        // col: 0=周一, 1=周二, ..., 5=周六, 6=周日
        int dayOfWeek = col + 1;
        return dayOfWeek == 6 || dayOfWeek == 7;
    }

    private void showEditTimeDialog(int day, String dateStr, DailyPlan existingPlan) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_time, null);

        TextView tvDate = dialogView.findViewById(R.id.tvDate);
        MaterialButton btnMorningTime = dialogView.findViewById(R.id.btnMorningTime);
        MaterialButton btnEveningTime = dialogView.findViewById(R.id.btnEveningTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(TimeUtils.parseDate(dateStr));
        } catch (Exception e) {
            cal.set(currentYear, currentMonth - 1, day);
        }
        tvDate.setText(sdf.format(cal.getTime()));

        String morningTime = "";
        String eveningTime = "";
        if (existingPlan != null) {
            morningTime = existingPlan.getMorningTime();
            eveningTime = existingPlan.getEveningTime();
        }

        btnMorningTime.setText(morningTime.isEmpty() ? "08:30" : morningTime);
        btnEveningTime.setText(eveningTime.isEmpty() ? "18:00" : eveningTime);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    // 保存修改
                    savePlanForDate(dateStr, btnMorningTime.getText().toString(),
                            btnEveningTime.getText().toString());
                    loadCurrentMonthPlans();
                    buildCalendarGrid();
                })
                .setNegativeButton(R.string.cancel, null);

        // 早班时间选择
        btnMorningTime.setOnClickListener(v -> {
            int[] timeParts = TimeUtils.parseTimeToHourMinute(btnMorningTime.getText().toString());
            int hour = timeParts[0];
            int minute = timeParts[1];

            TimePickerDialog timeDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> {
                        String time = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute1);
                        btnMorningTime.setText(time);
                    }, hour, minute, true);
            timeDialog.show();
        });

        // 晚班时间选择
        btnEveningTime.setOnClickListener(v -> {
            int[] timeParts = TimeUtils.parseTimeToHourMinute(btnEveningTime.getText().toString());
            int hour = timeParts[0];
            int minute = timeParts[1];

            TimePickerDialog timeDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> {
                        String time = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute1);
                        btnEveningTime.setText(time);
                    }, hour, minute, true);
            timeDialog.show();
        });

        builder.show();
    }

    private void savePlanForDate(String date, String morningTime, String eveningTime) {
        // 先删除该日期的旧计划（如果存在）
        planDao.deleteByDateRange(date, date);

        // 创建新计划
        DailyPlan plan = new DailyPlan();
        plan.setDate(date);
        plan.setMorningTime(morningTime);
        plan.setEveningTime(eveningTime);
        plan.setEnabled(true);

        // 保存新计划
        planDao.insert(plan);
    }

    private void randomGenerateAll() {
        // 先更新TimeUtils的时间范围
        updateRandomGenerateWithNewRange();

        int daysInMonth = TimeUtils.getDaysInMonth(currentYear, currentMonth);
        String startDate = TimeUtils.formatDate(currentYear, currentMonth, 1);
        String endDate = TimeUtils.formatDate(currentYear, currentMonth, daysInMonth);

        // 先删除当月的旧计划
        planDao.deleteByDateRange(startDate, endDate);

        // 生成新的随机计划
        List<DailyPlan> newPlans = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            String dateStr = TimeUtils.formatDate(currentYear, currentMonth, day);

            DailyPlan plan = new DailyPlan();
            plan.setDate(dateStr);
            plan.setMorningTime(TimeUtils.generateMorningRandomTime());
            plan.setEveningTime(TimeUtils.generateEveningRandomTime());
            plan.setEnabled(true);

            newPlans.add(plan);
        }

        // 批量插入
        planDao.insertAll(newPlans);
        loadCurrentMonthPlans();
        buildCalendarGrid();
        Toast.makeText(this, "已为" + currentMonth + "月所有日期生成随机打卡时间", Toast.LENGTH_SHORT).show();
    }

    private void saveAndFinish() {
        // 调度下一个闹钟
        try {
            AlarmScheduler.scheduleNextAlarm(this);
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
