package com.kyokomi.core.constants;

public enum SkillType {
	NONE(0),
	
	ATTACK_DAMAGE(10),
	ATTACK_COUNT(11),
	
	DEFENCE_DAMAGE(20),
	
	SPPED(30),
	;
	
	private Integer value;
	
	private SkillType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
}
