package com.kyokomi.srpgquest.constant;

public enum MapBattleCutInLayerType {
	// TODO: TAGどこかで管理したい
	PLAYER_TURN_CUTIN(60001, "cutin/player_turn.png"),
	ENEMY_TURN_CUTIN(60002,  "cutin/enemy_turn.png"),
	PLAYER_WIN_CUTIN(60003,  "cutin/player_win.png"),
	GAME_OVER_CUTIN(60004,   "cutin/game_over.jpg"),
	;
	private Integer value;
	private String fileName;
	private MapBattleCutInLayerType(Integer value, String fileName) {
		this.value = value;
		this.fileName = fileName;
	}
	public Integer getValue() {
		return value;
	}
	public String getFileName() {
		return fileName;
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