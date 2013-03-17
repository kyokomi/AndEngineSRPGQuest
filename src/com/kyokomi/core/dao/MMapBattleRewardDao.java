package com.kyokomi.core.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.kyokomi.core.entity.MMapBattleRewardEntity;

public class MMapBattleRewardDao extends AGameBaseDao<MMapBattleRewardEntity> {

	private static final String TBL_NAME = "M_MAP_BATTLE_REWARD";
	
	public MMapBattleRewardDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<MMapBattleRewardEntity> getDtoClass() {
		return MMapBattleRewardEntity.class;
	}
	
	public List<MMapBattleRewardEntity> selectByMapBattleId(SQLiteDatabase pSqLiteDatabase, int mapBattleId) {
		String where = "map_battle_id = ?";
		String[] whereArgs = {String.valueOf(mapBattleId)};
		return query(pSqLiteDatabase, where, whereArgs);
	}
}
