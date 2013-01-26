package com.kyokomi.srpgquest.map;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.MapItem;

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
	
	private final GameManager mGameManager;
	private final MapItemManager mMapItemManager;
	private final int mapX;
	private final int mapY;
//	private final float mapScale;
	
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
	public MapManager(GameManager pGameManager, int pMapX, int pMapY, float pMapScale) {
		this.mapX = pMapX;
		this.mapY = pMapY;
		this.RIGHT = mapX;
		this.BOTTOM = mapY;
		
//		this.mapScale = pMapScale;
		this.mGameManager = pGameManager;
		this.mMapItemManager = new MapItemManager(mapX, mapY);		
	}

	/**
	 * プレイヤーキャラ追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	public void addPlayer(int mapPointX, int mapPointY, ActorPlayerDto actorPlayer) {
		addActor(MapDataType.PLAYER, mapPointX, mapPointY, actorPlayer);
	}
	
	/**
	 * エネミーキャラ追加.
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	public void addEnemy(int mapPointX, int mapPointY, ActorPlayerDto actorPlayer) {
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
		
		mMapItemManager.setObject(mapPointX, mapPointY, mapItem);
	}
	
	/**
	 * アクター追加.
	 * @param mapDataType
	 * @param mapPointX
	 * @param mapPointY
	 * @param actorPlayer
	 */
	private void addActor(MapDataType mapDataType, int mapPointX, int mapPointY, ActorPlayerDto actorPlayer) {
		ActorPlayerMapItem playerMapItem = new ActorPlayerMapItem();
		playerMapItem.setPlayerId(actorPlayer.getPlayerId());
		playerMapItem.setAttackDist(actorPlayer.getAttackRange());
		playerMapItem.setMoveDist(actorPlayer.getMovePoint());
		
		playerMapItem.setMapDataType(mapDataType);
		playerMapItem.setMapPointX(mapPointX);
		playerMapItem.setMapPointY(mapPointY);
		
		mMapItemManager.setObject(mapPointX, mapPointY, playerMapItem);
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
		
		// 移動後のマップ情報を更新
		int oldMapPointX = moveToActorPlayer.getMapPointX();
		int oldMapPointY = moveToActorPlayer.getMapPointY();
		mMapItemManager.setObject(oldMapPointX, oldMapPointY, null);
		
		int moveMapPointX = moveFromMapPoint.getMapPointX();
		int moveMapPointY = moveFromMapPoint.getMapPointY();
		mMapItemManager.setObject(moveMapPointX, moveMapPointY, moveToActorPlayer);
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
		// TODO: 現在何もすることがない
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
	
	public int getMapPointToActorPlayerId(MapPoint mapPoint) {
		ActorPlayerMapItem actorPlayerMapItem = getMapPointToActorPlayer(mapPoint);
		if (actorPlayerMapItem != null) {
			return actorPlayerMapItem.getPlayerId();
		}
		return 0;
	}
	
	private MapPoint getMoveMapPoint(int mapPointX, int mapPointY, MoveDirectionType moveDirectionType) {
		MapPoint mapPoint = mGameManager.getTouchMapPointToMapPoint(mapPointX, mapPointY);
		mapPoint.setDirection(moveDirectionType);
		return mapPoint;
	}
}
