package com.kyokomi.srpgquest.map.common;

import com.kyokomi.srpgquest.constant.MapDataType;

/**
 * マップ情報.
 * @author kyokomi
 *
 */
public class MapData {
	
	/**
	 * コンストラクタ.
	 */
	public MapData() {
		this.dist = 0;
		this.type = MapDataType.NONE;
	}
	
	/** タイプ. */
	private MapDataType type;
	
	/** 残移動数. */
	private int dist;
	
	public boolean chkMove(int dist) {
		if (type == MapDataType.NONE || (type == MapDataType.MOVE_DIST && this.dist < dist )) {
			return true;
		}
		return false;
	}
	public boolean chkAttack(int dist) {
		if (type == MapDataType.NONE || type == MapDataType.ENEMY || type == MapDataType.MOVE_DIST) {
			return true;
		}
		return false;
	}
	/**
	 * @return the type
	 */
	public MapDataType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(MapDataType type) {
		this.type = type;
	}
	/**
	 * @return the dist
	 */
	public int getDist() {
		return dist;
	}
	/**
	 * @param dist the dist to set
	 */
	public void setDist(int dist) {
		this.dist = dist;
	}
}