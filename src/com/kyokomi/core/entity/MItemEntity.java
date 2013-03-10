package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
_ID INTEGER NOT NULL,
ITEM_NAME TEXT,
ITEM_IMAGE_ID INTEGER,
ITEM_TYPE INTEGER NOT NULL,
ITEM_OBJECT_ID INTEGER,
*/
public class MItemEntity implements IDatabaseEntity {

	private Integer itemId;
	private String itemName;
	private Integer itemImageId;
	private Integer itemType;
	private Integer itemObjectId;
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.itemId = pCursor.getInt(count); count++;
		this.itemName = pCursor.getString(count); count++;
		this.itemImageId = pCursor.getInt(count); count++;
		this.itemType = pCursor.getInt(count); count++;
		this.itemObjectId = pCursor.getInt(count); count++;
	}

	@Override
	public ContentValues createContentValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		return itemId;
	}

	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Integer getItemImageId() {
		return itemImageId;
	}
	public void setItemImageId(Integer itemImageId) {
		this.itemImageId = itemImageId;
	}
	public Integer getItemType() {
		return itemType;
	}
	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}
	public Integer getItemObjectId() {
		return itemObjectId;
	}
	public void setItemObjectId(Integer itemObjectId) {
		this.itemObjectId = itemObjectId;
	}
}
