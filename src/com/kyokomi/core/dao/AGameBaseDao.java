package com.kyokomi.core.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kyokomi.core.entity.IDatabaseEntity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class AGameBaseDao<T extends IDatabaseEntity> {

	protected abstract String getDatabaseTableName();
	
	protected abstract Class<T> getDtoClass();
	
	
	public List<T> selectAll(SQLiteDatabase pSqLiteDatabase) {
		return query(pSqLiteDatabase, null, null, null, null, null, null, null);
	}
	
	protected List<T> query(SQLiteDatabase pSqLiteDatabase,
		String[] columns, 
		String selection, String[] selectionArgs, 
		String groupBy, String having, String orderBy, String limit) {
		
		Cursor cursor = pSqLiteDatabase.query(
				getDatabaseTableName(), columns, selection, selectionArgs, groupBy, having, orderBy);
		List<T> dtos = new ArrayList<T>();
		while (cursor.moveToNext()) {
			try {
				T instance = getDtoClass().newInstance();
				instance.initCursor(cursor);
				dtos.add(instance);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		
		return dtos;
	}
	public T selectById(SQLiteDatabase pSqLiteDatabase, int id) {
		String where = "_id = ?";
		String[] whereArgs = {String.valueOf(id)};
		String limit = "1";
		Iterator<T> it = query(pSqLiteDatabase, null, where, whereArgs, null, null, null, limit).iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
	
	public long insert(SQLiteDatabase pSqLiteDatabase, T entity) {
		return pSqLiteDatabase.insert(getDatabaseTableName(), null, entity.createContentValues());		
	}

	public long update(SQLiteDatabase pSqLiteDatabase, T entity) {
		String where = "_id = ?";
		String[] whereArgs = {String.valueOf(entity.getId())};
		return pSqLiteDatabase.update(getDatabaseTableName(), entity.createContentValues(), where, whereArgs);
	}
}
