package com.kyokomi.srpgquest.constant;

public enum MapDataType {
	NONE(0),
	MOVE_DIST(1),
	ATTACK_DIST(2),
	MAP_ITEM(3),
	PLAYER(4),
	ENEMY(5)
	;
	private Integer value;

	private MapDataType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static MapDataType get(Integer value) {
		MapDataType[] values = values();
		for (MapDataType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}