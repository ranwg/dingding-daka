package com.dingding.daka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dingding.daka.data.ClockRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<ClockRecord> records = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClockRecord record = records.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void setRecords(List<ClockRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View viewTypeIndicator;
        private TextView tvDate;
        private TextView tvType;
        private TextView tvExecuteTime;
        private TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTypeIndicator = itemView.findViewById(R.id.viewTypeIndicator);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvExecuteTime = itemView.findViewById(R.id.tvExecuteTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        void bind(ClockRecord record) {
            tvDate.setText(record.getDate());
            tvExecuteTime.setText("执行时间：" + record.getExecuteTime());

            if (record.getType() == ClockRecord.TYPE_MORNING) {
                tvType.setText("早班");
                viewTypeIndicator.setBackgroundTintList(
                        ContextCompat.getColorStateList(itemView.getContext(), R.color.morning_color));
            } else {
                tvType.setText("晚班");
                viewTypeIndicator.setBackgroundTintList(
                        ContextCompat.getColorStateList(itemView.getContext(), R.color.evening_color));
            }

            if (ClockRecord.STATUS_SUCCESS.equals(record.getStatus())) {
                tvStatus.setText(R.string.clock_success);
                tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
            } else {
                tvStatus.setText(R.string.clock_failed);
                tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.error));
            }
        }
    }
}
