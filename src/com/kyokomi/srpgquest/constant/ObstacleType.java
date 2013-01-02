package com.kyokomi.srpgquest.constant;

public enum ObstacleType {

	TAG_OBSTACLE_DEFAULT( 0,  0, "",               0, 0,   0, 0,   0),
	TAG_OBSTACLE_TRAP(    1, 50, "main_trap.png",  0, 0,   0, 0, 380),
	TAG_OBSTACLE_FIRE(    2, 40, "main_fire.png",  0, 0,   0, 0, 260),
	TAG_OBSTACLE_ENEMY(   3, 80, "main_enemy.png", 1, 2, 100, 0, 325),
	TAG_OBSTACLE_EAGLE(   4, 70, "main_eagle.png", 1, 2, 200, 0,  30),
	TAG_OBSTACLE_HEART(   5,  0, "main_heart.png", 0, 0,   0, 0,   0),
	;
	
	/** 値. */
	private Integer value;
	/** 衝突許容値. */
	private Integer allowable;
	/** ファイル名. */
	private String fileName;
	/** Spriteコマ数(横). */
	private Integer column;
	/** Spriteコマ数(縦). */
	private Integer row;
	/** Animated Duration. */
	private Integer duration;
	/** x座標. */
	private Integer x;
	/** y座標. */
	private Integer y;
	
	ObstacleType(Integer value, Integer allowable, String fileName, Integer column, Integer row, Integer duration, Integer x, Integer y) {
		this.value = value;
		this.allowable = allowable;
		this.fileName = fileName;
		this.column = column;
		this.row = row;
		this.duration = duration;
		this.x = x;
		this.y = y;
	}
	public static ObstacleType getObstacleType(Integer tag) {
		ObstacleType[] values = values();
		for (ObstacleType ObstacleType : values) {
			if (ObstacleType.getValue() == tag) {
				return ObstacleType;
			}
		}
		return TAG_OBSTACLE_DEFAULT;
	}
	public Integer getValue() {
		return value;
	}
	public Integer getAllowable() {
		return allowable;
	}
	public String getFileName() {
		return fileName;
	}
	public Integer getColumn() {
		return column;
	}
	public Integer getRow() {
		return row;
	}
	public Integer getDuration() {
		return duration;
	}
	public Integer getX() {
		return x;
	}
	public Integer getY() {
		return y;
	}
}