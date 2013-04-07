package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
	_ID INTEGER NOT NULL,
	SAVE_ID INTEGER NOT NULL,
	ITEM_ID INTEGER NOT NULL,
	ITEM_COUNT INTEGER,
*/
public class TUserItemEntity implements IDatabaseEntity {

	private Integer userItemId;
	private Integer saveId;
	private Integer itemId;
	private Integer itemCount;
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.userItemId = pCursor.getInt(count); count++;
		this.saveId = pCursor.getInt(count); count++;
		this.itemId = pCursor.getInt(count); count++;
		this.itemCount = pCursor.getInt(count); count++;
	}

	@Override
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		values.put("_ID", this.userItemId);
		values.put("SAVE_ID", this.saveId);
		values.put("ITEM_ID", this.itemId);
		values.put("ITEM_COUNT", this.itemCount);
		return values;
	}

	@Override
	public int getId() {
		return this.userItemId;
	}

	public Integer getUserItemId() {
		return userItemId;
	}

	public void setUserItemId(Integer userItemId) {
		this.userItemId = userItemId;
	}

	public Integer getSaveId() {
		return saveId;
	}

	public void setSaveId(Integer saveId) {
		this.saveId = saveId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}
}
