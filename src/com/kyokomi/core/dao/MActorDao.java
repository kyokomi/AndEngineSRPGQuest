package com.kyokomi.core.dao;

import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;

import com.kyokomi.core.entity.MActorEntity;

public class MActorDao extends AGameBaseDao<MActorEntity> {

	private static final String TBL_NAME = "M_ACTOR";
	
	public MActorDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<MActorEntity> getDtoClass() {
		return MActorEntity.class;
	}

	public MActorEntity selectByActorId(SQLiteDatabase pSqLiteDatabase, int actorId) {
		String where = "_id = ?";
		String[] whereArgs = {String.valueOf(actorId)};
		String limit = "1";
		Iterator<MActorEntity> it = query(pSqLiteDatabase, null, where, whereArgs, null, null, null, limit).iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
}
