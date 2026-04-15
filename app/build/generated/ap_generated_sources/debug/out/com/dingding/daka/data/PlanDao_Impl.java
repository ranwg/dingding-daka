package com.dingding.daka.data;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlanDao_Impl implements PlanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyPlan> __insertionAdapterOfDailyPlan;

  private final EntityDeletionOrUpdateAdapter<DailyPlan> __deletionAdapterOfDailyPlan;

  private final EntityDeletionOrUpdateAdapter<DailyPlan> __updateAdapterOfDailyPlan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByDateRange;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public PlanDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyPlan = new EntityInsertionAdapter<DailyPlan>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `daily_plans` (`id`,`date`,`morningTime`,`eveningTime`,`enabled`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DailyPlan value) {
        stmt.bindLong(1, value.getId());
        if (value.getDate() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getDate());
        }
        if (value.getMorningTime() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getMorningTime());
        }
        if (value.getEveningTime() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getEveningTime());
        }
        final int _tmp;
        _tmp = value.isEnabled() ? 1 : 0;
        stmt.bindLong(5, _tmp);
      }
    };
    this.__deletionAdapterOfDailyPlan = new EntityDeletionOrUpdateAdapter<DailyPlan>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `daily_plans` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DailyPlan value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfDailyPlan = new EntityDeletionOrUpdateAdapter<DailyPlan>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `daily_plans` SET `id` = ?,`date` = ?,`morningTime` = ?,`eveningTime` = ?,`enabled` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DailyPlan value) {
        stmt.bindLong(1, value.getId());
        if (value.getDate() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getDate());
        }
        if (value.getMorningTime() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getMorningTime());
        }
        if (value.getEveningTime() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getEveningTime());
        }
        final int _tmp;
        _tmp = value.isEnabled() ? 1 : 0;
        stmt.bindLong(5, _tmp);
        stmt.bindLong(6, value.getId());
      }
    };
    this.__preparedStmtOfDeleteByDateRange = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM daily_plans WHERE date BETWEEN ? AND ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM daily_plans";
        return _query;
      }
    };
  }

  @Override
  public void insert(final DailyPlan plan) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDailyPlan.insert(plan);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertAll(final List<DailyPlan> plans) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDailyPlan.insert(plans);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final DailyPlan plan) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfDailyPlan.handle(plan);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final DailyPlan plan) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfDailyPlan.handle(plan);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteByDateRange(final String startDate, final String endDate) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByDateRange.acquire();
    int _argIndex = 1;
    if (startDate == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, startDate);
    }
    _argIndex = 2;
    if (endDate == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, endDate);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteByDateRange.release(_stmt);
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public DailyPlan getByDate(final String date) {
    final String _sql = "SELECT * FROM daily_plans WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfMorningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "morningTime");
      final int _cursorIndexOfEveningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "eveningTime");
      final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
      final DailyPlan _result;
      if(_cursor.moveToFirst()) {
        _result = new DailyPlan();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpDate;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmpDate = null;
        } else {
          _tmpDate = _cursor.getString(_cursorIndexOfDate);
        }
        _result.setDate(_tmpDate);
        final String _tmpMorningTime;
        if (_cursor.isNull(_cursorIndexOfMorningTime)) {
          _tmpMorningTime = null;
        } else {
          _tmpMorningTime = _cursor.getString(_cursorIndexOfMorningTime);
        }
        _result.setMorningTime(_tmpMorningTime);
        final String _tmpEveningTime;
        if (_cursor.isNull(_cursorIndexOfEveningTime)) {
          _tmpEveningTime = null;
        } else {
          _tmpEveningTime = _cursor.getString(_cursorIndexOfEveningTime);
        }
        _result.setEveningTime(_tmpEveningTime);
        final boolean _tmpEnabled;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfEnabled);
        _tmpEnabled = _tmp != 0;
        _result.setEnabled(_tmpEnabled);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DailyPlan> getByDateRange(final String startDate, final String endDate) {
    final String _sql = "SELECT * FROM daily_plans WHERE date BETWEEN ? AND ? ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (startDate == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, startDate);
    }
    _argIndex = 2;
    if (endDate == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, endDate);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfMorningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "morningTime");
      final int _cursorIndexOfEveningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "eveningTime");
      final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
      final List<DailyPlan> _result = new ArrayList<DailyPlan>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DailyPlan _item;
        _item = new DailyPlan();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpDate;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmpDate = null;
        } else {
          _tmpDate = _cursor.getString(_cursorIndexOfDate);
        }
        _item.setDate(_tmpDate);
        final String _tmpMorningTime;
        if (_cursor.isNull(_cursorIndexOfMorningTime)) {
          _tmpMorningTime = null;
        } else {
          _tmpMorningTime = _cursor.getString(_cursorIndexOfMorningTime);
        }
        _item.setMorningTime(_tmpMorningTime);
        final String _tmpEveningTime;
        if (_cursor.isNull(_cursorIndexOfEveningTime)) {
          _tmpEveningTime = null;
        } else {
          _tmpEveningTime = _cursor.getString(_cursorIndexOfEveningTime);
        }
        _item.setEveningTime(_tmpEveningTime);
        final boolean _tmpEnabled;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfEnabled);
        _tmpEnabled = _tmp != 0;
        _item.setEnabled(_tmpEnabled);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DailyPlan> getAllEnabled() {
    final String _sql = "SELECT * FROM daily_plans WHERE enabled = 1 ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfMorningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "morningTime");
      final int _cursorIndexOfEveningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "eveningTime");
      final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
      final List<DailyPlan> _result = new ArrayList<DailyPlan>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DailyPlan _item;
        _item = new DailyPlan();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpDate;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmpDate = null;
        } else {
          _tmpDate = _cursor.getString(_cursorIndexOfDate);
        }
        _item.setDate(_tmpDate);
        final String _tmpMorningTime;
        if (_cursor.isNull(_cursorIndexOfMorningTime)) {
          _tmpMorningTime = null;
        } else {
          _tmpMorningTime = _cursor.getString(_cursorIndexOfMorningTime);
        }
        _item.setMorningTime(_tmpMorningTime);
        final String _tmpEveningTime;
        if (_cursor.isNull(_cursorIndexOfEveningTime)) {
          _tmpEveningTime = null;
        } else {
          _tmpEveningTime = _cursor.getString(_cursorIndexOfEveningTime);
        }
        _item.setEveningTime(_tmpEveningTime);
        final boolean _tmpEnabled;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfEnabled);
        _tmpEnabled = _tmp != 0;
        _item.setEnabled(_tmpEnabled);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DailyPlan> getAll() {
    final String _sql = "SELECT * FROM daily_plans ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfMorningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "morningTime");
      final int _cursorIndexOfEveningTime = CursorUtil.getColumnIndexOrThrow(_cursor, "eveningTime");
      final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
      final List<DailyPlan> _result = new ArrayList<DailyPlan>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DailyPlan _item;
        _item = new DailyPlan();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpDate;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmpDate = null;
        } else {
          _tmpDate = _cursor.getString(_cursorIndexOfDate);
        }
        _item.setDate(_tmpDate);
        final String _tmpMorningTime;
        if (_cursor.isNull(_cursorIndexOfMorningTime)) {
          _tmpMorningTime = null;
        } else {
          _tmpMorningTime = _cursor.getString(_cursorIndexOfMorningTime);
        }
        _item.setMorningTime(_tmpMorningTime);
        final String _tmpEveningTime;
        if (_cursor.isNull(_cursorIndexOfEveningTime)) {
          _tmpEveningTime = null;
        } else {
          _tmpEveningTime = _cursor.getString(_cursorIndexOfEveningTime);
        }
        _item.setEveningTime(_tmpEveningTime);
        final boolean _tmpEnabled;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfEnabled);
        _tmpEnabled = _tmp != 0;
        _item.setEnabled(_tmpEnabled);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int hasPlanForDate(final String date) {
    final String _sql = "SELECT COUNT(*) FROM daily_plans WHERE date = ? AND enabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
