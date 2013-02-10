package com.kyokomi.core.dao;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.core.dto.ADatabaseDto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class AGameBaseDao<T extends ADatabaseDto> {

	protected abstract String getDataBaseName();
	
	protected abstract Class<T> getDtoClass();
	
	
	public List<T> selectAll(SQLiteDatabase pSqLiteDatabase) {
		return query(pSqLiteDatabase, null, null, null, null, null, null, null);
	}
	
	protected List<T> query(SQLiteDatabase pSqLiteDatabase,
		String[] columns, 
		String selection, String[] selectionArgs, 
		String groupBy, String having, String orderBy, String limit) {
		
		Cursor cursor = pSqLiteDatabase.query(
				getDataBaseName(), columns, selection, selectionArgs, groupBy, having, orderBy);
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
}
