package com.kyokomi.srpgquest.constant;

public enum LayerZIndexType {
	TALK_LAYER(80),
	CUTIN_LAYER(70),
	TEXT_LAYER(60),
	EFFETCT_LAYER(50),
	POPUP_LAYER(40),
	ATTACKCURSOR_LAYER(30),
	ACTOR_LAYER(20),
	MOVECURSOR_LAYER(10),
	BACKGROUND_LAYER(0),
	;
	private Integer value;
	private LayerZIndexType(Integer value) {
		this.value = value;
	}
	public Integer getValue() {
		return value;
	}
}
