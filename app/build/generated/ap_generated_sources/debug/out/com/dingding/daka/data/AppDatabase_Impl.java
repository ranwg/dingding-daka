package com.dingding.daka.data;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PlanDao _planDao;

  private volatile RecordDao _recordDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `daily_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT, `morningTime` TEXT, `eveningTime` TEXT, `enabled` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `clock_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT, `executeTime` TEXT, `type` INTEGER NOT NULL, `status` TEXT, `remark` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '844f81a2e462d3bdbbbac6dce07b65cc')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `daily_plans`");
        _db.execSQL("DROP TABLE IF EXISTS `clock_records`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsDailyPlans = new HashMap<String, TableInfo.Column>(5);
        _columnsDailyPlans.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyPlans.put("date", new TableInfo.Column("date", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyPlans.put("morningTime", new TableInfo.Column("morningTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyPlans.put("eveningTime", new TableInfo.Column("eveningTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyPlans.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyPlans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyPlans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyPlans = new TableInfo("daily_plans", _columnsDailyPlans, _foreignKeysDailyPlans, _indicesDailyPlans);
        final TableInfo _existingDailyPlans = TableInfo.read(_db, "daily_plans");
        if (! _infoDailyPlans.equals(_existingDailyPlans)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_plans(com.dingding.daka.data.DailyPlan).\n"
                  + " Expected:\n" + _infoDailyPlans + "\n"
                  + " Found:\n" + _existingDailyPlans);
        }
        final HashMap<String, TableInfo.Column> _columnsClockRecords = new HashMap<String, TableInfo.Column>(6);
        _columnsClockRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClockRecords.put("date", new TableInfo.Column("date", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClockRecords.put("executeTime", new TableInfo.Column("executeTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClockRecords.put("type", new TableInfo.Column("type", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClockRecords.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClockRecords.put("remark", new TableInfo.Column("remark", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysClockRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesClockRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoClockRecords = new TableInfo("clock_records", _columnsClockRecords, _foreignKeysClockRecords, _indicesClockRecords);
        final TableInfo _existingClockRecords = TableInfo.read(_db, "clock_records");
        if (! _infoClockRecords.equals(_existingClockRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "clock_records(com.dingding.daka.data.ClockRecord).\n"
                  + " Expected:\n" + _infoClockRecords + "\n"
                  + " Found:\n" + _existingClockRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "844f81a2e462d3bdbbbac6dce07b65cc", "7ad99c45682fe41db84fcfac6f40641e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "daily_plans","clock_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `daily_plans`");
      _db.execSQL("DELETE FROM `clock_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PlanDao.class, PlanDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecordDao.class, RecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public PlanDao planDao() {
    if (_planDao != null) {
      return _planDao;
    } else {
      synchronized(this) {
        if(_planDao == null) {
          _planDao = new PlanDao_Impl(this);
        }
        return _planDao;
      }
    }
  }

  @Override
  public RecordDao recordDao() {
    if (_recordDao != null) {
      return _recordDao;
    } else {
      synchronized(this) {
        if(_recordDao == null) {
          _recordDao = new RecordDao_Impl(this);
        }
        return _recordDao;
      }
    }
  }
}
