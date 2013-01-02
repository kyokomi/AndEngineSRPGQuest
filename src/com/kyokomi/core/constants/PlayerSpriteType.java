package com.kyokomi.core.constants;

public enum PlayerSpriteType {
	PLAYER_TYPE_HIDE(0),
	PLAYER_TYPE_NORMAL(1),
	PLAYER_TYPE_DEFENSE(2),
	PLAYER_TYPE_ATTACK(3),
	;
	
	private Integer value;
	
	private PlayerSpriteType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
}