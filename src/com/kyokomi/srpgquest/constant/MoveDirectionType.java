package com.kyokomi.srpgquest.constant;

/**
 * マップ移動方向の定数.
 * @author kyokomi
 *
 */
public enum MoveDirectionType {
	MOVE_DOWN(0),
	MOVE_LEFT(1),
	MOVE_RIGHT(2),
	MOVE_UP(3);
	
	private Integer value;
	
	private MoveDirectionType(Integer value) {
		this.value = value;
	}
	public Integer getValue() {
		return value;
	}
}