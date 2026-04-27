package com.artem.fitathlete.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artem.fitathlete.data.db.Converters;
import com.artem.fitathlete.data.model.WorkoutEntity;
import com.artem.fitathlete.data.model.WorkoutType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class WorkoutDao_Impl implements WorkoutDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutEntity> __insertionAdapterOfWorkoutEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<WorkoutEntity> __deletionAdapterOfWorkoutEntity;

  private final EntityDeletionOrUpdateAdapter<WorkoutEntity> __updateAdapterOfWorkoutEntity;

  private final SharedSQLiteStatement __preparedStmtOfToggleFavorite;

  public WorkoutDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutEntity = new EntityInsertionAdapter<WorkoutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workouts` (`id`,`dateMillis`,`exercise`,`type`,`sets`,`reps`,`weightKg`,`cardioDistanceMeters`,`cardioMinutes`,`load`,`isFavorite`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkoutEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateMillis());
        statement.bindString(3, entity.getExercise());
        final String _tmp = __converters.fromWorkoutType(entity.getType());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getSets());
        statement.bindLong(6, entity.getReps());
        statement.bindDouble(7, entity.getWeightKg());
        statement.bindDouble(8, entity.getCardioDistanceMeters());
        statement.bindDouble(9, entity.getCardioMinutes());
        statement.bindDouble(10, entity.getLoad());
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        statement.bindString(12, entity.getNote());
      }
    };
    this.__deletionAdapterOfWorkoutEntity = new EntityDeletionOrUpdateAdapter<WorkoutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `workouts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkoutEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWorkoutEntity = new EntityDeletionOrUpdateAdapter<WorkoutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `workouts` SET `id` = ?,`dateMillis` = ?,`exercise` = ?,`type` = ?,`sets` = ?,`reps` = ?,`weightKg` = ?,`cardioDistanceMeters` = ?,`cardioMinutes` = ?,`load` = ?,`isFavorite` = ?,`note` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkoutEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateMillis());
        statement.bindString(3, entity.getExercise());
        final String _tmp = __converters.fromWorkoutType(entity.getType());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getSets());
        statement.bindLong(6, entity.getReps());
        statement.bindDouble(7, entity.getWeightKg());
        statement.bindDouble(8, entity.getCardioDistanceMeters());
        statement.bindDouble(9, entity.getCardioMinutes());
        statement.bindDouble(10, entity.getLoad());
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        statement.bindString(12, entity.getNote());
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfToggleFavorite = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE workouts SET isFavorite = NOT isFavorite WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final WorkoutEntity workout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutEntity.insert(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final WorkoutEntity workout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkoutEntity.handle(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final WorkoutEntity workout, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkoutEntity.handle(workout);
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
  public Flow<List<WorkoutEntity>> observeAll() {
    final String _sql = "SELECT * FROM workouts ORDER BY dateMillis DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workouts"}, new Callable<List<WorkoutEntity>>() {
      @Override
      @NonNull
      public List<WorkoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfExercise = CursorUtil.getColumnIndexOrThrow(_cursor, "exercise");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfSets = CursorUtil.getColumnIndexOrThrow(_cursor, "sets");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "weightKg");
          final int _cursorIndexOfCardioDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "cardioDistanceMeters");
          final int _cursorIndexOfCardioMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cardioMinutes");
          final int _cursorIndexOfLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "load");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<WorkoutEntity> _result = new ArrayList<WorkoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final String _tmpExercise;
            _tmpExercise = _cursor.getString(_cursorIndexOfExercise);
            final WorkoutType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toWorkoutType(_tmp);
            final int _tmpSets;
            _tmpSets = _cursor.getInt(_cursorIndexOfSets);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeightKg;
            _tmpWeightKg = _cursor.getDouble(_cursorIndexOfWeightKg);
            final double _tmpCardioDistanceMeters;
            _tmpCardioDistanceMeters = _cursor.getDouble(_cursorIndexOfCardioDistanceMeters);
            final double _tmpCardioMinutes;
            _tmpCardioMinutes = _cursor.getDouble(_cursorIndexOfCardioMinutes);
            final double _tmpLoad;
            _tmpLoad = _cursor.getDouble(_cursorIndexOfLoad);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new WorkoutEntity(_tmpId,_tmpDateMillis,_tmpExercise,_tmpType,_tmpSets,_tmpReps,_tmpWeightKg,_tmpCardioDistanceMeters,_tmpCardioMinutes,_tmpLoad,_tmpIsFavorite,_tmpNote);
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
  public Flow<List<WorkoutEntity>> observeFavorites() {
    final String _sql = "SELECT * FROM workouts WHERE isFavorite = 1 ORDER BY dateMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workouts"}, new Callable<List<WorkoutEntity>>() {
      @Override
      @NonNull
      public List<WorkoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfExercise = CursorUtil.getColumnIndexOrThrow(_cursor, "exercise");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfSets = CursorUtil.getColumnIndexOrThrow(_cursor, "sets");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "weightKg");
          final int _cursorIndexOfCardioDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "cardioDistanceMeters");
          final int _cursorIndexOfCardioMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cardioMinutes");
          final int _cursorIndexOfLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "load");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<WorkoutEntity> _result = new ArrayList<WorkoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final String _tmpExercise;
            _tmpExercise = _cursor.getString(_cursorIndexOfExercise);
            final WorkoutType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toWorkoutType(_tmp);
            final int _tmpSets;
            _tmpSets = _cursor.getInt(_cursorIndexOfSets);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeightKg;
            _tmpWeightKg = _cursor.getDouble(_cursorIndexOfWeightKg);
            final double _tmpCardioDistanceMeters;
            _tmpCardioDistanceMeters = _cursor.getDouble(_cursorIndexOfCardioDistanceMeters);
            final double _tmpCardioMinutes;
            _tmpCardioMinutes = _cursor.getDouble(_cursorIndexOfCardioMinutes);
            final double _tmpLoad;
            _tmpLoad = _cursor.getDouble(_cursorIndexOfLoad);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new WorkoutEntity(_tmpId,_tmpDateMillis,_tmpExercise,_tmpType,_tmpSets,_tmpReps,_tmpWeightKg,_tmpCardioDistanceMeters,_tmpCardioMinutes,_tmpLoad,_tmpIsFavorite,_tmpNote);
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
  public Object countByDay(final long dayStart, final long dayEnd,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM workouts WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayStart);
    _argIndex = 2;
    _statement.bindLong(_argIndex, dayEnd);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByDay(final long dayStart, final long dayEnd,
      final Continuation<? super List<WorkoutEntity>> $completion) {
    final String _sql = "SELECT * FROM workouts WHERE dateMillis >= ? AND dateMillis <= ? ORDER BY dateMillis ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayStart);
    _argIndex = 2;
    _statement.bindLong(_argIndex, dayEnd);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WorkoutEntity>>() {
      @Override
      @NonNull
      public List<WorkoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfExercise = CursorUtil.getColumnIndexOrThrow(_cursor, "exercise");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfSets = CursorUtil.getColumnIndexOrThrow(_cursor, "sets");
          final int _cursorIndexOfReps = CursorUtil.getColumnIndexOrThrow(_cursor, "reps");
          final int _cursorIndexOfWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "weightKg");
          final int _cursorIndexOfCardioDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "cardioDistanceMeters");
          final int _cursorIndexOfCardioMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "cardioMinutes");
          final int _cursorIndexOfLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "load");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<WorkoutEntity> _result = new ArrayList<WorkoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final String _tmpExercise;
            _tmpExercise = _cursor.getString(_cursorIndexOfExercise);
            final WorkoutType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toWorkoutType(_tmp);
            final int _tmpSets;
            _tmpSets = _cursor.getInt(_cursorIndexOfSets);
            final int _tmpReps;
            _tmpReps = _cursor.getInt(_cursorIndexOfReps);
            final double _tmpWeightKg;
            _tmpWeightKg = _cursor.getDouble(_cursorIndexOfWeightKg);
            final double _tmpCardioDistanceMeters;
            _tmpCardioDistanceMeters = _cursor.getDouble(_cursorIndexOfCardioDistanceMeters);
            final double _tmpCardioMinutes;
            _tmpCardioMinutes = _cursor.getDouble(_cursorIndexOfCardioMinutes);
            final double _tmpLoad;
            _tmpLoad = _cursor.getDouble(_cursorIndexOfLoad);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new WorkoutEntity(_tmpId,_tmpDateMillis,_tmpExercise,_tmpType,_tmpSets,_tmpReps,_tmpWeightKg,_tmpCardioDistanceMeters,_tmpCardioMinutes,_tmpLoad,_tmpIsFavorite,_tmpNote);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object lastWorkoutMillis(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT MAX(dateMillis) FROM workouts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
