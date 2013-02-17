package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

public class TSaveDataEntity implements IDatabaseEntity {

	private Integer saveId;
	private Integer scenariNo;
	private Integer seqNo;
	
	public TSaveDataEntity() {
		
	}
	public TSaveDataEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	
	@Override
	public void initCursor(Cursor pCursor) {
		this.saveId        = pCursor.getInt(0);
		this.scenariNo     = pCursor.getInt(1);
		this.seqNo         = pCursor.getInt(2);		
	}
	public Integer getSaveId() {
		return saveId;
	}
	public void setSaveId(Integer saveId) {
		this.saveId = saveId;
	}
	public Integer getScenariNo() {
		return scenariNo;
	}
	public void setScenariNo(Integer scenariNo) {
		this.scenariNo = scenariNo;
	}
	public Integer getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}
	@Override
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		values.put("SAVE_ID", saveId);
		values.put("SCENARIO_NO", scenariNo);
		values.put("SEQ_NO", seqNo);
		return values;
	}
}
