package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 	_ID INTEGER NOT NULL,
	OBJECT_TYPE INTEGER,
	OBJECT_ID INTEGER,
	MAP_BATTLE_ID INTEGER NOT NULL,
 */
public class MMapBattleRewardEntity implements IDatabaseEntity {

	private Integer rewardId;
	private Integer mapBattleId;
	private Integer objectType;
	private Integer objectId;
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.rewardId = pCursor.getInt(count); count++;
		this.mapBattleId = pCursor.getInt(count); count++;
		this.objectType = pCursor.getInt(count); count++;
		this.objectId = pCursor.getInt(count); count++;
	}

	@Override
	public ContentValues createContentValues() {
		return null;
	}

	@Override
	public int getId() {
		return this.rewardId;
	}
	
	public Integer getRewardId() {
		return rewardId;
	}
	public void setRewardId(Integer rewardId) {
		this.rewardId = rewardId;
	}
	public Integer getObjectType() {
		return objectType;
	}
	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}
	public Integer getObjectId() {
		return objectId;
	}
	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}
	public Integer getMapBattleId() {
		return mapBattleId;
	}
	public void setMapBattleId(Integer mapBattleId) {
		this.mapBattleId = mapBattleId;
	}
}
