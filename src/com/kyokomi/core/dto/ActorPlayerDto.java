package com.kyokomi.core.dto;

import java.util.List;

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
	/** レベル. */
	private int lv;
	/** 経験値. */
	private int exp;
	/** HP. */
	private int hitPoint;
	/** HP最大値. */
	private int hitPointLimit;
	/** MP. */
	private int magicPoint;
	/** MP最大値. */
	private int magicPointLimit;
	
	/** 移動力(ステータス表示用). */
	private int movePoint;
	/** 攻撃範囲(ステータス表示用). */
	private int attackRange;
	
	/** 装備. */
	private ActorPlayerEquipDto equipDto;
	/** スキルリスト. */
	private List<ActorPlayerSkillDto> skillDtoList;
	
	/**
	 * @return the equipDto
	 */
	public ActorPlayerEquipDto getEquipDto() {
		return equipDto;
	}

	/**
	 * @param equipDto the equipDto to set
	 */
	public void setEquipDto(ActorPlayerEquipDto equipDto) {
		this.equipDto = equipDto;
	}

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

	/**
	 * @return the lv
	 */
	public int getLv() {
		return lv;
	}

	/**
	 * @param lv the lv to set
	 */
	public void setLv(int lv) {
		this.lv = lv;
	}

	/**
	 * @return the exp
	 */
	public int getExp() {
		return exp;
	}

	/**
	 * @param exp the exp to set
	 */
	public void setExp(int exp) {
		this.exp = exp;
	}

	/**
	 * @return the hitPointLimit
	 */
	public int getHitPointLimit() {
		return hitPointLimit;
	}

	/**
	 * @param hitPointLimit the hitPointLimit to set
	 */
	public void setHitPointLimit(int hitPointLimit) {
		this.hitPointLimit = hitPointLimit;
	}

	/**
	 * @return the magicPointLimit
	 */
	public int getMagicPointLimit() {
		return magicPointLimit;
	}

	/**
	 * @param magicPointLimit the magicPointLimit to set
	 */
	public void setMagicPointLimit(int magicPointLimit) {
		this.magicPointLimit = magicPointLimit;
	}
	public List<ActorPlayerSkillDto> getSkillDtoList() {
		return skillDtoList;
	}

	public void setSkillDtoList(List<ActorPlayerSkillDto> skillDtoList) {
		this.skillDtoList = skillDtoList;
	}
}
