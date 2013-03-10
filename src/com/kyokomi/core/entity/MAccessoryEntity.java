package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 	
 * _ID INTEGER NOT NULL,
	DEFENSE_POINT INTEGER,
	APPEND_TYPE INTEGER,
	APPEND_VALUE INTEGER,
	
 * @author kyokomi
 *
 */
public class MAccessoryEntity implements IDatabaseEntity {

	private Integer accessoryId;
	private Integer defensePoint;
	private Integer appendType;
	private Integer appendValue;
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.accessoryId         = pCursor.getInt(count); count++;
		this.defensePoint      = pCursor.getInt(count); count++;
		this.appendType       = pCursor.getInt(count); count++;
		this.appendValue      = pCursor.getInt(count); count++;
	}

	@Override
	public ContentValues createContentValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		return accessoryId;
	}
	
	public Integer getAccessoryId() {
		return accessoryId;
	}
	public void setAccessoryId(Integer accessoryId) {
		this.accessoryId = accessoryId;
	}
	public Integer getDefensePoint() {
		return defensePoint;
	}
	public void setDefensePoint(Integer defensePoint) {
		this.defensePoint = defensePoint;
	}
	public Integer getAppendType() {
		return appendType;
	}
	public void setAppendType(Integer appendType) {
		this.appendType = appendType;
	}
	public Integer getAppendValue() {
		return appendValue;
	}
	public void setAppendValue(Integer appendValue) {
		this.appendValue = appendValue;
	}
}
