package com.kyokomi.core.dto;

public class ActorPlayerDto {

	/** プレイヤーを一意に識別するID. */
	private Integer playerId;
	
	/** キャライメージのID. */
	private int imageResId;
	
	/** キャラ名. */
	private String name;
	
	/** 攻撃力. */
	private int attackPoint;
	/** 防御力. */
	private int defencePoint;
	/** HP. */
	private int hitPoint;
	/** MP. */
	private int magicPoint;
	
	/** 移動力(ステータス表示用). */
	private int movePoint;
	/** 攻撃範囲(ステータス表示用). */
	private int attackRange;
	
	/**
	 * @return the playerId
	 */
	public Integer getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}
	/**
	 * @return the imageResId
	 */
	public int getImageResId() {
		return imageResId;
	}
	/**
	 * @param imageResId the imageResId to set
	 */
	public void setImageResId(int imageResId) {
		this.imageResId = imageResId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the attackPoint
	 */
	public int getAttackPoint() {
		return attackPoint;
	}
	/**
	 * @param attackPoint the attackPoint to set
	 */
	public void setAttackPoint(int attackPoint) {
		this.attackPoint = attackPoint;
	}
	/**
	 * @return the defencePoint
	 */
	public int getDefencePoint() {
		return defencePoint;
	}
	/**
	 * @param defencePoint the defencePoint to set
	 */
	public void setDefencePoint(int defencePoint) {
		this.defencePoint = defencePoint;
	}
	/**
	 * @return the hitPoint
	 */
	public int getHitPoint() {
		return hitPoint;
	}
	/**
	 * @param hitPoint the hitPoint to set
	 */
	public void setHitPoint(int hitPoint) {
		this.hitPoint = hitPoint;
	}
	/**
	 * @return the magicPoint
	 */
	public int getMagicPoint() {
		return magicPoint;
	}
	/**
	 * @param magicPoint the magicPoint to set
	 */
	public void setMagicPoint(int magicPoint) {
		this.magicPoint = magicPoint;
	}
	/**
	 * @return the movePoint
	 */
	public int getMovePoint() {
		return movePoint;
	}
	/**
	 * @param movePoint the movePoint to set
	 */
	public void setMovePoint(int movePoint) {
		this.movePoint = movePoint;
	}
	/**
	 * @return the attackRange
	 */
	public int getAttackRange() {
		return attackRange;
	}
	/**
	 * @param attackRange the attackRange to set
	 */
	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}
}
