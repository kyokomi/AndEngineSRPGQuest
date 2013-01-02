package com.kyokomi.srpgquest.map;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.actor.ActorPlayer;
import com.kyokomi.srpgquest.actor.CharacterStatus;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.map.common.MapData;
import com.kyokomi.srpgquest.map.common.MapData.MapDataType;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.CharacterSpriteView;
import com.kyokomi.srpgquest.map.item.MapItem;
import com.kyokomi.srpgquest.map.item.MapSpriteView;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.provider.CalendarContract.Instances;
import android.util.Log;
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
	
	private static final int GRID_SIZE = 40;
	private static final int TOP    = 0;
	private static final int LEFT   = 0;
	private final int RIGHT;
	private final int BOTTOM;
	
	private final GameManager mGameManager;
	
	private int mapX;
	private int mapY;
	private float mapScale;
	
	/** マップ位置情報(各ListのIndexId). */
	private MapItem[][] mapItems;
	private MapData[][] mapDatas;
	
	/** マップ移動情報. */
	private List<MapPoint> movePointList;
	
	/** プレイヤーリスト. */
	private List<ActorPlayerMapItem> playerList;
	/** モンスターリスト. */
	private List<ActorPlayerMapItem> enemyList;
	/** 障害物リスト. */
	private List<MapItem> objectList;
	/** カーソルリスト. */
	private List<MapItem> cursorList;
	
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
		
		// 初期化
		mapInit();
		this.playerList = new ArrayList<ActorPlayerMapItem>();
		this.enemyList = new ArrayList<ActorPlayerMapItem>();
		this.objectList = new ArrayList<MapItem>();
	}
	
	/**
	 * マップ初期化.
	 */
	private void mapInit() {
		// 初期化
		this.mapItems = new MapItem[mapX][mapY];
		this.mapDatas = new MapData[mapX][mapY];
		for (int x = 0; x < mapDatas.length; x++) {
			for (int y = 0; y < mapDatas[x].length; y++) {
				this.mapDatas[x][y] = new MapData();
				this.mapDatas[x][y].setDist(0);
				this.mapDatas[x][y].setType(MapDataType.NONE);
			}
		}
		this.cursorList = new ArrayList<MapItem>();
	}
	
	/**
	 * カーソル情報初期化.
	 */
	private void cursorInit() {
		// カーソルだけ削除
		for (int x = 0; x < mapDatas.length; x++) {
			for (int y = 0; y < mapDatas[x].length; y++) {
				if (mapDatas[x][y].getType() == MapDataType.MOVE_DIST || 
						mapDatas[x][y].getType() == MapDataType.ATTACK_DIST) {
					mapDatas[x][y] = new MapData();
				}
			}
		}
		cursorList = new ArrayList<MapItem>();
	}
	
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
		
		mapItems[mapPointX][mapPointY] = mapItem;
		mapDatas[mapPointX][mapPointY].setType(mapItem.getMapDataType());
		
		// リストに追加
		switch (mapItem.getMapDataType()) {
		case PLAYER:
			playerList.add((ActorPlayerMapItem)mapItem);
			mapDatas[mapPointX][mapPointY].setDist(mapItem.getMoveDist());
			break;
		case ENEMY:
			enemyList.add((ActorPlayerMapItem)mapItem);
			break;
		case MAP_ITEM:
			objectList.add(mapItem);
			break;	
		default:
			// 対象外は親ビューに追加しない
			return;
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
		if (mapDatas[mapPointX][mapPointY].getType() != MapDataType.NONE && 
				mapDatas[mapPointX][mapPointY].getType() != MapDataType.ENEMY ) {
			return;
		}
		// TODO: カーソル・アイコン追加
//		addCursor(R.drawable.window_c_00, MapDataType.ATTACK_DIST,
//				mapPointX, mapPointY, dist, new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				if (mGameManager.touchedAttackCursor(v)) {
//					// タップしたマップ情報を取得
//					final int x = ((MapSpriteView) v).getMapPointX();
//					final int y = ((MapSpriteView) v).getMapPointY();
//					
//					// 攻撃位置に攻撃対象が存在する場合
//					if (mapDatas[x][y].getType() == MapDataType.ENEMY) {
//						// TODO: デュエルスタート
//						
//					} else {
//						// TODO: 攻撃対象がいない
//					}
//					
//					// カーソル情報クリア
//					cursorInit(selectMapItem);
////				}
//			}
//		});
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
		if (mapDatas[mapPointX][mapPointY].getType() != MapDataType.NONE) {
			return;
		}
//		addCursor(R.drawable.window_b_00, MapDataType.MOVE_DIST, 
//				mapPointX, mapPointY, dist, new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (mGameManager.touchedMoveCursor(v)) {
//					moveMapItem((MapSpriteView)v, selectMapItem);
//				}
//			}
//		});
	}
	
	
//	public boolean showCharcterDist() {
//		boolean isAction = false;
//		if (!selectMapItem.isMoveDone()) {
//			charcterFindDist(selectMapItem);
//			isAction = true;
//		}
//		return isAction;
//	}
//	public boolean showCharcterAttack() {
//		boolean isAction = false;
//		if (!selectMapItem.isAttackDone()) {
//			charcterFindAttack(selectMapItem);	
//			isAction = true;
//		}
//		return isAction;
//	}
//	public void characterEnd() {
//		selectMapItem.setAttackDone(true);
//		selectMapItem.setMoveDone(true);
//		mGameManager.actionWait(selectMapItem.getMapDataType());
//	}
//	public void selectCancel() {
//		this.selectMapItem = null;
//		mGameManager.actionCancel();
//	}
//	
//	/**
//	 * カーソル追加.
//	 * @param mapPointX
//	 * @param mapPointY
//	 * @param dist
//	 */
//	private void addCursor(int resId, MapDataType mapDataType, int mapPointX, int mapPointY, int dist, OnClickListener clickListener) {
//
//		// 画像読み込み
//		MapSpriteView mapSpriteView  = new MapSpriteView(mGameManager.getActivity(), mapDataType, 
//				resId, 4, 4, mapPointX, mapPointY, getGridSize(), mapScale);
//		mapSpriteView.setSprite(2, 2);
//		mapSpriteView.setAlpha(0.8f);
//		
//		// clickイベント登録
//		mapSpriteView.setOnClickListener(clickListener);
//		
//		// 描画設定
//		mGameManager.addMapItem(mapSpriteView);
//		// リストに追加
//		cursorList.add(mapSpriteView);
//	}
//	
//	/**
//	 * キャラクター移動範囲検索.
//	 * @param characterView キャラクタービュー
//	 */
//	private void charcterFindDist(CharacterSpriteView characterView) {
//		// キャラクターの現在位置を取得
//		int mapX = characterView.getMapPointX();
//		int mapY = characterView.getMapPointY();
//		int dist = characterView.getMoveDist();
//		MapDataType mapDataType = characterView.getMapDataType();
//		
//		// 移動検索
//		mapDatas[mapX][mapY].setDist(dist);
//		mapDatas[mapX][mapY].setType(mapDataType);
//		// 検索開始(再帰呼び出し)
//		findDist(mapX, mapY, dist, true);
//		
//		// デバッグ用（端末を横にした時のイメージ）----------------
//		for (int i = 0; i < mapDatas.length; i++) {
//			String str = "";
//			for (int j = 0; j < mapDatas[i].length; j++) {
//				str += " " + this.mapDatas[i][j].getDist();  
//			}
//			Log.d("TEST", str);
//		}
//		// -----------------------------------------------
//	}
//	
//	/**
//	 * キャラクター攻撃範囲検索.
//	 * @param characterView キャラクタービュー
//	 */
//	private void charcterFindAttack(CharacterSpriteView characterView) {
//		// キャラクターの現在位置を元に短形グリッド座標を計算
//		int x = characterView.getMapPointX();
//		int y = characterView.getMapPointY();
//		int dist = characterView.getAttackDist();
//		MapDataType mapDataType = characterView.getMapDataType();
//		
//		// 移動検索
//		mapDatas[x][y].setDist(dist);
//		mapDatas[x][y].setType(mapDataType);
//		// 検索開始(再帰呼び出し)
//		findAttack(x, y, dist, true);
//		
//		// デバッグ用（端末を横にした時のイメージ）----------------
//		for (int i = 0; i < mapDatas.length; i++) {
//			String str = "";
//			for (int j = 0; j < mapDatas[i].length; j++) {
//				str += " " + this.mapDatas[i][j].getDist();  
//			}
//			Log.d("TEST", str);
//		}
//		// -----------------------------------------------
//	}
//	
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
		}
		mapDatas[x][y].setDist(dist);
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
	 * 移動範囲検索.
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
		mapDatas[x][y].setDist(dist);
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
	 * マップアイテムの移動.
	 * @param moveToMapItem 移動先
	 * @param player 移動対象
	 */
	private void moveMapItem(int mapPointX, int mapPointY, ActorPlayerMapItem moveToActorMapItem) {
	    // 移動情報作成
		createMovePointList(mapPointX, mapPointY, mapDatas[mapPointX][mapPointY].getDist(), moveToActorMapItem);	
		// 目的地を最後の移動箇所に指定(方向は直前のマスと同じ) TODO: おかしい？
		movePointList.add(new MapPoint(mapPointX, mapPointY, movePointList.get(movePointList.size()-1).getDirection()));

		// 移動アニメーション
		// TODO: GameManager側に通知
	}
	/**
	 * マップアイテム移動後の処理.
	 * @param moveToMapItem
	 * @param moveToView
	 */
	private void moveMapItemEnd(int mapPointX, int mapPointY, ActorPlayerMapItem moveToActorMapItem) {
		// 移動完了後のマップ情報更新
		moveEndChangeMapItem(mapPointX, mapPointY, moveToActorMapItem);
		// 移動済みにする
		moveToActorMapItem.setMoveDone(true);
		// 移動完了呼び出し
		// TODO: GameManager側？
	}
	
	/**
	 * 移動後マップ情報変更.
	 * @param mapPointX
	 * @param mapPointY
	 * @param moveToView
	 */
	private void moveEndChangeMapItem(int mapPointX, int mapPointY, MapItem moveToMapItem) {
		// リストとかカーソルまわりの情報を全部クリア
		movePointList = new ArrayList<MapPoint>();
		cursorInit();
		
		// 移動後のマップ情報を更新
		int oldX = moveToMapItem.getMapPointX();
		int oldY = moveToMapItem.getMapPointY();
		mapItems[oldX][oldY] = null;
		mapDatas[oldX][oldY] = new MapData();
		
		mapItems[mapPointX][mapPointY] = moveToMapItem;
		mapDatas[mapPointX][mapPointY].setType(moveToMapItem.getMapDataType());
		mapDatas[mapPointX][mapPointY].setDist(moveToMapItem.getMoveDist());
		moveToMapItem.setMapPointX(mapPointX);
		moveToMapItem.setMapPointY(mapPointY);
	}
//	
//	/**
//	 * 移動アニメーション.
//	 * @param mapView
//	 */
//	private Animator moveAnimation(MapSpriteView mapView, AnimatorListener animatorListener) {
//		
//		// 移動先の座標を逆算して求める
//		int size = movePointList.size();
//		float moveX[] = new float[size];
//		float moveY[] = new float[size];
//		int moveD[] = new int[size];
//		for (int i = 0; i < size; i++) {
//			MapPoint point = movePointList.get(i);
//			moveX[i] = point.getX() * getGridSize();
//			moveY[i] = point.getY() * getGridSize();
//			moveD[i] = point.getDirection().getValue();
//		}
//		
//		// 事前処理で移動方向を向いておく
//		mapView.setDirection(moveD[0]);
//		
//		// アニメーション
//		ObjectAnimator animX = ObjectAnimator.ofFloat(mapView, "x", moveX);
//		ObjectAnimator animY = ObjectAnimator.ofFloat(mapView, "y", moveY);
//		ObjectAnimator animD = ObjectAnimator.ofInt(mapView, "direction", moveD);
//		AnimatorSet animatorSet = new AnimatorSet();
//		animatorSet.setDuration(500);
//		if (animatorListener != null) {
//			animatorSet.addListener(animatorListener);
//		}
//		// 開始
//		animatorSet.play(animY).with(animX).with(animD);
//		animatorSet.start();
//		
//		return animatorSet;
//	}
//	
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
		if (mapItems[x][y] != moveMapItem) {
			// タップした位置のdistの次はどこか探す
			dist++;
			
			// 上か？
			if (y > TOP && mapDatas[x][y - 1].getDist() == dist 
					&& mapDatas[x][y - 1].getType() != ignoreDataType) {
				createMovePointList(x, y-1, dist, moveMapItem);
				movePointList.add(new MapPoint(x, y-1, MoveDirectionType.MOVE_DOWN));
			}
			// 下か？
			else if (y < (BOTTOM -1) && mapDatas[x][y + 1].getDist() == dist
					&& mapDatas[x][y + 1].getType() != ignoreDataType) {
				createMovePointList(x, y+1, dist, moveMapItem);
				movePointList.add(new MapPoint(x, y+1, MoveDirectionType.MOVE_UP));
			}	
			// 右か?
			else if (x > LEFT && mapDatas[x - 1][y].getDist() == dist
					&& mapDatas[x - 1][y].getType() != ignoreDataType) {
				createMovePointList(x-1, y, dist, moveMapItem);
				movePointList.add(new MapPoint(x-1, y, MoveDirectionType.MOVE_RIGHT));
			}
			// 左にいけるか?
			else if (x < (RIGHT - 1) && mapDatas[x + 1][y].getDist() == dist
					&& mapDatas[x + 1][y].getType() != ignoreDataType) {
				createMovePointList(x+1, y, dist, moveMapItem);
				movePointList.add(new MapPoint(x+1, y, MoveDirectionType.MOVE_LEFT));
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
}