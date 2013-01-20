package com.kyokomi.srpgquest.map;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.MapItem;

/**
 * マップ上に配置するオブジェクトを管理します。
 * @author kyokomi
 * TODO: シングルトンでいい
 */
public class MapItemManager {
	private static final String TAG = "MapItemManager";
	
	private final int mMapX;
	private final int mMapY;
	
	/** カーソルを管理するレイヤー. */
	private MapItem[][] mCursorMapItemLayer;
	/** キャラクターや障害物などのオブジェクトを管理するレイヤー. */
	private MapItem[][] mObjectMapItemLayer;
	/** マップの根底. */
	private MapItem[][] mBaseMapItemLayer;
	
	public MapItemManager(final int pMapX, final int pMapY) {
		this.mMapX = pMapX;
		this.mMapY = pMapY;

		clear();
	}
	
	public void clear() {
		clearCursorMapItemLayer();
		clearObjectMapItemLayer();
		clearBaseMapItemLayer();
	}
	public void clearCursorMapItemLayer() {
		mCursorMapItemLayer = new MapItem[mMapX][mMapY];
	}
	public void clearObjectMapItemLayer() {
		mObjectMapItemLayer = new MapItem[mMapX][mMapY];
	}
	public void clearBaseMapItemLayer() {
		mBaseMapItemLayer = new MapItem[mMapX][mMapY];
	}
	
	public List<MapItem> getAttackCursorMapItemList() {
		return getCursorMapItemList(MapDataType.ATTACK_DIST);
	}
	public List<MapItem> getMoveCursorMapItemList() {
		return getCursorMapItemList(MapDataType.MOVE_DIST);
	}
	private List<MapItem> getCursorMapItemList(MapDataType mapDataType) {
		List<MapItem> cursorList = new ArrayList<MapItem>();
		for (int x = 0; x < mCursorMapItemLayer.length; x++) {
			for (int y = 0; y < mCursorMapItemLayer[x].length; y++) {
				if (getCursor(x,y) != null && getCursor(x,y).getMapDataType() == mapDataType) {
					cursorList.add(getCursor(x,y));
				}
			}	
		}
		return cursorList;
	}
	
	public void setCursor(MapPoint mapPoint, MapItem mapItem) {
		setCursor(mapPoint.getMapPointX(), mapPoint.getMapPointY(), mapItem);
	}
	public void setCursor(int mapPointX, int mapPointY, MapItem mapItem) {
		setMapItem(mCursorMapItemLayer, mapPointX, mapPointY, mapItem);
	}
	public MapItem getCursor(MapPoint mapPoint) {
		return getCursor(mapPoint.getMapPointX(), mapPoint.getMapPointY());
	}
	public MapItem getCursor(int mapPointX, int mapPointY) {
		return getMapItem(mCursorMapItemLayer, mapPointX, mapPointY);
	}
	public void setObject(MapPoint mapPoint, MapItem mapItem) {
		setObject(mapPoint.getMapPointX(), mapPoint.getMapPointY(), mapItem);
	}
	public void setObject(int mapPointX, int mapPointY, MapItem mapItem) {
		setMapItem(mObjectMapItemLayer, mapPointX, mapPointY, mapItem);
	}
	public MapItem getObject(MapPoint mapPoint) {
		return getObject(mapPoint.getMapPointX(), mapPoint.getMapPointY());
	}
	public MapItem getObject(int mapPointX, int mapPointY) {
		return getMapItem(mObjectMapItemLayer, mapPointX, mapPointY);
	}
	public void setBase(MapPoint mapPoint, MapItem mapItem) {
		setBase(mapPoint.getMapPointX(), mapPoint.getMapPointY(), mapItem);
	}
	public void setBase(int mapPointX, int mapPointY, MapItem mapItem) {
		setMapItem(mBaseMapItemLayer, mapPointX, mapPointY, mapItem);
	}
	public MapItem getBase(MapPoint mapPoint) {
		return getBase(mapPoint.getMapPointX(), mapPoint.getMapPointY());
	}
	public MapItem getBase(int mapPointX, int mapPointY) {
		return getMapItem(mBaseMapItemLayer, mapPointX, mapPointY); 
	}
	private void setMapItem(MapItem[][] mapItems, int mapPointX, int mapPointY, MapItem mapItem) {
		if (mapItems.length <= mapPointX || mapItems[0].length <= mapPointY) {
			// 範囲外
			return;
		}
		mapItems[mapPointX][mapPointY] = mapItem;
	}
	private MapItem getMapItem(MapItem[][] mapItems, int mapPointX, int mapPointY) {
		if (mapItems.length <= mapPointX || mapItems[0].length <= mapPointY) {
			// 範囲外
			return null;
		}
		return mapItems[mapPointX][mapPointY]; 
	}
	
	public boolean chkMove(int mapPointX, int mapPointY, int dist) {
		MapItem mapItem = getPointItem(mapPointX, mapPointY);
		if (mapItem == null || 
				(mapItem.getMapDataType() == MapDataType.NONE || 
					(mapItem.getMapDataType() == MapDataType.MOVE_DIST && mapItem.getMoveDist() < dist ))) {
			return true;
		}
		return false;
	}
	public boolean chkAttack(int mapPointX, int mapPointY, int dist) {
		MapItem mapItem = getPointItem(mapPointX, mapPointY);
		if (mapItem == null || 
				(mapItem.getMapDataType() == MapDataType.NONE || 
					mapItem.getMapDataType() == MapDataType.ENEMY || 
					mapItem.getMapDataType() == MapDataType.MOVE_DIST)) {
			return true;
		}
		return false;
	}
	public boolean chkMovePoint(int mapPointX, int mapPointY, int dist, MapDataType ignoreDataType) {
		MapItem mapItem = getCursor(mapPointX, mapPointY);
		if (mapItem != null && mapItem.getMoveDist() == dist 
				&& mapItem.getMapDataType() != ignoreDataType) {
			return true;
		}
		return false;
	}
	
	
	public MapItem getPointItem(MapPoint mapPoint) {
		return getPointItem(mapPoint);
	}
	public MapItem getPointItem(int mapPointX, int mapPointY) {
		MapItem mapItem = getCursor(mapPointX, mapPointY);
		if (mapItem == null) {
			mapItem = getObject(mapPointX, mapPointY);
			if (mapItem == null) {
				mapItem = getBase(mapPointX, mapPointY);
				if (mapItem == null) {
					return null;
				}
			}
		}
		return mapItem;
	}
	// ----------------------
	// TODO: デバッグ用
	// ----------------------
//	/**
//	 * TODO: test用
//	 */
//	public void debugShowMapDatas() {
//		Log.d(TAG, "====== debugShowMapDatas ======");
//		StringBuffer buffer = null;
//		for (int k = 0; k < mapDatas[0].length; k++) {
//			buffer = new StringBuffer();
//			for (int i = 0; i < mapDatas.length; i++) {
//				buffer.append(mapDatas[i][k].getType().getValue());
//				buffer.append(".");
//			}			
//			Log.d(TAG, buffer.toString());
//		}
//	}
	
	public void DEBUG_LOG_MAP_ITEM_LAYER() {
		Log.d(TAG, "====== DEBUG_LOG_CURSOR_MAP_ITEM_LAYER ======");
		
		StringBuffer buffer = null;
		for (int y = 0; y < mMapY; y++) {
			buffer = new StringBuffer();
			for (int x = 0; x < mMapX; x++) {
				String outPutStr = "-";
				String baseLayerStr = logOutString(mBaseMapItemLayer[x][y]);
				String objectLayerStr = logOutString(mObjectMapItemLayer[x][y]);
				String cursorLayerStr = logOutString(mCursorMapItemLayer[x][y]);				
				if (cursorLayerStr != null) {
					outPutStr = cursorLayerStr;
				} else if (objectLayerStr != null) {
					outPutStr = objectLayerStr;
				} else if (baseLayerStr != null) {
					outPutStr = baseLayerStr;
				}
				buffer.append(outPutStr);
				buffer.append(".");
			}			
			Log.d(TAG, buffer.toString());
		}
	}
	private String logOutString(MapItem mapItem) { 
		if (mapItem == null) {
			return null;
		} else if (mapItem.getMapDataType() == MapDataType.ENEMY) {
			return ("E");
		} else if (mapItem.getMapDataType() == MapDataType.MAP_ITEM) {
			return ("@");
		} else {
			return String.valueOf(mapItem.getMoveDist());
		}
	}
}
