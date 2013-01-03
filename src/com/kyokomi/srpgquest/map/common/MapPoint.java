package com.kyokomi.srpgquest.map.common;

import com.kyokomi.srpgquest.constant.MoveDirectionType;

public class MapPoint {

	public MapPoint(float x, float y, int mapPointX, int mapPointY, MoveDirectionType direction) {
		this.mapPointX = mapPointX;
		this.mapPointY = mapPointY;
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	public MapPoint(float x, float y, int mapPointX, int mapPointY, float gridSize, MoveDirectionType direction) {
		this.mapPointX = mapPointX;
		this.mapPointY = mapPointY;
		this.x = x;
		this.y = y;
		this.gridSize = gridSize;
		this.direction = direction;
	}
	
	private int mapPointX;
	private int mapPointY;
	
	private float x;
	
	private float y;
	
	private float gridSize;
	
	private MoveDirectionType direction;
	

	/**
	 * @return the mapPointX
	 */
	public int getMapPointX() {
		return mapPointX;
	}

	/**
	 * @param mapPointX the mapPointX to set
	 */
	public void setMapPointX(int mapPointX) {
		this.mapPointX = mapPointX;
	}

	/**
	 * @return the mapPointY
	 */
	public int getMapPointY() {
		return mapPointY;
	}

	/**
	 * @param mapPointY the mapPointY to set
	 */
	public void setMapPointY(int mapPointY) {
		this.mapPointY = mapPointY;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}
	/**
	 * @return the gridSize
	 */
	public float getGridSize() {
		return gridSize;
	}
	/**
	 * @param gridSize the gridSize to set
	 */
	public void setGridSize(float gridSize) {
		this.gridSize = gridSize;
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