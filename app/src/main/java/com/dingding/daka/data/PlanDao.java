package com.dingding.daka.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyPlan plan);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DailyPlan> plans);

    @Update
    void update(DailyPlan plan);

    @Delete
    void delete(DailyPlan plan);

    @Query("SELECT * FROM daily_plans WHERE date = :date LIMIT 1")
    DailyPlan getByDate(String date);

    @Query("SELECT * FROM daily_plans WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    List<DailyPlan> getByDateRange(String startDate, String endDate);

    @Query("SELECT * FROM daily_plans WHERE enabled = 1 ORDER BY date ASC")
    List<DailyPlan> getAllEnabled();

    @Query("SELECT * FROM daily_plans ORDER BY date ASC")
    List<DailyPlan> getAll();

    @Query("DELETE FROM daily_plans WHERE date BETWEEN :startDate AND :endDate")
    void deleteByDateRange(String startDate, String endDate);

    @Query("DELETE FROM daily_plans")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM daily_plans WHERE date = :date AND enabled = 1")
    int hasPlanForDate(String date);
}
