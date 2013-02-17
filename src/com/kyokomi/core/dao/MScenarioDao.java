package com.kyokomi.core.dao;

import java.util.Iterator;
import java.util.List;

import com.kyokomi.core.entity.MScenarioEntity;

import android.database.sqlite.SQLiteDatabase;

public class MScenarioDao extends AGameBaseDao<MScenarioEntity>{
	private static final String TBL_NAME = "M_SCENARIO";
	
	public MScenarioDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}
	@Override
	protected Class<MScenarioEntity> getDtoClass() {
		return MScenarioEntity.class;
	}
	
	public List<MScenarioEntity> selectByScenarioNo(SQLiteDatabase pSqLiteDatabase, int pScenarioNo) {
		String where = "scenario_no = ?";
		String[] whereArgs = {String.valueOf(pScenarioNo)};
		return query(pSqLiteDatabase, null, where, whereArgs, null, null, null, null);
	}
	public MScenarioEntity selectByScenarioNoAndSeqNo(SQLiteDatabase pSqLiteDatabase, int pScenarioNo, int pSeqNo) {
		String where = "scenario_no = ? and seq_no = ?";
		String[] whereArgs = {String.valueOf(pScenarioNo), String.valueOf(pSeqNo)};
		String limit = "1";
		Iterator<MScenarioEntity> it = query(pSqLiteDatabase, null, where, whereArgs, null, null, null, limit).iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
	public MScenarioEntity selectNextSeq(SQLiteDatabase pSqLiteDatabase, int pScenarioNo, int pSeqNo) {
		String where = "scenario_no = ? and seq_no > ?";
		String[] whereArgs = {String.valueOf(pScenarioNo), String.valueOf(pSeqNo)};
		String limit = "1";
		Iterator<MScenarioEntity> it = query(pSqLiteDatabase, null, where, whereArgs, null, null, null, limit).iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			// 次のシナリオを探す
			return selectNextScenario(pSqLiteDatabase, pScenarioNo);
		}
	}
	public MScenarioEntity selectNextScenario(SQLiteDatabase pSqLiteDatabase, int pScenarioNo) {
		String where = "scenario_no > ?";
		String[] whereArgs = {String.valueOf(pScenarioNo)};
		String limit = "1";
		Iterator<MScenarioEntity> it = query(pSqLiteDatabase, null, where, whereArgs, null, null, null, limit).iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
}
