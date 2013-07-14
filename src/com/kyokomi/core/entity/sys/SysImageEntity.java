package com.kyokomi.core.entity.sys;

import android.content.ContentValues;
import android.database.Cursor;

import com.kyokomi.core.entity.IDatabaseEntity;

public class SysImageEntity  implements IDatabaseEntity {

	private Integer imageId;
	private String url;
	private byte[] objectBlob;
	private byte[] shortObjectBlob;
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.imageId          = pCursor.getInt(count); count++;
		this.url              = pCursor.getString(count); count++;
		this.objectBlob       = pCursor.getBlob(count); count++;
		this.shortObjectBlob  = pCursor.getBlob(count); count++;
	}

	@Override
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		values.put("_ID", imageId);
		values.put("URL", url);
		values.put("OBJECT_BLOB", objectBlob);
		values.put("SHORT_OBJECT_BLOB", shortObjectBlob);
		return values;
	}

	@Override
	public int getId() {
		return imageId;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getObjectBlob() {
		return objectBlob;
	}

	public void setObjectBlob(byte[] objectBlob) {
		this.objectBlob = objectBlob;
	}

	public byte[] getShortObjectBlob() {
		return shortObjectBlob;
	}

	public void setShortObjectBlob(byte[] shortObjectBlob) {
		this.shortObjectBlob = shortObjectBlob;
	}
}
