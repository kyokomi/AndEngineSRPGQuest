package com.kyokomi.srpgquest.constant;

public enum BattleActorType {
	
	PLAYER(1),
	ENEMY(2)
	;
	private Integer value;

	private BattleActorType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static BattleActorType get(Integer value) {
		BattleActorType[] values = values();
		for (BattleActorType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}