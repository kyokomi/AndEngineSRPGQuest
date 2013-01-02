package com.kyokomi.srpgquest.map.item;

import com.kyokomi.srpgquest.map.common.MapData.MapDataType;

public class MapItem {

	/** 短形マップ上の区分. */
	private MapDataType mapDataType;
	
	/** 短形マップ縦軸表示位置. */
	private int mapPointX;
	
	/** 短形マップ横軸表示位置. */
	private int mapPointY;
	
	/** 移動可能距離. */
	private int moveDist;
	/** 攻撃可能距離. */
	private int attackDist;
	/**
	 * @return the mapDataType
	 */
	public MapDataType getMapDataType() {
		return mapDataType;
	}
	/**
	 * @param mapDataType the mapDataType to set
	 */
	public void setMapDataType(MapDataType mapDataType) {
		this.mapDataType = mapDataType;
	}
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
	 * @return the moveDist
	 */
	public int getMoveDist() {
		return moveDist;
	}
	/**
	 * @param moveDist the moveDist to set
	 */
	public void setMoveDist(int moveDist) {
		this.moveDist = moveDist;
	}
	/**
	 * @return the attackDist
	 */
	public int getAttackDist() {
		return attackDist;
	}
	/**
	 * @param attackDist the attackDist to set
	 */
	public void setAttackDist(int attackDist) {
		this.attackDist = attackDist;
	}
}
