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
	
	private Integer faceImgId;
	
	public MActorEntity() {
		
	}
	public MActorEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.actorId      = pCursor.getInt(count); count++;
		this.actorName    = pCursor.getString(count); count++;
		this.imageResId   = pCursor.getInt(count); count++;
		this.faceImgId    = pCursor.getInt(count); count++;
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
	
	public Integer getFaceImgId() {
		return faceImgId;
	}
	public void setFaceImgId(Integer faceImgId) {
		this.faceImgId = faceImgId;
	}
	@Override
	public ContentValues createContentValues() {
		// マスターはupdateもinsertもしないから不要
		return null;
	}
	
	@Override
	public int getId() {
		return getActorId();
	}
}
