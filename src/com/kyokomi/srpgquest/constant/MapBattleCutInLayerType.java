package com.kyokomi.srpgquest.constant;

public enum MapBattleCutInLayerType {
	// TODO: TAGどこかで管理したい
	PLAYER_TURN_CUTIN(60001, "cutin/player_turn.png", false),
	ENEMY_TURN_CUTIN(60002,  "cutin/enemy_turn.png", false),
	PLAYER_WIN_CUTIN(60003,  "cutin/player_win.png", false),
	GAME_OVER_CUTIN(60004,   "cutin/game_over.jpg", true),
	;
	private Integer value;
	private String fileName;
	private boolean isWindowSize;
	
	private MapBattleCutInLayerType(Integer value, String fileName, boolean isWindowSize) {
		this.value = value;
		this.fileName = fileName;
		this.isWindowSize = isWindowSize;
	}
	public Integer getValue() {
		return value;
	}
	public String getFileName() {
		return fileName;
	}
	public boolean isWindowSize() {
		return isWindowSize;
	}
	public static MapBattleCutInLayerType get(Integer value) {
		MapBattleCutInLayerType[] values = values();
		for (MapBattleCutInLayerType type : values) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("find not tag type.");
	}
}