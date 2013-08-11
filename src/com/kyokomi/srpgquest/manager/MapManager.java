package com.kyokomi.srpgquest.manager;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.MapItem;
import com.kyokomi.srpgquest.utils.MapGridUtil;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

/**
 * マップ全体を管理するクラス.
 * マップとスプライトを全て保持する。
 * それぞれ個別のリストとまとめて管理するsprite、マップ位置をもとに管理するmap_dataというように多重管理。
 * 
 * @author kyokomi
 *
 */
public class MapManager {
	private static final String TAG = "MapManager";
	
	private static final int TOP    = 0;
	private static final int LEFT   = 0;
	private final int RIGHT;
	private final int BOTTOM;
	
	private final MapItemManager mMapItemManager;
	private final int mapX;
	private final int mapY;
	private final float mGridSizeX;
	private final float mGridSizeY;
	
	/** マップ移動情報. */
	private List<MapPoint> movePointList;
	
	public void debugShowMoveList() {
		Log.d(TAG, "====== debugShowMoveList ======");
		StringBuffer buffer = null;
		for (MapPoint movePoint : movePointList) {
			buffer = new StringBuffer();
			buffer.append("(");
			buffer.append(movePoint.getMapPointX());
			buffer.append(",");
			buffer.append(movePoint.getMapPointY());
			buffer.append(",");
			buffer.append(movePoint.getDirection());
			buffer.append(")");
			Log.d(TAG, buffer.toString());
		}
	}
	/**
	 * コンストラクタ.
	 * @param activity
	 * @param mapX
	 * @param mapY
	 * @param mapScale
	 */
	public MapManager(float pGridSize, int pMapX, int pMapY, float pMapScale) {
		this.mapX = pMapX;
		this.mapY = pMapY;
		this.mGridSizeX = pGridSize;
		this.mGridSizeY = pGridSize;
		this.RIGHT = mapX;
		this.BOTTOM = mapY;
		
		this.mMapItemManager = new MapItemManager(mapX, mapY);		
	}
	/**
	 * コンストラクタ.
	 * @param activity
	 * @param mapX
	 * @param mapY
	 * @param mapScale
	 */
	public MapManager(float pGridSizeX, float pGridSizeY, int pMapX, int pMapY, float pMapScale) {
		this.mapX = pMapX;
		this.mapY = pMapY;
		this.mGridSizeX = pGridSizeX;
		this.mGridSizeY = pGridSizeY;
		this.RIGHT = mapX;
		this.BOTTOM = mapY;
		
		this.mMapItemManager = new MapItemManager(mapX, mapY);		
	}

	/**
	 * プレイヤーキャラ追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	public void addPlayer(int seqNo, int mapPointX, int mapPointY, ActorPlayerDto actorPlayer) {
		addActor(MapDataType.PLAYER, seqNo, mapPointX, mapPointY, actorPlayer);
	}
	
	/**
	 * エネミーキャラ追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	public void addEnemy(int seqNo, int mapPointX, int mapPointY, ActorPlayerDto actorPlayer) {
		addActor(MapDataType.ENEMY, seqNo, mapPointX, mapPointY, actorPlayer);
	}
	
	/**
	 * 障害物追加.
	 * @param mapPointX
	 * @param mapPointY
	 */
	public void addObstacle(int mapPointX, int mapPointY) {
		MapItem mapItem = new MapItem();
		mapItem.setAttackDist(0);
		mapItem.setMoveDist(0);
		mapItem.setMapDataType(MapDataType.MAP_ITEM);
		mapItem.setMapPointX(mapPointX);
		mapItem.setMapPointY(mapPointY);
		
		mMapItemManager.setObject(mapPointX, mapPointY, mapItem);
	}
	
	/**
	 * アクター追加.
	 * @param mapDataType
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	private void addActor(MapDataType mapDataType, int seqNo, int mapPointX, int mapPointY, ActorPlayerDto actorPlayer) {
		ActorPlayerMapItem playerMapItem = new ActorPlayerMapItem();
		playerMapItem.setSeqNo(seqNo);
		playerMapItem.setAttackDist(actorPlayer.getAttackRange());
		playerMapItem.setMoveDist(actorPlayer.getMovePoint());
		
		playerMapItem.setMapDataType(mapDataType);
		playerMapItem.setMapPointX(mapPointX);
		playerMapItem.setMapPointY(mapPointY);
		
		mMapItemManager.setObject(mapPointX, mapPointY, playerMapItem);
	}
	
	/**
	 * 移動カーソル追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param dist
	 */
	public void addDistCursor(int mapPointX, int mapPointY, int dist) {
		// 未設定 or 移動オブジェクトで移動力が上の場合
		MapItem mapItem = mMapItemManager.getCursor(mapPointX, mapPointY);
		if (mapItem == null || 
				mapItem.getMapDataType() == MapDataType.NONE ||
				(mapItem.getMapDataType() == MapDataType.MOVE_DIST &&
						mapItem.getMoveDist() < dist)) {
			
			// リストに入れたやつだけあとで描画する
			MapItem cursorItem = new MapItem();
			cursorItem.setMapDataType(MapDataType.MOVE_DIST);
			cursorItem.setMapPointX(mapPointX);
			cursorItem.setMapPointY(mapPointY);
			cursorItem.setMoveDist(dist);
			cursorItem.setAttackDist(0);
			
			mMapItemManager.setCursor(mapPointX, mapPointY, cursorItem);
		}
	}
	
	/**
	 * 攻撃カーソル追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param dist
	 */
	public void addAttackCursor(int mapPointX, int mapPointY, int dist) {
		// 未設定カーソルの区分以外は無視
		MapItem mapItem = mMapItemManager.getCursor(mapPointX, mapPointY);
		if (mapItem == null || 
				mapItem.getMapDataType() == MapDataType.NONE || 
				mapItem.getMapDataType() == MapDataType.ENEMY ) {
			
			// リストに入れたやつだけあとで描画する
			MapItem cursorItem = new MapItem();
			cursorItem.setMapDataType(MapDataType.ATTACK_DIST);
			cursorItem.setMapPointX(mapPointX);
			cursorItem.setMapPointY(mapPointY);
			cursorItem.setMoveDist(0);
			cursorItem.setAttackDist(dist);
			
			mMapItemManager.setCursor(mapPointX, mapPointY, cursorItem);
		}
	}
	
	/**
	 * キャラクター移動範囲検索.
	 * @param actorItem キャラクタービュー
	 */
	public List<MapItem> actorPlayerFindDist(MapPoint mapPoint) {
		ActorPlayerMapItem actorPlayerMapItem = getMapPointToActorPlayer(mapPoint);
		if (actorPlayerMapItem != null) {
			return actorPlayerFindDist(actorPlayerMapItem);
		}
		return null;
	}
	/**
	 * キャラクター移動範囲検索.
	 * @param actorItem キャラクタービュー
	 */
	public List<MapItem> actorPlayerFindDist(ActorPlayerMapItem actorItem) {
		// キャラクターの現在位置を取得
		int mapX = actorItem.getMapPointX();
		int mapY = actorItem.getMapPointY();
		int dist = actorItem.getMoveDist();

		// カーソル情報を初期化
		mMapItemManager.clearCursorMapItemLayer();
		
		// 検索開始(再帰呼び出し)
		findDist(mapX, mapY, dist, true);
		
		// cursorListを作成
		return mMapItemManager.getMoveCursorMapItemList();		
	}
	
	/**
	 * 指定したプレイヤーの最短距離移動情報作成.
	 * @param moveToActorPlayer 移動対象プレイヤー
	 * @param moveFromMapPoint 移動先マップ情報
	 * @return 移動情報
	 */
	public List<MapPoint> actorPlayerCreateMovePointList(
			ActorPlayerMapItem moveToActorPlayer, MapPoint moveFromMapPoint) {

		mMapItemManager.DEBUG_LOG_MAP_ITEM_LAYER(); // DEBUG
		
		MapItem moveFromMapItem = mMapItemManager.getCursor(moveFromMapPoint);
		if (moveFromMapItem == null) {
			Log.e(TAG, "moveFromMapItem not found xy[" + 
					moveFromMapPoint.getMapPointX() + ", " + moveFromMapPoint.getMapPointY() + "]");
			return null;
		}
	    // 移動情報作成
		movePointList = new ArrayList<MapPoint>();
		// 移動開始点を現在地として登録
		movePointList.add(getMoveMapPoint(
				moveToActorPlayer.getMapPointX(), 
				moveToActorPlayer.getMapPointY(), 
				MoveDirectionType.MOVE_DEFAULT));
		// 移動経路を作成
		createMovePointList(moveFromMapPoint.getMapPointX(), moveFromMapPoint.getMapPointY(), 
				moveFromMapItem.getMoveDist(), moveToActorPlayer);

		// 最初の移動開始時の方向を割り出す（一歩以上移動する場合）
		if (movePointList.size() > 2) {
			movePointList.get(0).setDirection(
					movePointList.get(1).getPointToMoveDirectionType(movePointList.get(0)));			
		}
		// 一つ手前の位置から最後の移動箇所へ向かう方向を割り出す
		moveFromMapPoint.setDirection(
				moveFromMapPoint.getPointToMoveDirectionType(
						movePointList.get(movePointList.size() - 1)));
		// 目的地を最後の移動箇所に指定
		movePointList.add(moveFromMapPoint);
		
		debugShowMoveList(); // DEBUG

		return movePointList;
	}
	
	/**
	 * 移動後マップ情報変更.
	 * @param mapPointX
	 * @param mapPointY
	 * @param moveToView
	 */
	public void moveEndChangeMapItem(
			ActorPlayerMapItem moveToActorPlayer, MapPoint moveFromMapPoint) {
		
		// リストとかカーソルまわりの情報を全部クリア
		movePointList = new ArrayList<MapPoint>();
		
		// 移動後のマップ情報を更新
		int oldMapPointX = moveToActorPlayer.getMapPointX();
		int oldMapPointY = moveToActorPlayer.getMapPointY();
		mMapItemManager.setObject(oldMapPointX, oldMapPointY, null);
		
		int moveMapPointX = moveFromMapPoint.getMapPointX();
		int moveMapPointY = moveFromMapPoint.getMapPointY();
		mMapItemManager.setObject(moveMapPointX, moveMapPointY, moveToActorPlayer);
		moveToActorPlayer.setMapPointX(moveMapPointX);
		moveToActorPlayer.setMapPointY(moveMapPointY);
		
		// 移動範囲カーソルをクリア
		mMapItemManager.clearCursorMapItemLayer();
	}
	
	/**
	 * 移動後マップ情報変更.
	 * @param mapPointX
	 * @param mapPointY
	 * @param moveToView
	 */
	public void attackEndChangeMapItem(ActorPlayerMapItem fromPlayerMapItem, ActorPlayerMapItem toPlayerMapItem, boolean isDead) {
		// 攻撃範囲カーソルをクリア
		mMapItemManager.clearCursorMapItemLayer();
		
		// 死亡した場合、攻撃対象をマップ上から消去する
		if (isDead) {
			mMapItemManager.setObject(toPlayerMapItem.getMapPointX(), 
					toPlayerMapItem.getMapPointY(), null);
		}
	}
	
	/**
	 * キャラクター攻撃範囲検索.
	 * @param mapPoint
	 */
	public List<MapItem> actorPlayerFindAttackDist(MapPoint mapPoint) {
		ActorPlayerMapItem actorPlayerMapItem = getMapPointToActorPlayer(mapPoint);
		if (actorPlayerMapItem != null) {
			return actorPlayerFindAttackDist(actorPlayerMapItem);
		}
		return null;
	}
	private List<MapItem> actorPlayerFindAttackDist(ActorPlayerMapItem actorPlayerMapItem) {
		// キャラクターの現在位置を元に短形グリッド座標を計算
		int x = actorPlayerMapItem.getMapPointX();
		int y = actorPlayerMapItem.getMapPointY();
		int dist = actorPlayerMapItem.getAttackDist();
		
		// カーソル情報を初期化
		mMapItemManager.clearCursorMapItemLayer();
		
		// 検索開始(再帰呼び出し)
		findAttack(x, y, dist, true);
		
		// cursorListを作成
		return mMapItemManager.getAttackCursorMapItemList();
	}
	
	/**
	 * 移動範囲検索.
	 * @param x
	 * @param y
	 * @param dist
	 * @param first
	 */
	private void findDist(int x, int y, int dist, boolean first) {
		
		// 初期位置は移動対象外とする制御
		if (!first) {
			// 移動可能範囲に追加
			addDistCursor(x, y, dist);
		}
		if (dist == 0) {
			return;
		}

		// 上にいけるか?
		if (y > TOP && mMapItemManager.chkMove(x, y - 1, dist)) {
			findDist(x, y - 1, dist - 1, false);
		}
		// 下にいけるか?
		if (y < (BOTTOM -1) && mMapItemManager.chkMove(x, y + 1, dist)) {
			findDist(x, y + 1, dist - 1, false);
		}	
		// 右にいけるか?
		if (x > LEFT && mMapItemManager.chkMove(x - 1, y, dist)) {
			findDist(x - 1, y, dist - 1, false);
		}
		// 左にいけるか?
		if (x < (RIGHT - 1) && mMapItemManager.chkMove(x + 1, y, dist)) {
			findDist(x + 1, y, dist - 1, false);
		}
	}
	
	/**
	 * 攻撃範囲検索.
	 * @param x
	 * @param y
	 * @param dist
	 * @param first
	 */
	private void findAttack(int x, int y, int dist, boolean first) {
		
		// 初期位置は移動対象外とする制御
		if (!first) {
			// 移動可能範囲に追加
			addAttackCursor(x, y, dist);
		}
		if (dist == 0) {
			return;
		}
		// 上にいけるか?
		if (y > TOP && mMapItemManager.chkAttack(x, y - 1, dist)) {
			findAttack(x, y - 1, dist - 1, false);
		}
		// 下にいけるか?
		if (y < (BOTTOM -1) && mMapItemManager.chkAttack(x, y + 1, dist)) {
			findAttack(x, y + 1, dist - 1, false);
		}	
		// 右にいけるか?
		if (x > LEFT && mMapItemManager.chkAttack(x - 1, y, dist)) {
			findAttack(x - 1, y, dist - 1, false);
		}
		// 左にいけるか?
		if (x < (RIGHT - 1) && mMapItemManager.chkAttack(x + 1, y, dist)) {
			findAttack(x + 1, y, dist - 1, false);
		}
	}
	
	/**
	 * 攻撃方向検索.
	 * @param x
	 * @param y
	 * @param dist
	 * @param first
	 */
	public MoveDirectionType findAttackDirection(MapItem fromMapItem, MapItem toMapItem) {
		MoveDirectionType resultType = MoveDirectionType.MOVE_DEFAULT;
		
		int directionX = fromMapItem.getMapPointX() - toMapItem.getMapPointX();
		int directionY = fromMapItem.getMapPointY() - toMapItem.getMapPointY();
		Log.d(TAG, "directX = " + directionX + " directY = " + directionY);
		// 左
		if (directionX >= 1) {
			resultType = MoveDirectionType.MOVE_LEFT;			
		// 右
		} else if (directionX < 0) {
			resultType = MoveDirectionType.MOVE_RIGHT;
		// 左
		} else if (directionY >= 1) {
			resultType = MoveDirectionType.MOVE_UP;
		// 右
		} else if (directionY < 0) {
			resultType = MoveDirectionType.MOVE_DOWN;
		}
		return resultType;
	}
	
	/**
	 * 移動ルート情報を作成.
	 * movePointListに追加していきます.
	 * 
	 * @param x
	 * @param y
	 * @param dist
	 * @param moveMapItem
	 */
	private void createMovePointList(int x, int y, int dist, MapItem moveMapItem) {
		
		// 自軍キャラでなければ通過不可能とする除外Type
		MapDataType ignoreDataType;
		if (moveMapItem.getMapDataType() == MapDataType.PLAYER) {
			ignoreDataType = MapDataType.ENEMY;
		} else {
			ignoreDataType = MapDataType.PLAYER;
		}
		
		// タップ位置から自キャラがいるところまでの最短ルートを探す
		if (mMapItemManager.getObject(x, y) != moveMapItem) {
			// タップした位置のdistの次はどこか探す
			dist++;
			
			// 下か
			if (y > TOP && mMapItemManager.chkMovePoint(x, y - 1, dist, ignoreDataType)) {
				createMovePointList(x, y-1, dist, moveMapItem);
				movePointList.add(getMoveMapPoint(x, y-1, MoveDirectionType.MOVE_DOWN));
			}
			// 上か？
			else if (y < (BOTTOM -1) && mMapItemManager.chkMovePoint(x, y + 1, dist, ignoreDataType)) {
				createMovePointList(x, y+1, dist, moveMapItem);
				movePointList.add(getMoveMapPoint(x, y+1, MoveDirectionType.MOVE_UP));
			}
			// 右か?
			else if (x > LEFT && mMapItemManager.chkMovePoint(x - 1, y, dist, ignoreDataType)) {
				createMovePointList(x-1, y, dist, moveMapItem);
				movePointList.add(getMoveMapPoint(x-1, y, MoveDirectionType.MOVE_RIGHT));
			}
			// 左にいけるか?
			else if (x < (RIGHT - 1) && mMapItemManager.chkMovePoint(x + 1, y, dist, ignoreDataType)) {
				createMovePointList(x+1, y, dist, moveMapItem);
				movePointList.add(getMoveMapPoint(x+1, y, MoveDirectionType.MOVE_LEFT));
			}
		}
	}
	
	/**
	 * プレイヤーターン終了判定.
	 * @return
	 */
	public boolean checkPlayerTurnEnd(MapDataType pMapDataType) {
		boolean isAllWait = true;
		List<MapItem> playerMapItemList = mMapItemManager.getObjectMapItemList(pMapDataType);
		for (MapItem mapItem : playerMapItemList) {
			if (mapItem instanceof ActorPlayerMapItem) {
				if (((ActorPlayerMapItem) mapItem).isWaitDone() == false) {
					isAllWait = false;
					break;
				}
			}
		}
		return isAllWait;
	}
	
	/**
	 * 全プレイヤーを行動可能にする
	 * @return
	 */
	public void refreshAllActorWait(MapDataType mapDataType) {
		List<MapItem> playerMapItemList = mMapItemManager.getObjectMapItemList(mapDataType);
		for (MapItem mapItem : playerMapItemList) {
			if (mapItem instanceof ActorPlayerMapItem) {
				((ActorPlayerMapItem) mapItem).setWaitDone(false);
			}
		}
	}

	public ActorPlayerMapItem findAttackPlayerMapitem(ActorPlayerMapItem enemyMapItem) {
		ActorPlayerMapItem attackTarget = null;
		// 評価ポイント方式
		int evalPoint = Integer.MAX_VALUE;
		
		// 障害物を無視して距離を求める
		// 最も直線距離の近い相手を攻撃目標にする
		List<MapItem> playerList = mMapItemManager.getObjectMapItemList(MapDataType.PLAYER);
		for (MapItem player : playerList) {
			if (player instanceof ActorPlayerMapItem) {
				int dist = Math.abs(enemyMapItem.getMapPointX() - player.getMapPointX()) + 
						Math.abs(enemyMapItem.getMapPointY() - player.getMapPointY());
				if (evalPoint > dist) {
					attackTarget = (ActorPlayerMapItem) player;
					evalPoint = dist;
				}				
			}
		}
		return attackTarget;
	}
	
	/**
	 * 敵の移動先マップポイントを探索.
	 * @param attackTarget
	 * @param enemyMapItem
	 * @return
	 */
	public MapPoint findEnemyMoveMapPoint(ActorPlayerMapItem attackTarget, ActorPlayerMapItem enemyMapItem) {
		
		int evalPoint = Integer.MIN_VALUE;
		int moveX = enemyMapItem.getMapPointX();
		int moveY = enemyMapItem.getMapPointY();
		
		int tageX = attackTarget.getMapPointX();
		int tageY = attackTarget.getMapPointY();
		
		for (int y = TOP; y < BOTTOM; y++) {
			for (int x = LEFT; x < RIGHT; x++) {
				// 攻撃距離計算
				int dist = Math.abs(x - tageX) + Math.abs(y - tageY);
				// 攻撃範囲内
				if (dist <= enemyMapItem.getAttackDist()) {
					int moveDist = 0;
					
					if (mMapItemManager.getCursor(x, y) != null && 
							mMapItemManager.getCursor(x, y).getMapDataType() == MapDataType.MOVE_DIST) {
						// 移動可能範囲
						moveDist = mMapItemManager.getCursor(x, y).getMoveDist();
					} else {
						continue;
					}
					
					// 評価点の求め方
					// なるべく動かず、できるだけ遠くから攻撃する
					int point = moveDist + dist * 2;
					
					if (evalPoint < point) {
						evalPoint = point;
						moveX = x;
						moveY = y;
					}
				}
			}
		}
		
		// 攻撃可能位置に移動できない
		if (evalPoint == Integer.MIN_VALUE) {
			// TODO: 可能な限り近づくとか？
			moveX = enemyMapItem.getMapPointX();
			moveY = enemyMapItem.getMapPointY();
		}
		
		return calcGridPosition(moveX, moveY);
	}
	
	// ----------------------------------------------------------
	// 汎用
	// ----------------------------------------------------------
	
//	public ActorPlayerMapItem getSeqNoToActorMapItem(int seqNo) {
//		List<MapItem> actorList = mMapItemManager.getActorMapItemList();
//		for (MapItem actor : actorList) {
//			if (actor instanceof ActorPlayerMapItem) {
//				if (((ActorPlayerMapItem) actor).getSeqNo() == seqNo) {
//					return (ActorPlayerMapItem) actor;
//				}
//			}
//		}
//		return null;
//	}
	public ActorPlayerMapItem getSeqNoToActorMapItem(int seqNo, MapDataType mapDataType) {
		List<MapItem> actorList = mMapItemManager.getObjectMapItemList(mapDataType);
		for (MapItem actor : actorList) {
			if (actor.getMapDataType() == mapDataType && actor instanceof ActorPlayerMapItem) {
				if (((ActorPlayerMapItem) actor).getSeqNo() == seqNo) {
					return (ActorPlayerMapItem) actor;
				}
			}
		}
		return null;
	}
	public MapItem getMapPointToMapItem(MapPoint mapPoint) {
		return mMapItemManager.getPointItem(mapPoint);
	}
	
	public ActorPlayerMapItem getMapPointToActorPlayer(MapPoint mapPoint) {
		MapItem mapItem = mMapItemManager.getObject(mapPoint);
		if (mapItem != null && 
				(mapItem.getMapDataType() == MapDataType.PLAYER || mapItem.getMapDataType() == MapDataType.ENEMY)) {
			return ((ActorPlayerMapItem) mapItem);
		}
		return null;
	}
	
	public int getMapPointToActorSeqNo(MapPoint mapPoint) {
		ActorPlayerMapItem actorPlayerMapItem = getMapPointToActorPlayer(mapPoint);
		if (actorPlayerMapItem != null) {
			return actorPlayerMapItem.getSeqNo();
		}
		return 0;
	}
	
	private MapPoint getMoveMapPoint(int mapPointX, int mapPointY, MoveDirectionType moveDirectionType) {
		MapPoint mapPoint = calcGridPosition(mapPointX, mapPointY);
		mapPoint.setDirection(moveDirectionType);
		return mapPoint;
	}
	
	public MapPoint calcGridPosition(int mapPointX, int mapPointY) {
		PointF dispPoint = MapGridUtil.indexToDisp(mapPointX, mapPointY);
		return new MapPoint(dispPoint.x, dispPoint.y, mapPointX, mapPointY, mGridSizeX, mGridSizeY, MoveDirectionType.MOVE_DOWN);
	}
	
	public MapPoint calcGridDecodePosition(float x, float y) {
		Point mapPoint = MapGridUtil.dispToIndex(x, y);
		return calcGridPosition(mapPoint.x, mapPoint.y);
	}
	
	/**
	 * マップアイテムからマップ座標情報を取得.
	 * @param mapItem
	 * @return マップ座標情報
	 */
	public MapPoint getMapItemToMapPoint(MapItem mapItem) {
		return calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
	}
}
