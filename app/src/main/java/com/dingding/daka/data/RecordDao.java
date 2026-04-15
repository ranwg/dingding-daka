package com.dingding.daka.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClockRecord record);

    @Delete
    void delete(ClockRecord record);

    @Query("SELECT * FROM clock_records ORDER BY date DESC, executeTime DESC")
    List<ClockRecord> getAll();

    @Query("SELECT * FROM clock_records WHERE date = :date ORDER BY type ASC")
    List<ClockRecord> getByDate(String date);

    @Query("SELECT * FROM clock_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, type ASC")
    List<ClockRecord> getByDateRange(String startDate, String endDate);

    @Query("SELECT * FROM clock_records WHERE date = :date AND type = :type LIMIT 1")
    ClockRecord getByDateAndType(String date, int type);

    @Query("SELECT COUNT(*) FROM clock_records WHERE date = :date AND type = :type AND status = 'success'")
    int hasExecutedToday(String date, int type);

    @Query("DELETE FROM clock_records WHERE date = :date")
    void deleteByDate(String date);

    @Query("DELETE FROM clock_records")
    void deleteAll();
}
