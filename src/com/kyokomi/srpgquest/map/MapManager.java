package com.kyokomi.srpgquest.map;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.actor.ActorPlayer;
import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.map.common.MapData;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.MapItem;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.provider.CalendarContract.Instances;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.view.View.OnClickListener;

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
	
	private final GameManager mGameManager;
	private final MapItemManager mMapItemManager;
	
	private int mapX;
	private int mapY;
	private float mapScale;
	
	/** マップ位置情報(各ListのIndexId). */
//	private MapItem[][] mapItems; // TODO: もしかしてイラン子？MapDataあるし
	private MapData[][] mapDatas;
	
	/** マップ移動情報. */
	private List<MapPoint> movePointList;
	
	/**
	 * TODO: test用
	 */
	public void debugShowMapDatas() {
		Log.d(TAG, "====== debugShowMapDatas ======");
		StringBuffer buffer = null;
		for (int k = 0; k < mapDatas[0].length; k++) {
			buffer = new StringBuffer();
			for (int i = 0; i < mapDatas.length; i++) {
				buffer.append(mapDatas[i][k].getType().getValue());
				buffer.append(".");
			}			
			Log.d(TAG, buffer.toString());
		}
	}
	
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
	public MapManager(GameManager gameManager, int mapX, int mapY, float mapScale) {
		this.mapX = mapX;
		this.mapY = mapY;
		this.RIGHT = mapX;
		this.BOTTOM = mapY;
		
		this.mapScale = mapScale;
		this.mGameManager = gameManager;
		this.mMapItemManager = new MapItemManager(mapX, mapY);
		
		// 初期化
		mapInit();		
	}
	
	/**
	 * マップ初期化.
	 */
	private void mapInit() {
		this.mapDatas = new MapData[mapX][mapY];
		for (int x = 0; x < mapDatas.length; x++) {
			for (int y = 0; y < mapDatas[x].length; y++) {
				this.mapDatas[x][y] = new MapData();
				this.mapDatas[x][y].setDist(0);
				this.mapDatas[x][y].setType(MapDataType.NONE);
			}
		}
	}
	
	/**
	 * カーソル情報初期化.
	 */
	private void cursorMapDataInit() {
		// カーソルだけ削除
		for (int x = 0; x < mapDatas.length; x++) {
			for (int y = 0; y < mapDatas[x].length; y++) {
				if (mapDatas[x][y].getType() == MapDataType.MOVE_DIST || 
						mapDatas[x][y].getType() == MapDataType.ATTACK_DIST) {
					mapDatas[x][y] = new MapData();
				}
			}
		}
	}
	
//	/**
//	 * マップアイテムからカーソル情報の初期化
//	 */
//	private void cursorMapItemInit() {
//		for (int x = 0; x < mapItems.length; x++) {
//			for (int y = 0; y < mapItems[x].length; y++) {
//				if (getMapItem(x, y) != null && getMapItem(x, y).getMapDataType() == MapDataType.MOVE_DIST) {
//					setMapItem(x, y, null);
//				}
//			}	
//		}
//	}
	
//	/**
//	 * カーソルリスト作成.
//	 * @return
//	 */
//	private void createCursorList(MapDataType mapDataType) {
//		cursorList = new ArrayList<MapItem>();
//		for (int x = 0; x < mapItems.length; x++) {
//			for (int y = 0; y < mapItems[x].length; y++) {
//				if (getMapItem(x,y) != null && getMapItem(x,y).getMapDataType() == mapDataType) {
//					cursorList.add(getMapItem(x,y));
//				}
//			}	
//		}
//	}
	
	/**
	 * プレイヤーキャラ追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	public void addPlayer(int mapPointX, int mapPointY, ActorPlayer actorPlayer) {
		addActor(MapDataType.PLAYER, mapPointX, mapPointY, actorPlayer);
	}
	
	/**
	 * エネミーキャラ追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	public void addEnemy(int mapPointX, int mapPointY, ActorPlayer actorPlayer) {
		addActor(MapDataType.ENEMY, mapPointX, mapPointY, actorPlayer);
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
		
		addMapItem(mapItem);
	}
	
	/**
	 * アクター追加.
	 * @param mapDataType
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	private void addActor(MapDataType mapDataType, int mapPointX, int mapPointY, ActorPlayer actorPlayer) {
		ActorPlayerMapItem playerMapItem = new ActorPlayerMapItem();
		playerMapItem.setPlayerId(actorPlayer.getPlayerId());
		playerMapItem.setAttackDist(actorPlayer.getAttackRange());
		playerMapItem.setMoveDist(actorPlayer.getMovePoint());
		
		playerMapItem.setMapDataType(mapDataType);
		playerMapItem.setMapPointX(mapPointX);
		playerMapItem.setMapPointY(mapPointY);
		
		addMapItem(playerMapItem);
	}
	
	/**
	 * マップアイテム追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param mapView
	 */
	private void addMapItem(MapItem mapItem) {
		int mapPointX = mapItem.getMapPointX();
		int mapPointY = mapItem.getMapPointY();
		
		mMapItemManager.setObject(mapPointX, mapPointY, mapItem);
		// TODO: いらないかも？
		mapDatas[mapPointX][mapPointY].setType(mapItem.getMapDataType());
		
		// リストに追加
		switch (mapItem.getMapDataType()) {
		case PLAYER:
			mapDatas[mapPointX][mapPointY].setDist(mapItem.getMoveDist());
			break;
		case ENEMY:
			break;
		case MAP_ITEM:
			break;	
		default:
			return;
		}
	}
	
//	TODO: バトルはあとで
//	/**
//	 * バトル実行.
//	 * @param from
//	 * @param to
//	 */
//	private void battle(final CharacterSpriteView from, final CharacterSpriteView to) {
//		
//		// 攻撃処理アニメーション作成
//		ObjectAnimator anim;
//		
//		// ゲーム管理側でバトル計算
//		boolean isBattle = mGameManager.battleStart(from, to);
//		if (isBattle) {
//			// 消える
//			anim = ObjectAnimator.ofFloat(to, "alpha", 0.0f, 1.0f, 0.0f, 0.0f);
//			
//			anim.addListener(new AnimatorListener() {
//				@Override public void onAnimationStart(Animator animation) {}
//				@Override public void onAnimationRepeat(Animator animation) {}
//				@Override public void onAnimationEnd(Animator animation) {
//					int x = to.getMapPointX();
//					int y = to.getMapPointY();
//					mGameManager.removeMapItem(to);
//					mapViews[x][y] = null;
//					mapDatas[x][y] = new MapData();
//					enemyList.remove(to);
//					
//					mGameManager.battleEnd(from.getMapDataType());
//				}
//				@Override public void onAnimationCancel(Animator animation) {}
//			});
//			
//		} else {
//			anim = ObjectAnimator.ofFloat(to, "alpha", 0.0f, 1.0f, 0.0f, 1.0f);
//			anim.addListener(new AnimatorListener() {
//				@Override public void onAnimationStart(Animator animation) {}
//				@Override public void onAnimationRepeat(Animator animation) {}
//				@Override public void onAnimationEnd(Animator animation) {
//					mGameManager.battleEnd(from.getMapDataType());
//				}
//				@Override public void onAnimationCancel(Animator animation) {}
//			});
//		}
//		anim.setDuration(400);
//		anim.start();
//		
//		// 攻撃済みにする
//		from.setAttackDone(true);
//	}
//	
	/**
	 * 移動カーソル追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param dist
	 */
	public void addDistCursor(int mapPointX, int mapPointY, int dist) {
		// 未設定 or 移動オブジェクトで移動力が上の場合
		if (mapDatas[mapPointX][mapPointY].getType() == MapDataType.NONE ||
				mapDatas[mapPointX][mapPointY].getType() == MapDataType.MOVE_DIST &&
				mapDatas[mapPointX][mapPointY].getDist() < dist) {
			
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
		if (mapDatas[mapPointX][mapPointY].getType() == MapDataType.NONE || 
				mapDatas[mapPointX][mapPointY].getType() == MapDataType.ENEMY ) {
			
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
		MapDataType mapDataType = actorItem.getMapDataType();
		
		// 移動検索
		mapDatas[mapX][mapY].setDist(dist);
		mapDatas[mapX][mapY].setType(mapDataType);
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
		
	    // 移動情報作成
		movePointList = new ArrayList<MapPoint>();
		createMovePointList(moveFromMapPoint.getMapPointX(), moveFromMapPoint.getMapPointY(), 
				moveFromMapItem.getMoveDist(), moveToActorPlayer);
		// 目的地を最後の移動箇所に指定
		movePointList.add(moveFromMapPoint);
		
		debugShowMoveList(); // DEBUG

		// カーソル情報をクリア
		mMapItemManager.clearCursorMapItemLayer();

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
		// 移動済みに更新（TODO: 参照されてる？）
		moveToActorPlayer.setMoveDone(true);
		
		// リストとかカーソルまわりの情報を全部クリア
		movePointList = new ArrayList<MapPoint>();
		cursorMapDataInit();
		
		// 移動後のマップ情報を更新
		int oldMapPointX = moveToActorPlayer.getMapPointX();
		int oldMapPointY = moveToActorPlayer.getMapPointY();
		mMapItemManager.setObject(oldMapPointX, oldMapPointY, null);
		// TODO: いらんこ？
		mapDatas[oldMapPointX][oldMapPointY] = new MapData();
		
		int moveMapPointX = moveFromMapPoint.getMapPointX();
		int moveMapPointY = moveFromMapPoint.getMapPointY();
		mMapItemManager.setObject(moveMapPointX, moveMapPointY, moveToActorPlayer);
		// TODO: いらんこ？
		mapDatas[moveMapPointX][moveMapPointY].setType(moveToActorPlayer.getMapDataType());
		mapDatas[moveMapPointX][moveMapPointY].setDist(moveToActorPlayer.getMoveDist());
		moveToActorPlayer.setMapPointX(moveMapPointX);
		moveToActorPlayer.setMapPointY(moveMapPointY);
	}
	
	/**
	 * 移動後マップ情報変更.
	 * @param mapPointX
	 * @param mapPointY
	 * @param moveToView
	 */
	public void attackEndChangeMapItem() {
//		// 移動済みに更新（TODO: 参照されてる？）
//		moveToActorPlayer.setMoveDone(true);
		
		// リストとかカーソルまわりの情報を全部クリア
		cursorMapDataInit();
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
		MapDataType mapDataType = actorPlayerMapItem.getMapDataType();
		
		// 移動検索
		mapDatas[x][y].setDist(dist);
		mapDatas[x][y].setType(mapDataType);
		
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
			// 移動情報初期化
			movePointList = new ArrayList<MapPoint>();
			
			// 移動可能範囲に追加
			addDistCursor(x, y, dist);
			mapDatas[x][y].setType(MapDataType.MOVE_DIST);
			mapDatas[x][y].setDist(dist);
		}
		if (dist == 0) {
			return;
		}

		// 上にいけるか?
		if (y > TOP && mapDatas[x][y - 1].chkMove(dist)) {
			findDist(x, y - 1, dist - 1, false);
		}
		// 下にいけるか?
		if (y < (BOTTOM -1) && mapDatas[x][y + 1].chkMove(dist)) {
			findDist(x, y + 1, dist - 1, false);
		}	
		// 右にいけるか?
		if (x > LEFT && mapDatas[x - 1][y].chkMove(dist)) {
			findDist(x - 1, y, dist - 1, false);
		}
		// 左にいけるか?
		if (x < (RIGHT - 1) && mapDatas[x + 1][y].chkMove(dist)) {
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
			mapDatas[x][y].setDist(dist);
			mapDatas[x][y].setType(MapDataType.ATTACK_DIST);
		}
		if (dist == 0) {
			return;
		}

		// 上にいけるか?
		if (y > TOP && mapDatas[x][y - 1].chkAttack(dist)) {
			findAttack(x, y - 1, dist - 1, false);
		}
		// 下にいけるか?
		if (y < (BOTTOM -1) && mapDatas[x][y + 1].chkAttack(dist)) {
			findAttack(x, y + 1, dist - 1, false);
		}	
		// 右にいけるか?
		if (x > LEFT && mapDatas[x - 1][y].chkAttack(dist)) {
			findAttack(x - 1, y, dist - 1, false);
		}
		// 左にいけるか?
		if (x < (RIGHT - 1) && mapDatas[x + 1][y].chkAttack(dist)) {
			findAttack(x + 1, y, dist - 1, false);
		}
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
			
			// 上か？
			if (y > TOP && mapDatas[x][y - 1].getDist() == dist 
					&& mapDatas[x][y - 1].getType() != ignoreDataType) {
				createMovePointList(x, y-1, dist, moveMapItem);
				movePointList.add(mGameManager.getTouchMapPointToMapPoint(x, y-1));
			}
			// 下か？
			else if (y < (BOTTOM -1) && mapDatas[x][y + 1].getDist() == dist
					&& mapDatas[x][y + 1].getType() != ignoreDataType) {
				createMovePointList(x, y+1, dist, moveMapItem);
				movePointList.add(mGameManager.getTouchMapPointToMapPoint(x, y+1));
			}	
			// 右か?
			else if (x > LEFT && mapDatas[x - 1][y].getDist() == dist
					&& mapDatas[x - 1][y].getType() != ignoreDataType) {
				createMovePointList(x-1, y, dist, moveMapItem);
				movePointList.add(mGameManager.getTouchMapPointToMapPoint(x-1, y));
			}
			// 左にいけるか?
			else if (x < (RIGHT - 1) && mapDatas[x + 1][y].getDist() == dist
					&& mapDatas[x + 1][y].getType() != ignoreDataType) {
				createMovePointList(x+1, y, dist, moveMapItem);
				movePointList.add(mGameManager.getTouchMapPointToMapPoint(x+1, y));
			}
		}
	}
//	
//	/**
//	 * 敵の行動.
//	 * @param enemy
//	 */
//	public void enemyMove(CharacterSpriteView enemy) {
//		CharacterSpriteView attackTarget = null;
//		
//		// 評価ポイント
//		int evalPoint = Integer.MAX_VALUE;
//		
//		// 障害物を無視して距離を求める
//		// 最も直線距離の近い相手を攻撃目標にする
//		for (CharacterSpriteView player : playerList) {
//			int dist = Math.abs(enemy.getMapPointX() - player.getMapPointX()) + 
//					Math.abs(enemy.getMapPointY() - player.getMapPointY());
//			if (evalPoint > dist) {
//				attackTarget = player;
//				evalPoint = dist;
//			}
//		}
//		// 移動検索
//		charcterFindDist(enemy);
//		
//		evalPoint = Integer.MIN_VALUE;
//		int moveX = enemy.getMapPointX();
//		int moveY = enemy.getMapPointY();
//		
//		int tageX = attackTarget.getMapPointX();
//		int tageY = attackTarget.getMapPointY();
//		
//		for (int y = TOP; y < BOTTOM; y++) {
//			for (int x = LEFT; x < RIGHT; x++) {
//				// 攻撃距離計算
//				int dist = Math.abs(x - tageX) + Math.abs(y - tageY);
//				// 攻撃範囲内
//				if (dist <= enemy.getAttackDist()) {
//					int moveDist = 0;
//					if (mapDatas[x][y].getType() == MapDataType.MOVE_DIST) {
//						// 移動可能範囲
//						moveDist = mapDatas[x][y].getDist();
//					} else if (mapViews[x][y] == enemy) {
//						continue;
//					}
//					
//					// 評価点の求め方
//					// なるべく動かず、できるだけ遠くから攻撃する
//					int point = moveDist + dist * 2;
//					
//					if (evalPoint < point) {
//						evalPoint = point;
//						moveX = x;
//						moveY = y;
//					}
//				}
//			}
//		}
//		
//		// 攻撃可能位置に移動できない
//		if (evalPoint == Integer.MIN_VALUE) {
//			// TODO: 可能な限り近づくとか？
//			attackTarget = null;
//			moveX = enemy.getMapPointX();
//			moveY = enemy.getMapPointY();
//		}
//		
//		boolean isNotMove = true;
//		
//		// 最初の位置と違う場合、移動する
//		if (moveX == enemy.getMapPointX() && moveY == enemy.getMapPointY()) {
//			
//		} else {
//			// TODO: cursorはmapViewの中にないのでリストから探す
//			for (MapSpriteView cursor : cursorList) {
//				if (cursor.getMapPointX() == moveX && cursor.getMapPointY() == moveY) {
//					
//					// 移動
//					moveEnemy(cursor, enemy, attackTarget);
//					
//					isNotMove = false;
//					break;
//				}
//			}
//		}
//		
//		if (isNotMove) {
//			// カーソル情報をクリア
//			cursorInit(enemy);
//			
//			enemy.setAttackDone(true);
//			enemy.setMoveDone(true);
//			mGameManager.actionWait(enemy.getMapDataType());
//		}
//	}
//	
//	private void moveEnemy(final MapSpriteView moveToMapItem, final CharacterSpriteView moveToView, 
//			final CharacterSpriteView attackTarget) {
//		moveMapItem(moveToMapItem, moveToView, new AnimatorListener() {
//			@Override public void onAnimationStart(Animator animation) {}
//			@Override public void onAnimationRepeat(Animator animation) {}
//			@Override public void onAnimationCancel(Animator animation) {}
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				moveMapItemEnd(moveToMapItem, moveToView);
//				
//				// 攻撃判定
//				charcterFindAttack(moveToView);
//				
//				// デュエルスタート！
//				CharacterSpriteView from = moveToView;
//				CharacterSpriteView to = attackTarget;
//				battle(from, to);
//			
//				// カーソル情報クリア
//				cursorInit(moveToView);
//			}
//		});
//	}
	
	// ----------------------------------------------------------
	// 汎用
	// ----------------------------------------------------------
	
	public MapItem getMapPointToMapItem(MapPoint mapPoint) {
		MapItem mapItem = mMapItemManager.getCursor(mapPoint);
		if (mapItem == null) {
			mapItem = mMapItemManager.getObject(mapPoint);
			if (mapItem == null) {
				mapItem = mMapItemManager.getBase(mapPoint);
				if (mapItem == null) {
					return null;
				}
			}
		}
		return mapItem;
	}
	
	public ActorPlayerMapItem getMapPointToActorPlayer(MapPoint mapPoint) {
		MapItem mapItem = mMapItemManager.getObject(mapPoint);
		if (mapItem != null && 
				(mapItem.getMapDataType() == MapDataType.PLAYER || mapItem.getMapDataType() == MapDataType.ENEMY)) {
			return ((ActorPlayerMapItem) mapItem);
		}
		return null;
	}
	
	public int getMapPointToActorPlayerId(MapPoint mapPoint) {
		ActorPlayerMapItem actorPlayerMapItem = getMapPointToActorPlayer(mapPoint);
		if (actorPlayerMapItem != null) {
			return actorPlayerMapItem.getPlayerId();
		}
		return 0;
	}
	
//	private MapItem getMapItem(int pointX, int pointY) {
//		if (mapItems.length <= pointX || mapItems[0].length <= pointY) {
//			// 範囲外
//			return null;
//		}
//		return mapItems[pointX][pointY];
//	}
//	
//	private void setMapItem(int pointX, int pointY, MapItem mapItem) {
//		if (mapItems.length <= pointX || mapItems[0].length <= pointY) {
//			// 範囲外
//			return;
//		}
//		mapItems[pointX][pointY] = mapItem;
//	}
//	
//	private MapItem getAttackDistMapItem(int pointX, int pointY) {
//		if (attackDistMapItems.length <= pointX || attackDistMapItems[0].length <= pointY) {
//			// 範囲外
//			return null;
//		}
//		return attackDistMapItems[pointX][pointY];
//	}
}
