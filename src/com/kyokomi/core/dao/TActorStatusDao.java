package com.kyokomi.core.dao;

import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;

import com.kyokomi.core.entity.TActorStatusEntity;

public class TActorStatusDao extends AGameBaseDao<TActorStatusEntity> {

	private static final String TBL_NAME = "T_ACTOR_STATUS";
	
	public TActorStatusDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<TActorStatusEntity> getDtoClass() {
		return TActorStatusEntity.class;
	}

	public TActorStatusEntity selectByActorId(SQLiteDatabase pSqLiteDatabase, int actorId) {
		String where = "_id = ?";
		String[] whereArgs = {String.valueOf(actorId)};
		String limit = "1";
		Iterator<TActorStatusEntity> it = query(pSqLiteDatabase, null, where, whereArgs, null, null, null, limit).iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
}
