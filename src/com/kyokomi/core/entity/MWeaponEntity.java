package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 	_ID INTEGER NOT NULL,
	ATTACK_POINT INTEGER,
	HIT_PROBABILITY INTEGER,
	ATTACK_RANGE_TYPE INTEGER,
	ATTACK_RANGE_VALUE INTEGER,
	APPEND_TYPE INTEGER,
	APPEND_VALUE INTEGER,
 * @author kyokomi
 *
 */
public class MWeaponEntity implements IDatabaseEntity {

	private Integer weaponId;
	private Integer attackPoint;
	private Integer hitProbabilty;
	private Integer attackRangeType;
	private Integer attackRangeValue;
	private Integer appendType;
	private Integer appendValue;
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.weaponId         = pCursor.getInt(count); count++;
		this.attackPoint      = pCursor.getInt(count); count++;
		this.hitProbabilty    = pCursor.getInt(count); count++;
		this.attackRangeType  = pCursor.getInt(count); count++;
		this.attackRangeValue = pCursor.getInt(count); count++;
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
		return weaponId;
	}

	public Integer getWeaponId() {
		return weaponId;
	}
	public void setWeaponId(Integer weaponId) {
		this.weaponId = weaponId;
	}
	public Integer getAttackPoint() {
		return attackPoint;
	}
	public void setAttackPoint(Integer attackPoint) {
		this.attackPoint = attackPoint;
	}
	public Integer getHitProbabilty() {
		return hitProbabilty;
	}
	public void setHitProbabilty(Integer hitProbabilty) {
		this.hitProbabilty = hitProbabilty;
	}
	public Integer getAttackRangeType() {
		return attackRangeType;
	}
	public void setAttackRangeType(Integer attackRangeType) {
		this.attackRangeType = attackRangeType;
	}
	public Integer getAttackRangeValue() {
		return attackRangeValue;
	}
	public void setAttackRangeValue(Integer attackRangeValue) {
		this.attackRangeValue = attackRangeValue;
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
