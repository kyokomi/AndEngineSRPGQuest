package com.kyokomi.srpgquest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.TiledSprite;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.PlayerTalkDto.TalkDirection;
import com.kyokomi.srpgquest.constant.GameStateType;
import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.constant.SelectMenuType;
import com.kyokomi.srpgquest.logic.BattleLogic;
import com.kyokomi.srpgquest.map.MapManager;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.MapItem;
import com.kyokomi.srpgquest.scene.MapBattleScene;

import android.util.Log;
import android.util.SparseArray;

/**
 * ゲーム全体を管理するクラス.
 * @author kyokomi
 *
 */
public class GameManager {
	private final static String TAG = "GameManager";
	
	private final static float GRID_SIZE = 40;
	
	private MapBattleScene mBaseScene;
	
	private GameStateType mGameState;
	
	private SparseArray<ActorPlayerDto> mPlayerList;
	private SparseArray<ActorPlayerDto> mEnemyList;
	
	/** マップ管理. */
	private MapManager mMapManager;
	
	/** バトル汎用. */
	private BattleLogic mBattleLogic;
	
	/** 選択したプレイヤーテンポラリ. */
	private ActorPlayerMapItem mSelectActorPlayer;
	
	/** 敵のターンタイマー. */
	private TimerHandler mEnemyTurnUpdateHandler;
	
	public TimerHandler getEnemyTurnUpdateHandler() {
		return mEnemyTurnUpdateHandler;
	}
	
	/**
	 * コンストラクタ.
	 * @param mainActivity
	 */
	public GameManager(MapBattleScene baseScene) {
		this.mBaseScene = baseScene;

		// 初期化
		mBattleLogic = new BattleLogic();
		mPlayerList = new SparseArray<ActorPlayerDto>();
		mEnemyList = new SparseArray<ActorPlayerDto>();
		
		// 敵のターンのタイマー制御を生成
		mEnemyTurnUpdateHandler = new TimerHandler(1.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				Log.d(TAG, "mEnemyTurnUpdateHandler " + mGameState);
				if (mGameState == GameStateType.ENEMY_TURN) {
					
					// モンスターのリストをチェックし行動可能なモンスターを探す
					int count = mEnemyList.size();
					for (int i = 0; i < count; i++) {
						ActorPlayerDto enemy = mEnemyList.valueAt(i);
						ActorPlayerMapItem enemyMapItem = mMapManager.getPlayerIdToActorMapItem(
								enemy.getPlayerId(), MapDataType.ENEMY);
						
						Log.d(TAG, "Enemy isAttackDone["+ enemyMapItem.isAttackDone() + "] isMoveDoen["+ enemyMapItem.isMoveDone() + "]");
						
						// 行動させる
						changeGameState(GameStateType.ENEMY_SELECT);
						doEnemyAction(enemy, enemyMapItem);
					}
				}
			}
		});
		
		changeGameState(GameStateType.INIT);
	}
	
	/**
	 * マップ初期化.
	 * @param mapX
	 * @param mapY
	 * @param scale
	 */
	public void mapInit(int mapX, int mapY, float scale) {
		// 初期データ設定(mapX * mapYのグリッドを作成)
		mMapManager = new MapManager(this, mapX, mapY, scale);
		
		// TODO: test用
		int playerId = 1;
		int playerImageId = 110;
		addPlayer(3, 3, playerId, playerImageId);
		
		int enemyId = 2;
		int enemyImageId = 34;
		addEnemy(5, 5, enemyId, enemyImageId);
		
		// 障害物配置
		addObstacle(3, 5);
		addObstacle(6, 9);
		addObstacle(7, 9);
		addObstacle(8, 7);
		addObstacle(8, 8);
		addObstacle(8, 9);
		addObstacle(0, 2);
		addObstacle(1, 2);
		addObstacle(2, 2);
		addObstacle(3, 2);
		addObstacle(4, 2);
		addObstacle(5, 2);
		
		mBaseScene.sortChildren();
	}
	
	/**
	 * ゲーム開始.
	 */
	public void gameStart() {
		changeGameState(GameStateType.START);
	}
	// -----------------------------------------------------
	// 座標計算とか
	// -----------------------------------------------------
	private MapPoint calcGridPosition(int mapPointX, int mapPointY) {
		float x = GRID_SIZE * mapPointX;
		float y = GRID_SIZE * mapPointY;
		return new MapPoint(x, y, mapPointX, mapPointY, GRID_SIZE, MoveDirectionType.MOVE_DOWN);
	}
	private MapPoint calcGridDecodePosition(float x, float y) {
		int mapPointX = (int)(x / GRID_SIZE);
		int mapPointY = (int)(y / GRID_SIZE);
		return calcGridPosition(mapPointX, mapPointY);
	}
	/**
	 * タッチした画面のx,y座標からマップ上のプレイヤーIDを取得.
	 * 何もいない場合などは0を返却
	 * @param x
	 * @param y
	 * @return playerId
	 */
	public int getTouchPositionToPlayerId(float x, float y) {
		MapPoint mapPoint = calcGridDecodePosition(x, y);
		return mMapManager.getMapPointToActorPlayerId(mapPoint);
	}
	/**
	 * タッチした画面のx,y座標からマップ座標情報を取得.
	 * @param x
	 * @param y
	 * @return マップ座標情報
	 */
	public MapPoint getTouchPositionToMapPoint(float x, float y) {
		return calcGridDecodePosition(x, y);
	}
	/**
	 * タッチした画面のx,y座標からマップ座標情報を取得.
	 * @param x
	 * @param y
	 * @return マップ座標情報
	 */
	public MapPoint getTouchMapPointToMapPoint(int mapPointX, int mapPointY) {
		return calcGridPosition(mapPointX, mapPointY);
	}
	
	/**
	 * マップアイテムからマップ座標情報を取得.
	 * @param mapItem
	 * @return マップ座標情報
	 */
	public MapPoint getMapItemToMapPoint(MapItem mapItem) {
		return calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
	}

	/**
	 * 選択中のアクター取得.
	 * @return
	 */
	public ActorPlayerMapItem getSelectActorPlayer() {
		return mSelectActorPlayer;
	}
	
	/**
	 * 行動キャラ選択のタッチ
	 * 移動時のタッチ
	 * 攻撃時のタッチ
	 * 敵のターン時のタッチ
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public MapDataType onTouchMapItemEvent(float x, float y) {
		final MapPoint mapPoint = calcGridDecodePosition(x, y);
		final MapItem mapItem = mMapManager.getMapPointToMapItem(mapPoint);
		MapDataType touchMapDataType;
		if (mapItem == null) {
			touchMapDataType = MapDataType.NONE;
		} else {
			touchMapDataType = mapItem.getMapDataType();
		}
		/* 現在のゲームステータスに応じて処理を行う */
		switch (mGameState) {
		case INIT:
		case START:
			break;
			
		/* プレイヤーのターン */
		case PLAYER_TURN:
						
			// プレイヤーキャラ選択が可能なので行動可能であればウィンドウ表示
			if (touchMapDataType == MapDataType.PLAYER) {
				ActorPlayerMapItem actorPlayerMapItem = (ActorPlayerMapItem) mapItem;
				
				// 敵のステータスは非表示
				mBaseScene.hideEnemyStatusWindow();
				
				// 攻撃もしくは移動が完了していなければ行動可能とする
				if (!actorPlayerMapItem.isWaitDone()) {
					// 行動ウィンドウを表示
					showSelectMenu(actorPlayerMapItem);
				} else {
					// アニメーション停止
					mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
					// プレイヤーのステータスは表示
					mBaseScene.showPlayerStatusWindow(actorPlayerMapItem.getPlayerId());
				}
			} else if (touchMapDataType == MapDataType.ENEMY) {
				ActorPlayerMapItem actorEnemyMapItem = (ActorPlayerMapItem) mapItem;
				// プレイヤーのステータス非表示
				mBaseScene.hidePlayerStatusWindow();
				// 敵のステータス表示
				mBaseScene.showEnemyStatusWindow(actorEnemyMapItem.getPlayerId());
			}
			break;
		
		/* キャラ選択中 */
		case PLAYER_SELECT:
			changeGameState(GameStateType.PLAYER_TURN);
			mSelectActorPlayer = null;
			// 行動ウィンドウ以外を押したら行動ウィンドウを閉じる
			hideSelectMenu();
			break;
			
		/* 攻撃選択中 */
		case PLAYER_ATTACK:
			// 攻撃を選択したときは敵しかタップイベントに反応しない
			if (touchMapDataType == MapDataType.ATTACK_DIST) {
				// 敵が存在するカーソルかチェック
				ActorPlayerMapItem enemy = mMapManager.getMapPointToActorPlayer(mapPoint);
				if (enemy != null) {
					// TODO: [将来対応]攻撃確認ウィンドウ表示				
					
					// ------- 攻撃処理 --------
					boolean isDead = battleStart(mSelectActorPlayer, enemy);
					
					// 攻撃済みにする
					mSelectActorPlayer.setAttackDone(true);
					if (mSelectActorPlayer.isWaitDone()) {
						// アニメーション停止
						mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
					}
					// 攻撃終了後 倒れたアクターをマップ上から削除とかカーソルを初期化など
					mMapManager.attackEndChangeMapItem(mSelectActorPlayer, enemy, isDead);
					
					// プレイヤーターンに戻る
					changeGameState(GameStateType.PLAYER_TURN);
				}
			} else {
				// キャンセル
				showSelectMenu();
			}
			// カーソル消去
			mBaseScene.hideCursorSprite();
			break;
		
		/* 移動選択中 */
		case PLAYER_MOVE:
			// 移動を選択したときは移動可能カーソルにしか反応しない
			if (touchMapDataType == MapDataType.MOVE_DIST) {
				if (mSelectActorPlayer != null) {
					
					// 移動List作成
					List<MapPoint> moveMapPointList = mMapManager.actorPlayerCreateMovePointList(
							mSelectActorPlayer, mapPoint);
					
					// 移動先のカーソルの色を変える
					mBaseScene.selectCursor(mapPoint);
					
					// 移動リストを引数にScene側の移動アニメーションを呼び出す
					mBaseScene.movePlayerAnimation(mSelectActorPlayer.getPlayerId(), moveMapPointList, 
							new MapBattleScene.IAnimationCallback() {
						@Override
						public void doAction() {
							// カーソルを消す
							mBaseScene.hideCursorSprite();
							
							// 移動結果をマップ情報に反映
							// プレイヤーのステータスを移動済みにする
							mMapManager.moveEndChangeMapItem(mSelectActorPlayer, mapPoint);
							// 移動済みに更新
							mSelectActorPlayer.setMoveDone(true);
							
							if (!mSelectActorPlayer.isWaitDone()) {
								// ポップアップ表示
								showSelectMenu();
							} else {
								// アニメーション停止
								mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
								
								changeGameState(GameStateType.PLAYER_TURN);
							}	
						}
					});
				}
			}
			break;
		
		case ENEMY_TURN:
			// 敵のターンは何もできない
			break;
			
		default:
			break;
		}
		
		return touchMapDataType;
	}
	
	//---------------------------------------------------------
	// プレイヤー追加とか
	//---------------------------------------------------------
	private void addPlayer(int mapPointX, int mapPointY, int playerId, int playerImageId) {
		if (mPlayerList.indexOfKey(playerId) >= 0) {
			// すでに追加済み
			return;
		}
 		ActorPlayerDto player = createActorPlayer(playerId, playerImageId);
		mPlayerList.put(playerId, player);
		mMapManager.addPlayer(mapPointX, mapPointY, player);
		// Scene側でSpriteを生成
		mBaseScene.createPlayerSprite(player,
				calcGridPosition(mapPointX, mapPointY));
	}
	private void addEnemy(int mapPointX, int mapPointY, int enemyId, int enemyImageId) {
		if (mEnemyList.indexOfKey(enemyId) >= 0) {
			// すでに追加済み
			return;
		}
		ActorPlayerDto enemy = createActorPlayer(enemyId, enemyImageId);
		mEnemyList.put(enemyId, enemy);
		mMapManager.addEnemy(mapPointX, mapPointY, enemy);
		// Scene側でSpriteを生成
		mBaseScene.createEnemySprite(enemy, 
				calcGridPosition(mapPointX, mapPointY));
	}
	private ActorPlayerDto createActorPlayer(int playerId, int imageResId) {
		ActorPlayerDto actorPlayer = new ActorPlayerDto();
		actorPlayer.setPlayerId(playerId);
		actorPlayer.setImageResId(imageResId);
		
		// TODO: DBとかから取得
		if (playerId == 1) {
			actorPlayer.setName("アスリーン");
		} else {
			actorPlayer.setName("ラーティ・クルス");
		}
		actorPlayer.setLv(1);
		actorPlayer.setExp(10);
		
		actorPlayer.setMovePoint(5);
		actorPlayer.setAttackRange(1);
		
		actorPlayer.setHitPoint(100);
		actorPlayer.setHitPointLimit(100);
		actorPlayer.setAttackPoint(60);
		actorPlayer.setDefencePoint(10);
		
		return actorPlayer;
	}
	private void addObstacle(int mapPointX, int mapPointY) {
		mMapManager.addObstacle(mapPointX, mapPointY);
		mBaseScene.createObstacleSprite(calcGridPosition(mapPointX, mapPointY), 16 * 12 + 0);
	}
	//---------------------------------------------------------
	// カーソル表示関連
	//---------------------------------------------------------
	/**
	 * 移動範囲カーソル表示.
	 * @param mapItem
	 */
	public boolean showMoveDistCursor(MapItem mapItem) {
		MapPoint mapPoint = calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
		return showMoveDistCursor(mapPoint.getX(), mapPoint.getY());
	}
	public boolean showMoveDistCursor(float x, float y) {
		MapPoint mapPoint = calcGridDecodePosition(x, y);
		List<MapItem> mapItems = mMapManager.actorPlayerFindDist(mapPoint);
		if (mapItems == null || mapItems.isEmpty()) {
			Log.d(TAG, "showMoveDistCursor create error");
			return false;
		}
		for (MapItem mapItem : mapItems) {
			MapPoint mapItemPoint = calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
			mBaseScene.createMoveCursorSprite(mapItemPoint);
		}
		mBaseScene.sortChildren();
		return true;
	}
	
	/**
	 * 攻撃範囲カーソル表示.
	 * @param mapItem
	 */
	private void showAttackDistCursor(MapItem mapItem) {
		MapPoint mapPoint = calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
		showAttackDistCursor(mapPoint.getX(), mapPoint.getY());
	}
	public void showAttackDistCursor(float x, float y) {
		MapPoint mapPoint = calcGridDecodePosition(x, y);
		List<MapItem> mapItems = mMapManager.actorPlayerFindAttackDist(mapPoint);
		for (MapItem mapItem : mapItems) {
			MapPoint mapItemPoint = calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
			mBaseScene.createAttackCursorSprite(mapItemPoint);
		}
		mBaseScene.sortChildren();
	}
	
	// ----------------------------------------------------------
	// メニュー関連
	// ----------------------------------------------------------
	/**
	 * メニューボタン選択イベント振り分け.
	 * @param pressedBtnTag
	 */
	public void touchMenuBtnEvent(int pressedBtnTag) {
		
		if (mSelectActorPlayer != null) {
			switch (SelectMenuType.findTag(pressedBtnTag)) {
			case MENU_ATTACK: // 攻撃
				changeGameState(GameStateType.PLAYER_ATTACK);
				showAttackDistCursor(mSelectActorPlayer);
				break;
			case MENU_MOVE: // 移動
				changeGameState(GameStateType.PLAYER_MOVE);
				if (!showMoveDistCursor(mSelectActorPlayer)) {
					changeGameState(GameStateType.PLAYER_TURN);
				}
				break;
			case MENU_WAIT: // 待機
				// 待機状態にする
				mSelectActorPlayer.setWaitDone(true);
				// アニメーション停止
				mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
				
				changeGameState(GameStateType.PLAYER_TURN);
				mBaseScene.hideCursorSprite();
				break;
			case MENU_CANCEL: // キャンセル
				break;
			default:
				break;
			}
		}
		hideSelectMenu();
	}

	/**
	 * 選択メニュー表示.
	 */
	private void showSelectMenu() {
		showSelectMenu(null);
	}
	/**
	 * 選択メニュー表示.
	 */
	private void showSelectMenu(ActorPlayerMapItem pSelectActorPlayer) {
		if (pSelectActorPlayer != null) {
			mSelectActorPlayer = pSelectActorPlayer;
		}
		// TODO: DEBUGログ
		Log.d(TAG, "mSelectActorPlayer[" + mSelectActorPlayer.getPlayerId() + "] isAttackDone=[" + mSelectActorPlayer.isAttackDone() + "]" + 
				"isMoveDone=[" + mSelectActorPlayer.isMoveDone() + "]");
		
		changeGameState(GameStateType.PLAYER_SELECT);
		mBaseScene.showSelectMenu(mSelectActorPlayer.isAttackDone(), mSelectActorPlayer.isMoveDone(), 
				getMapItemToMapPoint(mSelectActorPlayer));
		// プレイヤーのステータスも非表示
		mBaseScene.showPlayerStatusWindow(mSelectActorPlayer.getPlayerId());
	}
	private void hideSelectMenu() {
		mBaseScene.hideSelectMenu();
		// プレイヤーのステータスも非表示
		mBaseScene.hidePlayerStatusWindow();
	}
	
	// ----------------------------------------------------------
	// GameState
	// ----------------------------------------------------------
	/**
	 * ゲームステータス変更.
	 * @param pGameStateType
	 */
	private void changeGameState(GameStateType pGameStateType) {
		Log.d(TAG, "GameState [" + mGameState + "] => [" + pGameStateType + "]");

		// 初期化の場合
		if (pGameStateType == GameStateType.INIT) {
			// 何かステータス関連で初期処理があればここでやる
			mGameState = pGameStateType;
		
		} else if (pGameStateType == GameStateType.ENEMY_TURN) {
			// 敵の勝利判定
			if (!isBeingPlayer()) {
				// 敵勝利
				changeEnemyWin();
				// 敵のターンのタイマーを開始
				mBaseScene.registerUpdateHandler(mEnemyTurnUpdateHandler);
				
			} else {
				// ターン終了判定
				if (mMapManager.checkPlayerTurnEnd(MapDataType.ENEMY)) {
					// プレイヤーターン開始
					changePlayerTurn();
					// タイマー停止
					mBaseScene.unregisterUpdateHandler(mEnemyTurnUpdateHandler);
					Log.d(TAG, "EnemyTurn END");
				} else {
					mGameState = pGameStateType;
				}
			}
			
		} else if (pGameStateType == GameStateType.PLAYER_TURN) {
			
			// プレイヤー勝利判定
			if (!isBeingEnemy()) {
				// プレイヤー勝利
				changePlayerWin();
				
			} else {
				// プレイヤーターンエンド判定
				if (mMapManager.checkPlayerTurnEnd(MapDataType.PLAYER)) {
					// 敵のターン処理を実行
					changeEnemyTurn();
				} else {
					mGameState = pGameStateType;
				}
			}
		} else if (pGameStateType == GameStateType.START) {
			
			// 勝利条件表示
			// TODO: 勝利条件アニメーション
			
			// プレイヤーターン開始
			changePlayerTurn();
			
		} else {
			mGameState = pGameStateType;
		}
	}
	
	private void changePlayerTurn() {
		// プレイヤーターン開始
		mBaseScene.showPlayerTurn(new MapBattleScene.IAnimationCallback() {
			@Override
			public void doAction() {
				// 全プレイヤーを行動可能にしてアニメーションを再開
				mMapManager.refreshAllActorWait(MapDataType.PLAYER);
				int count = mPlayerList.size();
				for (int i = 0; i < count; i++) {
					mBaseScene.startWalkingPlayerAnimation(mPlayerList.valueAt(i).getPlayerId());
				}
				// ターン終了=> プレイヤーターン
				mGameState = GameStateType.PLAYER_TURN;
			}
		});
		// カットイン中に操作させないためにコールバック後に操作可能にする
	}
	private void changeEnemyTurn() {
		// 敵のターンアニメーション
		mBaseScene.showEnemyTurn(new MapBattleScene.IAnimationCallback() {
			@Override
			public void doAction() {
				// 全エネミーを行動可能にする
				mMapManager.refreshAllActorWait(MapDataType.ENEMY);
				int count = mEnemyList.size();
				for (int i = 0; i < count; i++) {
					mBaseScene.startWalkingEnemyAnimation(mEnemyList.valueAt(i).getPlayerId());
				}
				// 敵のターンのタイマーを開始
				mBaseScene.registerUpdateHandler(mEnemyTurnUpdateHandler);
			}
		});
		// カットイン中に操作させないためにコールバックに入れない
		// ターン終了=> 敵のターン
		mGameState = GameStateType.ENEMY_TURN;
	}
	
	private void changePlayerWin() {
		Log.d(TAG, "changeState Player Win");
		// 勝利のカットインを入れてコールバックで次のシナリオへ
		mBaseScene.showPlayerWin(new MapBattleScene.IAnimationCallback() {
			@Override
			public void doAction() {
				// TODO: 一旦タイトル画面に戻る
				mBaseScene.getBaseActivity().backToInitial();
			}
		});
		// カットイン中に操作させないためにコールバックに入れない
		mGameState = GameStateType.END;
	}
	private void changeEnemyWin() {
		Log.d(TAG, "changeState GameOver");
		// 敗北のカットインを入れてコールバックでタイトル画面に戻す
		mBaseScene.showGameOver(new MapBattleScene.IAnimationCallback() {
			@Override
			public void doAction() {
				// タイトル画面に戻る
				mBaseScene.getBaseActivity().backToInitial();
			}
		});
		// カットイン中に操作させないためにコールバックに入れない
		mGameState = GameStateType.END;
	}
	
	// ------------------------------------------------------
	// 敵の行動とか
	// ------------------------------------------------------
	/**
	 * 敵の行動.
	 * @param enemy
	 */
	public void doEnemyAction(ActorPlayerDto enemy, final ActorPlayerMapItem enemyMapItem) {
		
		// 攻撃対象のプレイヤーを探索
		final ActorPlayerMapItem attackTarget = mMapManager.findAttackPlayerMapitem(enemyMapItem);
		if (attackTarget == null) {
			// 攻撃対象がいない
			return;
		}
		
		// 移動検索
		if (!showMoveDistCursor(enemyMapItem)) {
			// 移動カーソル作成失敗
			return ;
		}
		
		// 攻撃対象へ攻撃するための移動先を探索
		final MapPoint enemyMovePoint = mMapManager.findEnemyMoveMapPoint(attackTarget, enemyMapItem);
		// 最初の位置と違う場合、移動する
		if (enemyMovePoint != null && !enemyMovePoint.isMuchMapPoint(getMapItemToMapPoint(enemyMapItem))) {
			
			changeGameState(GameStateType.ENEMY_MOVE);
			
			// 移動List作成
			List<MapPoint> moveMapPointList = mMapManager.actorPlayerCreateMovePointList(
					enemyMapItem, enemyMovePoint);
			if (moveMapPointList != null) {
				// 移動先のカーソルの色を変える
				mBaseScene.selectCursor(enemyMovePoint);
				
				// 移動リストを引数にScene側の移動アニメーションを呼び出す
				mBaseScene.moveEnemyAnimation(enemyMapItem.getPlayerId(), moveMapPointList, 
						new MapBattleScene.IAnimationCallback() {
					@Override
					public void doAction() {
						// カーソルを消す
						mBaseScene.hideCursorSprite();
						
						// 移動結果をマップ情報に反映
						// プレイヤーのステータスを移動済みにする
						mMapManager.moveEndChangeMapItem(enemyMapItem, enemyMovePoint);
						// 移動済みに更新
						enemyMapItem.setMoveDone(true);
						
						// 行動可能であれば攻撃する
						if (!enemyMapItem.isWaitDone()) {
							
							changeGameState(GameStateType.ENEMY_ATTACK);
							
							// 攻撃範囲表示
							showAttackDistCursor(enemyMapItem);
										
							// 攻撃範囲にプレイヤーが存在する場合
							// TODO: 判定仮
							if (true) {
								boolean isDead = battleStart(enemyMapItem, attackTarget);
								// 攻撃済みにする
								enemyMapItem.setAttackDone(true);
								if (enemyMapItem.isWaitDone()) {
									// アニメーション停止
									mBaseScene.stopWalkingEnemyAnimation(enemyMapItem.getPlayerId());
								}
								// 攻撃終了後 倒れたアクターをマップ上から削除とかカーソルを初期化など
								mMapManager.attackEndChangeMapItem(enemyMapItem, attackTarget, isDead);
							}						
						} else {
							// アニメーション停止
							mBaseScene.stopWalkingEnemyAnimation(enemyMapItem.getPlayerId());
						}	

						// 待機
						enemyMapItem.setWaitDone(true);
						changeGameState(GameStateType.ENEMY_TURN);
					}
				}); // コールバックEND
				
			} else {
				// 待機
				enemyMapItem.setWaitDone(true);
				changeGameState(GameStateType.ENEMY_TURN);
			}
		} else {
			// 待機
			enemyMapItem.setWaitDone(true);
			changeGameState(GameStateType.ENEMY_TURN);
		}
	}
	
	private boolean isBeingPlayer() {
		return isBeingActor(mPlayerList);
	}
	private boolean isBeingEnemy() {
		return isBeingActor(mEnemyList);
	}
	private boolean isBeingActor(SparseArray<ActorPlayerDto> actorArray) {
		boolean isBeingActor = false;
		int count = actorArray.size();
		for (int i = 0; i < count; i++) {
			ActorPlayerDto actor = actorArray.valueAt(i);
			if (actor.getHitPoint() > 0) {
				isBeingActor = true;
				break;
			}
		}
		return isBeingActor;
	}
	// ----------------------------------------------------------
	// Battle
	// ----------------------------------------------------------
	/**
	 * バトル開始し、倒したかの結果を返します.
	 * TODO: 反撃できるようにしたら　返り討ちになる場合もあるのでbooleanじゃフラグが足りない
	 * 
	 * @param player
	 * @param enemy 更新しちゃいます (注意)
	 * @return true:倒した / false:倒してない
	 */
	private boolean battleStart(ActorPlayerMapItem fromPlayerMapItem, ActorPlayerMapItem toPlayerMapItem) {
		boolean isDead = false;
		
		ActorPlayerDto formPlayer = getActorMapItemActorPlayer(fromPlayerMapItem);
		ActorPlayerDto toPlayer = getActorMapItemActorPlayer(toPlayerMapItem);

		// バトルロジック実行
		int damage = mBattleLogic.attack(formPlayer, toPlayer);
		// 死亡判定
		if (toPlayer.getHitPoint() <= 0) {
			// 死亡
			isDead = true;
		} else {
			// 生き残り
			isDead = false;
		}
		
		// TODO: [将来対応]キャラが攻撃モーションと敵の方向を向く,敵キャラがダメージモーション
		
		// ダメージを表示
		mBaseScene.showDamageText(damage, getMapItemToMapPoint(toPlayerMapItem));
		// ステータスウィンドウへの反映と死亡時はマップ上から消す
		if (toPlayerMapItem.getMapDataType() == MapDataType.ENEMY) {
			mBaseScene.refreshEnemyStatusWindow(toPlayerMapItem.getPlayerId());
			if (isDead) {
				mBaseScene.removeEnemy(toPlayerMapItem.getPlayerId());
				mEnemyList.remove(toPlayerMapItem.getPlayerId());
			}
		} else if (toPlayerMapItem.getMapDataType() == MapDataType.PLAYER) {
			mBaseScene.refreshPlayerStatusWindow(toPlayerMapItem.getPlayerId());
			if (isDead) {
				mBaseScene.removePlayer(toPlayerMapItem.getPlayerId());
				mPlayerList.remove(toPlayerMapItem.getPlayerId());
			}
		}
		return isDead;
	}
	
	// ----------------------------------------------------------
	// 汎用
	// ----------------------------------------------------------
	private ActorPlayerDto getActorMapItemActorPlayer(ActorPlayerMapItem actorMapItem) {
		ActorPlayerDto player = null;
		switch (actorMapItem.getMapDataType()) {
		case PLAYER:
			player = mPlayerList.get(actorMapItem.getPlayerId());
			break;
		case ENEMY:
			player = mEnemyList.get(actorMapItem.getPlayerId());
			break;
		default:
			break;
		}
		return player;
	}
}
