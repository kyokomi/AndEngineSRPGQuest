package com.kyokomi.srpgquest.constant;

public enum SelectMenuType {

	MENU_ATTACK(1),
	MENU_MOVE(2),
	MENU_WAIT(3),
	MENU_CANCEL(4),
	;
	
	private Integer value;

	private SelectMenuType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static SelectMenuType findTag(Integer tag) {
		SelectMenuType[] values = values();
		for (SelectMenuType type : values) {
			if (type.getValue() == tag) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}
