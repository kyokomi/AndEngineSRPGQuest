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
	private Integer attackPoint;
	private Integer defencePoint;
	private Integer hitPoint;
	private Integer magicPoint;
	private Integer movePoint;
	private Integer attackRange;
	
	public TActorStatusEntity() {
		
	}
	public TActorStatusEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	@Override
	public void initCursor(Cursor pCursor) {
		this.actorId        = pCursor.getInt(0);
		this.level          = pCursor.getInt(1);
		this.exp            = pCursor.getInt(2);
		this.attackPoint    = pCursor.getInt(3);
		this.defencePoint   = pCursor.getInt(4);
		this.hitPoint       = pCursor.getInt(5);
		this.magicPoint     = pCursor.getInt(6);
		this.movePoint      = pCursor.getInt(7);
		this.attackRange    = pCursor.getInt(8);
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
}
