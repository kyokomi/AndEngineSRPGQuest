package com.kyokomi.core.constants;

public enum ItemType {
	USE_ITEM(1),
	WEAPON(2),
	ACCESSORY(3),
	;
	
	private Integer value;
	
	private ItemType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static ItemType get(Integer value) {
		ItemType[] values = values();
		for (ItemType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}
