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
	
	public MapPoint(float x, float y, int mapPointX, int mapPointY, float gridSizeX, float gridSizeY, MoveDirectionType direction) {
		this.mapPointX = mapPointX;
		this.mapPointY = mapPointY;
		this.x = x;
		this.y = y;
		this.gridSizeX = gridSizeX;
		this.gridSizeY = gridSizeY;
		this.direction = direction;
	}
	
	private int mapPointX;
	private int mapPointY;
	
	private float x;
	
	private float y;
	
	private float gridSizeX;
	private float gridSizeY;
	
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
	
	public float getGridSizeX() {
		return gridSizeX;
	}

	public void setGridSizeX(float gridSizeX) {
		this.gridSizeX = gridSizeX;
	}

	public float getGridSizeY() {
		return gridSizeY;
	}

	public void setGridSizeY(float gridSizeY) {
		this.gridSizeY = gridSizeY;
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
	
	public MoveDirectionType getPointToMoveDirectionType(MapPoint pMapPoint) {
		int diffX = this.getMapPointX() - pMapPoint.getMapPointX();
		int diffY = this.getMapPointY() - pMapPoint.getMapPointY();
		
		if (diffX == 0 && diffY == 1) {
			return MoveDirectionType.MOVE_DOWN;
		} else if (diffX == 0 && diffY == -1) {
			return MoveDirectionType.MOVE_UP;
		} else if (diffX == 1 && diffY == 0) {
			return MoveDirectionType.MOVE_RIGHT;
		} else if (diffX == -1 && diffY == 0) {
			return MoveDirectionType.MOVE_LEFT;
		} else {
			return MoveDirectionType.MOVE_DEFAULT;
		}
	}
	
	/**
	 * マップ座標一致判定.
	 * @param mapPointX
	 * @param mapPointY
	 * @return true:一致 / false:不一致
	 */
	public boolean isMuchMapPoint(MapPoint mapPoint) {
		return isMuchMapPoint(mapPoint.getMapPointX(), mapPoint.getMapPointY());
	}
	/**
	 * マップ座標一致判定.
	 * @param mapPointX
	 * @param mapPointY
	 * @return true:一致 / false:不一致
	 */
	public boolean isMuchMapPoint(int mapPointX, int mapPointY) {
		return this.mapPointX == mapPointX && this.mapPointY == mapPointY;
	}
}