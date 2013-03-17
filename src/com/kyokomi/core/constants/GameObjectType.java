package com.kyokomi.core.constants;

public enum GameObjectType {
	EXP(1),
	GOLD(2),
	ITEM(3),
	;
	
	private Integer value;
	
	private GameObjectType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static GameObjectType get(Integer value) {
		GameObjectType[] values = values();
		for (GameObjectType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}
