package com.kyokomi.srpgquest.constant;

public enum GameStateType {
	
	/** 起動時. */
	INIT(0),
	
	/** プレイヤーターン. */
	PLAYER_TURN(10),
	/** プレイヤーキャラ行動選択中. */
	PLAYER_SELECT(11),
	/** プレイヤーキャラ移動先選択中. */
	PLAYER_MOVE(12),
	/** プレイヤーキャラ攻撃先選択中. */
	PLAYER_ATTACK(13),
	
	/** エネミーターン. */
	ENEMY_TURN(20),
	/** エネミーキャラ行動選択中. */
	ENEMY_SELECT(21),
	/** エネミーキャラ移動先選択中. */
	ENEMY_MOVE(22),
	/** エネミーキャラ攻撃先選択中. */
	ENEMY_ATTACK(23),
	
	/** アニメーション中. */
	ANIMATION(30), // TODO: 未使用
	END(99),
	;
	
	private Integer value;
	
	private GameStateType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
}
