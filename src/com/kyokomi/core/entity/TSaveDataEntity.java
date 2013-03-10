package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

public class TSaveDataEntity implements IDatabaseEntity {

	private Integer saveId;
	private Integer scenariId;
	private Integer partyId;
	private Integer gold;
	private Integer exp;
	
	public TSaveDataEntity() {
		
	}
	public TSaveDataEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	
	@Override
	public void initCursor(Cursor pCursor) {
		this.saveId        = pCursor.getInt(0);
		this.scenariId     = pCursor.getInt(1);
		this.partyId         = pCursor.getInt(2);
		this.gold         = pCursor.getInt(3);
		this.exp         = pCursor.getInt(4);
	}
	public Integer getSaveId() {
		return saveId;
	}
	public void setSaveId(Integer saveId) {
		this.saveId = saveId;
	}
	public Integer getScenariId() {
		return scenariId;
	}
	public void setScenariId(Integer scenariId) {
		this.scenariId = scenariId;
	}
	public Integer getPartyId() {
		return partyId;
	}
	public void setPartyId(Integer partyId) {
		this.partyId = partyId;
	}
	public Integer getGold() {
		return gold;
	}
	public void setGold(Integer gold) {
		this.gold = gold;
	}
	public Integer getExp() {
		return exp;
	}
	public void setExp(Integer exp) {
		this.exp = exp;
	}
	@Override
	public ContentValues createContentValues() {
		ContentValues values = new ContentValues();
		values.put("_ID", saveId);
		values.put("SCENARIO_ID", scenariId);
		values.put("PARTY_ID", partyId);
		values.put("GOLD", gold);
		values.put("EXP", exp);
		return values;
	}
	
	@Override
	public int getId() {
		return getSaveId();
	}
}
