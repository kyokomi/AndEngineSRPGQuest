package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

public interface IDatabaseEntity {
	
	public void initCursor(Cursor pCursor);
	
	public ContentValues createContentValues();
}
