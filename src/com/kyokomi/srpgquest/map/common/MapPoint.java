package com.kyokomi.srpgquest.map.common;

import com.kyokomi.srpgquest.constant.MoveDirectionType;

public class MapPoint {

	public MapPoint(int x, int y, MoveDirectionType direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	private int x;
	
	private int y;
	
	private MoveDirectionType direction;
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * @return the direction
	 */
	public MoveDirectionType getDirection() {
		return direction;
	}
	/**
	 * @param direction the direction to set
	 */
	public void setDirection(MoveDirectionType direction) {
		this.direction = direction;
	}
}