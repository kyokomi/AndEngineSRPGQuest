package com.kyokomi.srpgquest.constant;

/**
 * マップ移動方向の定数.
 * @author kyokomi
 *
 */
public enum MoveDirectionType {
	MOVE_DOWN(0, 6),
	MOVE_LEFT(1, 0),
	MOVE_RIGHT(2, 9),
	MOVE_UP(3, 3),

	// デフォルトはDOWN
	MOVE_DEFAULT(
			MoveDirectionType.MOVE_DOWN.getValue(),
			MoveDirectionType.MOVE_DOWN.getDirection())
	;
	
	private Integer value;
	private Integer direction;
	
	private MoveDirectionType(Integer value, Integer direction) {
		this.value = value;
		this.direction = direction;
	}
	public Integer getValue() {
		return value;
	}
	public Integer getDirection() {
		return direction;
	}
}