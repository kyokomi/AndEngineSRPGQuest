package com.kyokomi.srpgquest.constant;

public enum CommonTag {
	
	TALK_LAYER_TAG(999),
	
	;
	private Integer value;

	private CommonTag(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static CommonTag get(Integer value) {
		CommonTag[] values = values();
		for (CommonTag type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}