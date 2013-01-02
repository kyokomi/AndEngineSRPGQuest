package com.kyokomi.srpgquest.constant;

public enum GameStateType {
	
	INIT(0),
	
	PLAYER_TURN(10),
	PLAYER_SELECT(11),
	PLAYER_MOVE(12),
	PLAYER_ATTACK(13),
	
	ENEMY_TURN(20),
	
	ANIMATOR(30)
	;
	
	private Integer value;
	
	private GameStateType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
}
