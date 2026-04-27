package com.artem.fitathlete.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artem.fitathlete.data.model.MealEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MealDao_Impl implements MealDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MealEntity> __insertionAdapterOfMealEntity;

  private final EntityDeletionOrUpdateAdapter<MealEntity> __deletionAdapterOfMealEntity;

  private final EntityDeletionOrUpdateAdapter<MealEntity> __updateAdapterOfMealEntity;

  private final SharedSQLiteStatement __preparedStmtOfToggleFavorite;

  public MealDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMealEntity = new EntityInsertionAdapter<MealEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `meals` (`id`,`dateTimeMillis`,`dish`,`portionGrams`,`kcalPer100`,`proteinPer100`,`fatPer100`,`carbsPer100`,`totalCalories`,`totalProtein`,`totalFat`,`totalCarbs`,`isFavorite`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateTimeMillis());
        statement.bindString(3, entity.getDish());
        statement.bindDouble(4, entity.getPortionGrams());
        statement.bindDouble(5, entity.getKcalPer100());
        statement.bindDouble(6, entity.getProteinPer100());
        statement.bindDouble(7, entity.getFatPer100());
        statement.bindDouble(8, entity.getCarbsPer100());
        statement.bindDouble(9, entity.getTotalCalories());
        statement.bindDouble(10, entity.getTotalProtein());
        statement.bindDouble(11, entity.getTotalFat());
        statement.bindDouble(12, entity.getTotalCarbs());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(13, _tmp);
        statement.bindString(14, entity.getNote());
      }
    };
    this.__deletionAdapterOfMealEntity = new EntityDeletionOrUpdateAdapter<MealEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `meals` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMealEntity = new EntityDeletionOrUpdateAdapter<MealEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `meals` SET `id` = ?,`dateTimeMillis` = ?,`dish` = ?,`portionGrams` = ?,`kcalPer100` = ?,`proteinPer100` = ?,`fatPer100` = ?,`carbsPer100` = ?,`totalCalories` = ?,`totalProtein` = ?,`totalFat` = ?,`totalCarbs` = ?,`isFavorite` = ?,`note` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateTimeMillis());
        statement.bindString(3, entity.getDish());
        statement.bindDouble(4, entity.getPortionGrams());
        statement.bindDouble(5, entity.getKcalPer100());
        statement.bindDouble(6, entity.getProteinPer100());
        statement.bindDouble(7, entity.getFatPer100());
        statement.bindDouble(8, entity.getCarbsPer100());
        statement.bindDouble(9, entity.getTotalCalories());
        statement.bindDouble(10, entity.getTotalProtein());
        statement.bindDouble(11, entity.getTotalFat());
        statement.bindDouble(12, entity.getTotalCarbs());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(13, _tmp);
        statement.bindString(14, entity.getNote());
        statement.bindLong(15, entity.getId());
      }
    };
    this.__preparedStmtOfToggleFavorite = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE meals SET isFavorite = NOT isFavorite WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MealEntity meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMealEntity.insert(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MealEntity meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMealEntity.handle(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MealEntity meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMealEntity.handle(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object toggleFavorite(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfToggleFavorite.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfToggleFavorite.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MealEntity>> observeAll() {
    final String _sql = "SELECT * FROM meals ORDER BY dateTimeMillis DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimeMillis");
          final int _cursorIndexOfDish = CursorUtil.getColumnIndexOrThrow(_cursor, "dish");
          final int _cursorIndexOfPortionGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "portionGrams");
          final int _cursorIndexOfKcalPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "kcalPer100");
          final int _cursorIndexOfProteinPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPer100");
          final int _cursorIndexOfFatPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPer100");
          final int _cursorIndexOfCarbsPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPer100");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProtein");
          final int _cursorIndexOfTotalFat = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFat");
          final int _cursorIndexOfTotalCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbs");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateTimeMillis;
            _tmpDateTimeMillis = _cursor.getLong(_cursorIndexOfDateTimeMillis);
            final String _tmpDish;
            _tmpDish = _cursor.getString(_cursorIndexOfDish);
            final double _tmpPortionGrams;
            _tmpPortionGrams = _cursor.getDouble(_cursorIndexOfPortionGrams);
            final double _tmpKcalPer100;
            _tmpKcalPer100 = _cursor.getDouble(_cursorIndexOfKcalPer100);
            final double _tmpProteinPer100;
            _tmpProteinPer100 = _cursor.getDouble(_cursorIndexOfProteinPer100);
            final double _tmpFatPer100;
            _tmpFatPer100 = _cursor.getDouble(_cursorIndexOfFatPer100);
            final double _tmpCarbsPer100;
            _tmpCarbsPer100 = _cursor.getDouble(_cursorIndexOfCarbsPer100);
            final double _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getDouble(_cursorIndexOfTotalCalories);
            final double _tmpTotalProtein;
            _tmpTotalProtein = _cursor.getDouble(_cursorIndexOfTotalProtein);
            final double _tmpTotalFat;
            _tmpTotalFat = _cursor.getDouble(_cursorIndexOfTotalFat);
            final double _tmpTotalCarbs;
            _tmpTotalCarbs = _cursor.getDouble(_cursorIndexOfTotalCarbs);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new MealEntity(_tmpId,_tmpDateTimeMillis,_tmpDish,_tmpPortionGrams,_tmpKcalPer100,_tmpProteinPer100,_tmpFatPer100,_tmpCarbsPer100,_tmpTotalCalories,_tmpTotalProtein,_tmpTotalFat,_tmpTotalCarbs,_tmpIsFavorite,_tmpNote);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MealEntity>> observeFavorites() {
    final String _sql = "SELECT * FROM meals WHERE isFavorite = 1 ORDER BY dateTimeMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meals"}, new Callable<List<MealEntity>>() {
      @Override
      @NonNull
      public List<MealEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimeMillis");
          final int _cursorIndexOfDish = CursorUtil.getColumnIndexOrThrow(_cursor, "dish");
          final int _cursorIndexOfPortionGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "portionGrams");
          final int _cursorIndexOfKcalPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "kcalPer100");
          final int _cursorIndexOfProteinPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPer100");
          final int _cursorIndexOfFatPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPer100");
          final int _cursorIndexOfCarbsPer100 = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPer100");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProtein");
          final int _cursorIndexOfTotalFat = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFat");
          final int _cursorIndexOfTotalCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbs");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<MealEntity> _result = new ArrayList<MealEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateTimeMillis;
            _tmpDateTimeMillis = _cursor.getLong(_cursorIndexOfDateTimeMillis);
            final String _tmpDish;
            _tmpDish = _cursor.getString(_cursorIndexOfDish);
            final double _tmpPortionGrams;
            _tmpPortionGrams = _cursor.getDouble(_cursorIndexOfPortionGrams);
            final double _tmpKcalPer100;
            _tmpKcalPer100 = _cursor.getDouble(_cursorIndexOfKcalPer100);
            final double _tmpProteinPer100;
            _tmpProteinPer100 = _cursor.getDouble(_cursorIndexOfProteinPer100);
            final double _tmpFatPer100;
            _tmpFatPer100 = _cursor.getDouble(_cursorIndexOfFatPer100);
            final double _tmpCarbsPer100;
            _tmpCarbsPer100 = _cursor.getDouble(_cursorIndexOfCarbsPer100);
            final double _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getDouble(_cursorIndexOfTotalCalories);
            final double _tmpTotalProtein;
            _tmpTotalProtein = _cursor.getDouble(_cursorIndexOfTotalProtein);
            final double _tmpTotalFat;
            _tmpTotalFat = _cursor.getDouble(_cursorIndexOfTotalFat);
            final double _tmpTotalCarbs;
            _tmpTotalCarbs = _cursor.getDouble(_cursorIndexOfTotalCarbs);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new MealEntity(_tmpId,_tmpDateTimeMillis,_tmpDish,_tmpPortionGrams,_tmpKcalPer100,_tmpProteinPer100,_tmpFatPer100,_tmpCarbsPer100,_tmpTotalCalories,_tmpTotalProtein,_tmpTotalFat,_tmpTotalCarbs,_tmpIsFavorite,_tmpNote);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object dailyCalories(final long start, final long end,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT COALESCE(SUM(totalCalories), 0) FROM meals WHERE dateTimeMillis >= ? AND dateTimeMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp;
            _tmp = _cursor.getDouble(0);
            _result = _tmp;
          } else {
            _result = 0.0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
