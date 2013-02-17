package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 
 * _ID INTEGER NOT NULL,
 * 	NAME TEXT,
 * IMG_RES_ID INTEGER,
 * 
 * @author kyokomi
 *
 */
public class MActorEntity implements IDatabaseEntity {

	private Integer actorId;
	
	private String actorName;
	
	private Integer imageResId;
	
	public MActorEntity() {
		
	}
	public MActorEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	
	@Override
	public void initCursor(Cursor pCursor) {
		this.actorId      = pCursor.getInt(0);
		this.actorName    = pCursor.getString(1);
		this.imageResId   = pCursor.getInt(2);
	}
	public Integer getActorId() {
		return actorId;
	}
	public void setActorId(Integer actorId) {
		this.actorId = actorId;
	}
	public String getActorName() {
		return actorName;
	}
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	public Integer getImageResId() {
		return imageResId;
	}
	public void setImageResId(Integer imageResId) {
		this.imageResId = imageResId;
	}
	
	@Override
	public ContentValues createContentValues() {
		// マスターはupdateもinsertもしないから不要
		return null;
	}
}
