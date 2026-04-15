package com.dingding.daka;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dingding.daka.data.AppDatabase;
import com.dingding.daka.data.ClockRecord;
import com.dingding.daka.data.RecordDao;
import com.dingding.daka.util.TimeUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnPrevMonth;
    private ImageButton btnNextMonth;
    private TextView tvMonth;
    private RecyclerView recyclerView;
    private LinearLayout emptyView;

    private HistoryAdapter adapter;
    private RecordDao recordDao;

    private int currentYear;
    private int currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        initDatabase();
        setupListeners();
        initCurrentMonth();
        loadRecords();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvMonth = findViewById(R.id.tvMonth);
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);

        adapter = new HistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initDatabase() {
        AppDatabase db = AppDatabase.getInstance(this);
        recordDao = db.recordDao();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateMonthTitle();
            loadRecords();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateMonthTitle();
            loadRecords();
        });
    }

    private void initCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH) + 1;
        updateMonthTitle();
    }

    private void updateMonthTitle() {
        tvMonth.setText(String.format(Locale.CHINA, "%d年%d月", currentYear, currentMonth));
    }

    private void loadRecords() {
        int daysInMonth = TimeUtils.getDaysInMonth(currentYear, currentMonth);
        String startDate = TimeUtils.formatDate(currentYear, currentMonth, 1);
        String endDate = TimeUtils.formatDate(currentYear, currentMonth, daysInMonth);

        List<ClockRecord> records = recordDao.getByDateRange(startDate, endDate);

        adapter.setRecords(records);

        if (records.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
