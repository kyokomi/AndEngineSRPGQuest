package com.kyokomi.srpgquest.manager;

import java.util.List;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.logic.ActorPlayerLogic;
import com.kyokomi.srpgquest.constant.GameStateType;
import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.constant.SelectMenuType;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto.MapSymbol;
import com.kyokomi.srpgquest.layer.MapBattleCutInLayer.MapBattleCutInLayerType;
import com.kyokomi.srpgquest.logic.BattleLogic;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.MapItem;
import com.kyokomi.srpgquest.scene.MainScene;
import com.kyokomi.srpgquest.scene.MainScene;

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
	
	private MapBattleInfoDto mMapBattleInfoDto;
	
	private MainScene mBaseScene;
	
	private GameStateType mGameState;
	
	private SparseArray<ActorPlayerDto> mPlayerList;
	private SparseArray<ActorPlayerDto> mEnemyList;
	
	/** マップ管理. */
	private MapManager mMapManager;
	
	/** バトル汎用. */
	private BattleLogic mBattleLogic;
	
	/** 選択したプレイヤーテンポラリ. */
	private ActorPlayerMapItem mSelectActorPlayer;
	
	// --- logic ----
	private ActorPlayerLogic mActorPlayerLogic;
	
	/** 敵のターンタイマー. */
	private TimerHandler mEnemyTurnUpdateHandler;
	
	public TimerHandler getEnemyTurnUpdateHandler() {
		return mEnemyTurnUpdateHandler;
	}
	
	/**
	 * コンストラクタ.
	 * @param mainActivity
	 */
	public GameManager(MainScene baseScene) {
		this.mBaseScene = baseScene;

		// 初期化
		mBattleLogic = new BattleLogic();
		mActorPlayerLogic = new ActorPlayerLogic();
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
						int seqNo = mEnemyList.keyAt(i);
						ActorPlayerMapItem enemyMapItem = mMapManager.getSeqNoToActorMapItem(
								seqNo, MapDataType.ENEMY);
						
						Log.d(TAG, "Enemy[" + seqNo + "] isAttackDone["+ enemyMapItem.isAttackDone() + "] isMoveDoen["+ enemyMapItem.isMoveDone() + "]");
						if (enemyMapItem.isWaitDone()) {
							continue;
						}
						// 行動させる
						changeGameState(GameStateType.ENEMY_SELECT);
						if (doEnemyAction(enemyMapItem)) {
							// 行動した場合は次のスレッドまで待つ
							break;
						}
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
	public void mapInit(MapBattleInfoDto pMapBattleInfoDto) {
		this.mMapBattleInfoDto = pMapBattleInfoDto;
		
		// 初期データ設定(mapX * mapYのグリッドを作成)
		mMapManager = new MapManager(this, 
				mMapBattleInfoDto.getMapSizeX(), 
				mMapBattleInfoDto.getMapSizeY(), 
				1.0f);
		
		// シンボル作成と配置
		List<MapSymbol> mapSymbolList = mMapBattleInfoDto.getMapSymbolList();
		for (int i = 0; i < mapSymbolList.size(); i++) {
			MapSymbol mapSymbol = mapSymbolList.get(i);
			switch (MapDataType.get(mapSymbol.getType())) {
			case PLAYER:
				addPlayer(mapSymbol);
				break;
			case ENEMY:
				addEnemy(mapSymbol);
				break;
			case MAP_ITEM:
				addObstacle(mapSymbol);
				break;
			default:
				break;
			}
		}
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
	 * @return playerSeqNo
	 */
	public int getTouchPositionToActorSeqNo(float x, float y) {
		MapPoint mapPoint = calcGridDecodePosition(x, y);
		return mMapManager.getMapPointToActorSeqNo(mapPoint);
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
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				ActorPlayerMapItem actorPlayerMapItem = (ActorPlayerMapItem) mapItem;
				
				// 敵のステータスは非表示
				mBaseScene.hideEnemyStatusWindow();
				
				// 攻撃もしくは移動が完了していなければ行動可能とする
				if (!actorPlayerMapItem.isWaitDone()) {
					// 行動ウィンドウを表示
					showSelectMenu(actorPlayerMapItem);
				} else {
					// アニメーション停止
					mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getSeqNo());
					// プレイヤーのステータスは表示
					showPlayerStatusWindow(actorPlayerMapItem);
				}
			} else if (touchMapDataType == MapDataType.ENEMY) {
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				ActorPlayerMapItem actorEnemyMapItem = (ActorPlayerMapItem) mapItem;
				// プレイヤーのステータス非表示
				mBaseScene.hidePlayerStatusWindow();
				// 敵のステータス表示
				mBaseScene.showEnemyStatusWindow(actorEnemyMapItem.getSeqNo());
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
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
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
						mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getSeqNo());
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
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				if (mSelectActorPlayer != null) {
					
					// 移動List作成
					List<MapPoint> moveMapPointList = mMapManager.actorPlayerCreateMovePointList(
							mSelectActorPlayer, mapPoint);
					
					// 移動先のカーソルの色を変える
					mBaseScene.touchedCusorRectangle(mapPoint);
					
					// 移動リストを引数にScene側の移動アニメーションを呼び出す
					mBaseScene.movePlayerAnimation(mSelectActorPlayer.getSeqNo(), moveMapPointList, 
							new MainScene.IAnimationCallback() {
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
								mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getSeqNo());
								
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
	private void addPlayer(MapSymbol mapSymbol) {
		if (mPlayerList.indexOfKey(mapSymbol.getSeqNo()) >= 0) {
			// すでに追加済み
			return;
		}
 		ActorPlayerDto player = mActorPlayerLogic.createActorPlayerDto(mBaseScene, mapSymbol.getId());
		mPlayerList.put(mapSymbol.getSeqNo(), player);
		mMapManager.addPlayer(mapSymbol.getSeqNo(), 
				mapSymbol.getMapPointX(), mapSymbol.getMapPointY(), player);
		// Scene側でSpriteを生成
		mBaseScene.createPlayerSprite(mapSymbol.getSeqNo(), player,
				calcGridPosition(mapSymbol.getMapPointX(), mapSymbol.getMapPointY()), GRID_SIZE);
	}
	private void addEnemy(MapSymbol mapSymbol) {
		if (mEnemyList.indexOfKey(mapSymbol.getSeqNo()) >= 0) {
			// すでに追加済み
			return;
		}
		ActorPlayerDto enemy = mActorPlayerLogic.createActorPlayerDto(mBaseScene, mapSymbol.getId());
		mEnemyList.put(mapSymbol.getSeqNo(), enemy);
		mMapManager.addEnemy(mapSymbol.getSeqNo(), 
				mapSymbol.getMapPointX(), mapSymbol.getMapPointY(), enemy);
		// Scene側でSpriteを生成
		mBaseScene.createEnemySprite(mapSymbol.getSeqNo(), enemy, 
				calcGridPosition(mapSymbol.getMapPointX(), mapSymbol.getMapPointY()), GRID_SIZE);
	}
	
	private void addObstacle(MapSymbol mapSymbol) {
		mMapManager.addObstacle(mapSymbol.getMapPointX(), mapSymbol.getMapPointY());
		mBaseScene.createObstacleSprite(
				calcGridPosition(mapSymbol.getMapPointX(), mapSymbol.getMapPointY()), 
				mapSymbol.getId());
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
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				changeGameState(GameStateType.PLAYER_ATTACK);
				showAttackDistCursor(mSelectActorPlayer);
				break;
			case MENU_MOVE: // 移動
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				changeGameState(GameStateType.PLAYER_MOVE);
				if (!showMoveDistCursor(mSelectActorPlayer)) {
					changeGameState(GameStateType.PLAYER_TURN);
				}
				break;
			case MENU_WAIT: // 待機
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				// 待機状態にする
				mSelectActorPlayer.setWaitDone(true);
				// アニメーション停止
				mBaseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getSeqNo());
				
				changeGameState(GameStateType.PLAYER_TURN);
				mBaseScene.hideCursorSprite();
				break;
			case MENU_CANCEL: // キャンセル
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
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
		Log.d(TAG, "mSelectActorPlayer[" + mSelectActorPlayer.getSeqNo() + "] isAttackDone=[" + mSelectActorPlayer.isAttackDone() + "]" + 
				"isMoveDone=[" + mSelectActorPlayer.isMoveDone() + "]");
		
		changeGameState(GameStateType.PLAYER_SELECT);
		mBaseScene.showSelectMenu(mSelectActorPlayer.isAttackDone(), mSelectActorPlayer.isMoveDone(), 
				getMapItemToMapPoint(mSelectActorPlayer));
		
		// プレイヤーのステータスも非表示
		showPlayerStatusWindow(mSelectActorPlayer);
	}
	private void hideSelectMenu() {
		mBaseScene.hideSelectMenu();
		// プレイヤーのステータスも非表示
		mBaseScene.hidePlayerStatusWindow();
	}
	
	private void showPlayerStatusWindow(ActorPlayerMapItem pSelectActorPlayer) {
		float x = mBaseScene.getWindowWidth() / 2;
		// プレイヤーのステータスも非表示
		mBaseScene.showPlayerStatusWindow(pSelectActorPlayer.getSeqNo(), x);
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
			
			// プレイヤーターン開始
			changePlayerTurn();
			
		} else {
			mGameState = pGameStateType;
		}
	}
	
	private void changePlayerTurn() {
		// プレイヤーターン開始
		mBaseScene.showCutIn(MapBattleCutInLayerType.PLAYER_TURN_CUTIN, 
				new MainScene.IAnimationCallback() {
			@Override
			public void doAction() {
				// 全プレイヤーを行動可能にしてアニメーションを再開
				mMapManager.refreshAllActorWait(MapDataType.PLAYER);
				int count = mPlayerList.size();
				for (int i = 0; i < count; i++) {
					mBaseScene.startWalkingPlayerAnimation(mPlayerList.keyAt(i));
				}
				// ターン終了=> プレイヤーターン
				mGameState = GameStateType.PLAYER_TURN;
			}
		});
		// カットイン中に操作させないためにコールバック後に操作可能にする
	}
	private void changeEnemyTurn() {
		// 敵のターンアニメーション
		mBaseScene.showCutIn(MapBattleCutInLayerType.ENEMY_TURN_CUTIN,
				new MainScene.IAnimationCallback() {
			@Override
			public void doAction() {
				// 全エネミーを行動可能にする
				mMapManager.refreshAllActorWait(MapDataType.ENEMY);
				int count = mEnemyList.size();
				for (int i = 0; i < count; i++) {
					mBaseScene.startWalkingEnemyAnimation(mEnemyList.keyAt(i));
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
		mBaseScene.showCutIn(MapBattleCutInLayerType.PLAYER_WIN_CUTIN, 
				new MainScene.IAnimationCallback() {
			@Override
			public void doAction() {
				mBaseScene.clearMapBattle();
			}
		});
		// カットイン中に操作させないためにコールバックに入れない
		mGameState = GameStateType.END;
	}
	private void changeEnemyWin() {
		Log.d(TAG, "changeState GameOver");
		// 敗北のカットインを入れてコールバックでタイトル画面に戻す
		mBaseScene.showCutIn(MapBattleCutInLayerType.GAME_OVER_CUTIN,
				new MainScene.IAnimationCallback() {
			@Override
			public void doAction() {
				// タイトル画面に戻る
				mBaseScene.gameOverMapBattle();
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
	public boolean doEnemyAction(final ActorPlayerMapItem enemyMapItem) {
		
		// 攻撃対象のプレイヤーを探索
		final ActorPlayerMapItem attackTarget = mMapManager.findAttackPlayerMapitem(enemyMapItem);
		if (attackTarget == null) {
			// 攻撃対象がいない
			return false;
		}
		
		// 移動検索
		if (!showMoveDistCursor(enemyMapItem)) {
			// 移動カーソル作成失敗
			return false;
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
				mBaseScene.touchedCusorRectangle(enemyMovePoint);
				
				// 移動リストを引数にScene側の移動アニメーションを呼び出す
				mBaseScene.moveEnemyAnimation(enemyMapItem.getSeqNo(), moveMapPointList, 
						new MainScene.IAnimationCallback() {
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
									mBaseScene.stopWalkingEnemyAnimation(enemyMapItem.getSeqNo());
								}
								// 攻撃終了後 倒れたアクターをマップ上から削除とかカーソルを初期化など
								mMapManager.attackEndChangeMapItem(enemyMapItem, attackTarget, isDead);
							}						
						} else {
							// アニメーション停止
							mBaseScene.stopWalkingEnemyAnimation(enemyMapItem.getSeqNo());
						}	

						// 待機
						enemyMapItem.setWaitDone(true);
						changeGameState(GameStateType.ENEMY_TURN);
					}
				}); // コールバックEND
				
				return true;
				
			} else {
				// カーソルを消す
				mBaseScene.hideCursorSprite();
				// 待機
				enemyMapItem.setWaitDone(true);
				changeGameState(GameStateType.ENEMY_TURN);
				return false;
			}
		} else {
			// カーソルを消す
			mBaseScene.hideCursorSprite();
			// 待機
			enemyMapItem.setWaitDone(true);
			changeGameState(GameStateType.ENEMY_TURN);
			return false;
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
			mBaseScene.refreshEnemyStatusWindow(toPlayerMapItem.getSeqNo());
			if (isDead) {
				mBaseScene.removeEnemy(toPlayerMapItem.getSeqNo());
				mEnemyList.remove(toPlayerMapItem.getSeqNo());
			}
		} else if (toPlayerMapItem.getMapDataType() == MapDataType.PLAYER) {
			mBaseScene.refreshPlayerStatusWindow(toPlayerMapItem.getSeqNo());
			if (isDead) {
				mBaseScene.removePlayer(toPlayerMapItem.getSeqNo());
				mPlayerList.remove(toPlayerMapItem.getSeqNo());
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
			player = mPlayerList.get(actorMapItem.getSeqNo());
			break;
		case ENEMY:
			player = mEnemyList.get(actorMapItem.getSeqNo());
			break;
		default:
			break;
		}
		return player;
	}
}
