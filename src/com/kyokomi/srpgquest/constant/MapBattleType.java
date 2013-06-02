package com.kyokomi.srpgquest.constant;

public enum MapBattleType {
	START_PLAYER_TURN(0),
	START_ENEMY_TURN(1),
	;
	private Integer value;

	private MapBattleType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static MapBattleType get(Integer value) {
		MapBattleType[] values = values();
		for (MapBattleType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}