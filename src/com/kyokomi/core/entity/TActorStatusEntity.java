package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 
 * 	_ID INTEGER NOT NULL,
 * LEVEL INTEGER,
 * EXP INTEGER,
 * ATTACK_POINT INTEGER,
 * DEFENCE_POINT INTEGER,
 * HIT_POINT INTEGER,
 * MAGIC_POINT INTEGER,
 * MOVE_POINT INTEGER,
 * ATTACK_RANGE INTEGER,
 * 
 * @author kyokomi
 *
 */
public class TActorStatusEntity implements IDatabaseEntity {

	private Integer actorId;
	private Integer level;
	private Integer exp;
	private Integer nextExp;
	private Integer attackPoint;
	private Integer defencePoint;
	private Integer hitPoint;
	private Integer magicPoint;
	private Integer movePoint;
	private Integer attackRange;
	private Integer weaponId;
	private Integer accessoryId;
	
	public TActorStatusEntity() {
		
	}
	public TActorStatusEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.actorId        = pCursor.getInt(count); count++;
		this.level          = pCursor.getInt(count); count++;
		this.exp            = pCursor.getInt(count); count++;
		this.nextExp        = pCursor.getInt(count); count++;
		this.attackPoint    = pCursor.getInt(count); count++;
		this.defencePoint   = pCursor.getInt(count); count++;
		this.hitPoint       = pCursor.getInt(count); count++;
		this.magicPoint     = pCursor.getInt(count); count++;
		this.movePoint      = pCursor.getInt(count); count++;
		this.attackRange    = pCursor.getInt(count); count++;
		this.weaponId       = pCursor.getInt(count); count++;
		this.accessoryId    = pCursor.getInt(count); count++;
	}
	public Integer getActorId() {
		return actorId;
	}
	public void setActorId(Integer actorId) {
		this.actorId = actorId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getExp() {
		return exp;
	}
	public Integer getNextExp() {
		return nextExp;
	}
	public void setNextExp(Integer nextExp) {
		this.nextExp = nextExp;
	}
	public void setExp(Integer exp) {
		this.exp = exp;
	}
	public Integer getAttackPoint() {
		return attackPoint;
	}
	public void setAttackPoint(Integer attackPoint) {
		this.attackPoint = attackPoint;
	}
	public Integer getDefencePoint() {
		return defencePoint;
	}
	public void setDefencePoint(Integer defencePoint) {
		this.defencePoint = defencePoint;
	}
	public Integer getHitPoint() {
		return hitPoint;
	}
	public void setHitPoint(Integer hitPoint) {
		this.hitPoint = hitPoint;
	}
	public Integer getMagicPoint() {
		return magicPoint;
	}
	public void setMagicPoint(Integer magicPoint) {
		this.magicPoint = magicPoint;
	}
	public Integer getMovePoint() {
		return movePoint;
	}
	public void setMovePoint(Integer movePoint) {
		this.movePoint = movePoint;
	}
	public Integer getAttackRange() {
		return attackRange;
	}
	public void setAttackRange(Integer attackRange) {
		this.attackRange = attackRange;
	}
	public Integer getWeaponId() {
		return weaponId;
	}
	public void setWeaponId(Integer weaponId) {
		this.weaponId = weaponId;
	}
	public Integer getAccessoryId() {
		return accessoryId;
	}
	public void setAccessoryId(Integer accessoryId) {
		this.accessoryId = accessoryId;
	}
	@Override
	public ContentValues createContentValues() {
		// TODO: あとで
//		ContentValues values = new ContentValues();
//		values.put("SAVE_ID", saveId);
//		values.put("SCENARIO_NO", scenariNo);
//		values.put("SEQ_NO", seqNo);
//		return values;
		return null;
	}
	
	@Override
	public int getId() {
		return getActorId();
	}
}
