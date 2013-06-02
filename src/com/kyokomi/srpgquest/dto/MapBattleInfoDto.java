package com.kyokomi.srpgquest.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kyokomi.srpgquest.constant.MapBattleType;

import android.util.SparseArray;

/**
 * asset/map/x_x.jsonを読み込んだDTOです.
 * @author kyokomi
 *
 */
public class MapBattleInfoDto {

	private Integer mapId;
	private MapBattleType mapBattleType;
	private Integer mapSizeX;
	private Integer mapSizeY;
	
	private List<MapSymbol> mapSymbolList;
	private SparseArray<MapSymbol> mapSymbolArray;

	public void createMapJsonData(int pMapid, JSONArray pJsonArray) {
		this.mapId = pMapid;
		try {
			for (int i = 0; i < pJsonArray.length(); i++) {
				mapSymbolArray = new SparseArray<MapSymbol>();
				
				JSONObject jsonObj = pJsonArray.getJSONObject(i);
				// 基本情報を取得
				this.mapBattleType =  MapBattleType.get(jsonObj.getInt("mapBattleType"));
				this.mapSizeX =  jsonObj.getInt("mapSizeX");
				this.mapSizeY =  jsonObj.getInt("mapSizeY");
				// シンボル情報を取得
				JSONArray symbolJsonArray =  jsonObj.getJSONArray("symbol");
				for (int j = 0; j < symbolJsonArray.length(); j++) {
					JSONObject symbolJson = symbolJsonArray.getJSONObject(j);
					if (symbolJson == null) {
						continue;
					}
					MapSymbol symbol = new MapSymbol();
					symbol.setSeqNo(symbolJson.getInt("seqNo"));
					symbol.setType(symbolJson.getInt("type"));
					symbol.setId(symbolJson.getInt("id"));
					mapSymbolArray.put(symbol.getSeqNo(), symbol);
				}
				mapSymbolList = new ArrayList<MapBattleInfoDto.MapSymbol>();
				// マップ情報を取得しシンボルのX,Y座標を設定
				JSONArray mapJsonArray = jsonObj.getJSONArray("map");
				for (int y = 0; y < mapJsonArray.length(); y++) {
					String mapStr = mapJsonArray.getString(y);
					if (mapStr == null || mapStr.length() != this.mapSizeX) {
						continue;
					}
					for (int x = 0; x < mapStr.length(); x++) {
						char mapChar = mapStr.charAt(x);
						int mapDataSeqId = Integer.parseInt(String.valueOf(mapChar));
						if (mapDataSeqId <= 0) {
							continue;
						}
						MapSymbol mapSymbol = mapSymbolArray.get(mapDataSeqId);
						if (mapSymbol != null) {
							MapSymbol mapItem = new MapSymbol();
							mapItem.setSeqNo(mapSymbol.getSeqNo());
							mapItem.setType(mapSymbol.getType());
							mapItem.setId(mapSymbol.getId());
							mapItem.setMapPointX(x);
							mapItem.setMapPointY(y);
							mapSymbolList.add(mapItem);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public Integer getMapId() {
		return mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public MapBattleType getMapBattleType() {
		return mapBattleType;
	}
	public void setMapBattleType(MapBattleType mapBattleType) {
		this.mapBattleType = mapBattleType;
	}
	public Integer getMapSizeX() {
		return mapSizeX;
	}

	public void setMapSizeX(Integer mapSizeX) {
		this.mapSizeX = mapSizeX;
	}

	public Integer getMapSizeY() {
		return mapSizeY;
	}

	public void setMapSizeY(Integer mapSizeY) {
		this.mapSizeY = mapSizeY;
	}

	public SparseArray<MapSymbol> getMapSymbolArray() {
		return mapSymbolArray;
	}

	public void setMapSymbolArray(SparseArray<MapSymbol> mapSymbolArray) {
		this.mapSymbolArray = mapSymbolArray;
	}

	public List<MapSymbol> getMapSymbolList() {
		return mapSymbolList;
	}
	public void setMapSymbolList(List<MapSymbol> mapSymbolList) {
		this.mapSymbolList = mapSymbolList;
	}

	public class MapSymbol {
		private Integer seqNo;
		private Integer type;
		private Integer id;
		private Integer mapPointX;
		private Integer mapPointY;
		
		public Integer getSeqNo() {
			return seqNo;
		}
		public void setSeqNo(Integer seqNo) {
			this.seqNo = seqNo;
		}
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getMapPointX() {
			return mapPointX;
		}
		public void setMapPointX(Integer mapPointX) {
			this.mapPointX = mapPointX;
		}
		public Integer getMapPointY() {
			return mapPointY;
		}
		public void setMapPointY(Integer mapPointY) {
			this.mapPointY = mapPointY;
		}
	}
}
