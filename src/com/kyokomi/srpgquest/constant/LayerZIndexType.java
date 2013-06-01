package com.kyokomi.srpgquest.constant;

public enum LayerZIndexType {
	TALK_LAYER(90),
	CUTIN_LAYER(80),
	TEXT_LAYER(70),
	EFFETCT_LAYER(60),
	POPUP_LAYER(50),
	ACTOR_LAYER(40),
	SELECTCURSOR_LAYER(30),
	ATTACKCURSOR_LAYER(20),
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
