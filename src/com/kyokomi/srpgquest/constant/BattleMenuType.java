package com.kyokomi.srpgquest.constant;

public enum BattleMenuType {
	
	ATTACK(1, "攻撃"),
	DEFENCE(2, "防御"),
	SKILL(3, "特技"),
	ITEM(4, "道具")
	;
	private Integer value;
	private String text;

	private BattleMenuType(Integer value, String text) {
		this.value = value;
		this.text = text;
	}
	
	public Integer getValue() {
		return value;
	}
	public String getText() {
		return text;
	}
	public static BattleMenuType get(Integer value) {
		BattleMenuType[] values = values();
		for (BattleMenuType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}