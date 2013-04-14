package com.kyokomi.core.constants;

public enum SceneType {
	SCENE_TYPE_NOVEL(1),
	SCENE_TYPE_MAP(2),
	SCENE_TYPE_RESULT(3),
	SCENE_TYPE_FREE(4),
	SCENE_TYPE_END(99)
	;
	
	private Integer value;
	
	private SceneType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	public static SceneType get(Integer value) {
		SceneType[] values = values();
		for (SceneType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}
