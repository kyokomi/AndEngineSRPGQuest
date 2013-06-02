package com.kyokomi.srpgquest.manager;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.srpgquest.constant.GameStateType;
import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.constant.SelectMenuType;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto.MapSymbol;
import com.kyokomi.srpgquest.layer.CutInLayer.ICutInCallback;
import com.kyokomi.srpgquest.logic.BattleLogic;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.MapItem;
import com.kyokomi.srpgquest.scene.MainScene;
import com.kyokomi.srpgquest.scene.MainScene.IAnimationCallback;
import com.kyokomi.srpgquest.utils.MapGridUtil;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;

/**
 * ゲーム全体を管理するクラス.
 * @author kyokomi
 *
 */
public class GameManager {
	private final static String TAG = "GameManager";
	
	private MapBattleInfoDto mMapBattleInfoDto;
	
	private GameStateType mGameState;
	
	private SparseArray<ActorPlayerDto> mPlayerList;
	private SparseArray<ActorPlayerDto> mEnemyList;
	
	/** マップ管理. */
	private MapManager mMapManager;
	
	/** バトル汎用. */
	private BattleLogic mBattleLogic;
	
	/** 選択したプレイヤーテンポラリ. */
	private ActorPlayerMapItem mSelectActorPlayer;
	
	/**
	 * コンストラクタ.
	 * @param mainActivity
	 */
	public GameManager(SRPGGameManagerListener pSRPGGameManagerListener) {
		this.mSRPGGameManagerListener = pSRPGGameManagerListener;

		// 初期化
		mBattleLogic = new BattleLogic();
		mPlayerList = new SparseArray<ActorPlayerDto>();
		mEnemyList = new SparseArray<ActorPlayerDto>();
		
		// 敵のターンのタイマー制御を生成
		mSRPGGameManagerListener.setEnemyTurnUpdateHandler(new TimerHandler(1.0f, true, new ITimerCallback() {
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
		}));
		
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
		mMapManager = new MapManager(MapGridUtil.GRID_X, MapGridUtil.GRID_Y, 
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
		mSRPGGameManagerListener.refresh();;
	}
	
	/**
	 * ゲーム開始.
	 */
	public void gameStart() {
		changeGameState(GameStateType.START);
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
		final MapPoint mapPoint = mMapManager.calcGridDecodePosition(x, y);
		Log.d(TAG, "MapPoint  x = " + mapPoint.getMapPointX() + " y = " + mapPoint.getMapPointY());
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
				// TODO: SE
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				ActorPlayerMapItem actorPlayerMapItem = (ActorPlayerMapItem) mapItem;
				
				// 敵のステータスは非表示
				mSRPGGameManagerListener.hideEnemyStatusWindow();
				
				// 攻撃もしくは移動が完了していなければ行動可能とする
				if (!actorPlayerMapItem.isWaitDone()) {
					// 選択カーソル表示
					mSRPGGameManagerListener.touchedCusor(mapPoint);
					
					// 行動ウィンドウを表示
					showSelectMenu(actorPlayerMapItem);
				} else {
					// アニメーション停止
					mSRPGGameManagerListener.stopWalkingPlayerAnimation(mSelectActorPlayer.getSeqNo());
					// プレイヤーのステータスは表示
					mSRPGGameManagerListener.showPlayerStatusWindow(actorPlayerMapItem.getSeqNo());
				}
			} else if (touchMapDataType == MapDataType.ENEMY) {
				// TODO: SE
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				ActorPlayerMapItem actorEnemyMapItem = (ActorPlayerMapItem) mapItem;
				// プレイヤーのステータス非表示
				mSRPGGameManagerListener.hidePlayerStatusWindow();
				// 敵のステータス表示
				mSRPGGameManagerListener.showEnemyStatusWindow(actorEnemyMapItem.getSeqNo());
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
				// TODO: SE
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
						mSRPGGameManagerListener.stopWalkingPlayerAnimation(mSelectActorPlayer.getSeqNo());
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
			mSRPGGameManagerListener.hideCursor();
			break;
		
		/* 移動選択中 */
		case PLAYER_MOVE:
			// 移動を選択したときは移動可能カーソルにしか反応しない
			if (touchMapDataType == MapDataType.MOVE_DIST) {
				// TODO: SE
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				if (mSelectActorPlayer != null) {
					
					changeGameState(GameStateType.ANIMATION);
					
					// 移動List作成
					List<MapPoint> moveMapPointList = mMapManager.actorPlayerCreateMovePointList(
							mSelectActorPlayer, mapPoint);
					
					// 移動先のカーソルの色を変える
					mSRPGGameManagerListener.touchedCusor(mapPoint);
					
					// 移動リストを引数にScene側の移動アニメーションを呼び出す
					mSRPGGameManagerListener.movePlayerAnimation(mSelectActorPlayer.getSeqNo(), 
							moveMapPointList, 
							new MainScene.IAnimationCallback() {
						@Override
						public void doAction() {
							// カーソルを消す
							mSRPGGameManagerListener.hideCursor();
							
							// 移動結果をマップ情報に反映
							// プレイヤーのステータスを移動済みにする
							mMapManager.moveEndChangeMapItem(mSelectActorPlayer, mapPoint);
							// 移動済みに更新
							mSelectActorPlayer.setMoveDone(true);
							
							if (!mSelectActorPlayer.isWaitDone()) {
								// ポップアップ表示
								showSelectMenu();
								changeGameState(GameStateType.PLAYER_SELECT);
							} else {
								// アニメーション停止
								mSRPGGameManagerListener.stopWalkingPlayerAnimation(
										mSelectActorPlayer.getSeqNo());
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
	
	private final SRPGGameManagerListener mSRPGGameManagerListener;
	public interface SRPGGameManagerListener {
		// 敵のターンのタイマー
		void setEnemyTurnUpdateHandler(TimerHandler timerHandler);
		TimerHandler getEnemyTurnUpdateHandler();
		void stopEnemyTurnTimer();
		void startEnemyTurnTimer();
		// プレイヤー周り
		ActorPlayerDto createPlayer(int seqNo, int playerId, MapPoint mapPoint);
		ActorPlayerDto createEnemy(int seqNo, int enemyId, MapPoint mapPoint);
		void removeEnemy(final int enemyId);
		void removePlayer(final int playerId);
		void stopWalkingPlayerAnimation(int playerSeqNo);
		void stopWalkingEnemyAnimation(int enemySeqNo);
		void movePlayerAnimation(int playerSeqNo, List<MapPoint> moveMapPointList, final IAnimationCallback animationCallback);
		void moveEnemyAnimation(int enemySeqNo, List<MapPoint> moveMapPointList, final IAnimationCallback animationCallback);
		void acotorDirection(int acoterSeqNo, MoveDirectionType directionType);
		void acotorDamageEffect(int acoterSeqNo);
		// 障害物
		void createObstacle(int obstacleId, MapPoint mapPoint);
		// カーソル
		void createMoveCursors(List<Point> cursorMapPointList);
		void createAttackCursors(List<Point> cursorMapPointList);
		void touchedCusor(MapPoint mapPoint);
		void hideCursor();
		// カットイン
		void showPlayerWinCutIn(final ICutInCallback cutInCallback);
		void showGameOverCutIn(final ICutInCallback cutInCallback);
		void showPlayerTurnCutIn(final List<Integer> playerSeqNoList, final ICutInCallback cutInCallback);
		void showEnemyTurnCutIn(final List<Integer> enemySeqNoList, final ICutInCallback cutInCallback);
		// テキスト表示
		void showDamageText(final int damage, final PointF dispPoint);
		// ステータスウィンドウ
		void refreshPlayerStatusWindow(int playerSeqNo);
		void refreshEnemyStatusWindow(int enemySeqNo);
		void showPlayerStatusWindow(int playerSeqNo);
		void showEnemyStatusWindow(int enemySeqNo);
		void hidePlayerStatusWindow();
		void hideEnemyStatusWindow();
		// メニュー
		void showSelectMenu(boolean isAttackDone, boolean isMovedDone, MapPoint mapPoint);
		void hideSelectMenu();
		// sortとか
		void refresh();
	}
	
	//---------------------------------------------------------
	// プレイヤー追加とか
	//---------------------------------------------------------
	private void addPlayer(MapSymbol mapSymbol) {
		if (mPlayerList.indexOfKey(mapSymbol.getSeqNo()) >= 0) {
			// すでに追加済み
			return;
		}
		MapPoint mapPoint = mMapManager.calcGridPosition(mapSymbol.getMapPointX(), mapSymbol.getMapPointY());
		ActorPlayerDto player = mSRPGGameManagerListener.createPlayer(mapSymbol.getSeqNo(), mapSymbol.getId(), mapPoint);
		mPlayerList.put(mapSymbol.getSeqNo(), player);
		mMapManager.addPlayer(mapSymbol.getSeqNo(), mapPoint.getMapPointX(), mapPoint.getMapPointY(), player);
		Log.d(TAG, "addPlayer mapSymbol x = " + mapSymbol.getMapPointX() + " y = " + mapSymbol.getMapPointY());
		Log.d(TAG, "addPlayer mapPoint x = " + mapPoint.getMapPointX() + " y = " + mapPoint.getMapPointY());
	}
	private void addEnemy(MapSymbol mapSymbol) {
		if (mEnemyList.indexOfKey(mapSymbol.getSeqNo()) >= 0) {
			// すでに追加済み
			return;
		}
		MapPoint mapPoint = mMapManager.calcGridPosition(mapSymbol.getMapPointX(), mapSymbol.getMapPointY());
		ActorPlayerDto enemy = mSRPGGameManagerListener.createEnemy(mapSymbol.getSeqNo(), mapSymbol.getId(), mapPoint);
		mEnemyList.put(mapSymbol.getSeqNo(), enemy);
		mMapManager.addEnemy(mapSymbol.getSeqNo(), 
				mapPoint.getMapPointX(), mapPoint.getMapPointY(), enemy);
	}
	
	private void addObstacle(MapSymbol mapSymbol) {
		MapPoint mapPoint = mMapManager.calcGridPosition(mapSymbol.getMapPointX(), mapSymbol.getMapPointY());
		
		mMapManager.addObstacle(mapPoint.getMapPointX(), mapPoint.getMapPointY());
		mSRPGGameManagerListener.createObstacle(mapSymbol.getId(), 
				mMapManager.calcGridPosition(mapPoint.getMapPointX(), mapPoint.getMapPointY()));
	}
	//---------------------------------------------------------
	// カーソル表示関連
	//---------------------------------------------------------
	/**
	 * 移動範囲カーソル表示.
	 * @param mapItem
	 */
	private boolean showMoveDistCursor(MapItem mapItem) {
		MapPoint mapPoint = mMapManager.calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
		return showMoveDistCursor(mapPoint);
	}
	private boolean showMoveDistCursor(MapPoint mapPoint) {
		List<MapItem> mapItems = mMapManager.actorPlayerFindDist(mapPoint);
		if (mapItems == null || mapItems.isEmpty()) {
			Log.d(TAG, "showMoveDistCursor create error");
			return false;
		}
		List<Point> cursorMapPointList = new ArrayList<Point>();
		for (MapItem mapItem : mapItems) {
			cursorMapPointList.add(new Point(mapItem.getMapPointX(), mapItem.getMapPointY()));
		}
		mSRPGGameManagerListener.createMoveCursors(cursorMapPointList);
		return true;
	}
	
	/**
	 * 攻撃範囲カーソル表示.
	 * @param mapItem
	 */
	private boolean showAttackDistCursor(MapItem mapItem) {
		MapPoint mapPoint = mMapManager.calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
		return showAttackDistCursor(mapPoint);
	}
	private boolean showAttackDistCursor(MapPoint mapPoint) {
		List<MapItem> mapItems = mMapManager.actorPlayerFindAttackDist(mapPoint);
		if (mapItems == null || mapItems.isEmpty()) {
			Log.d(TAG, "showAttackDistCursor create error");
			return false;
		}
		List<Point> cursorMapPointList = new ArrayList<Point>();
		for (MapItem mapItem : mapItems) {
			cursorMapPointList.add(new Point(mapItem.getMapPointX(), mapItem.getMapPointY()));
		}
		mSRPGGameManagerListener.createAttackCursors(cursorMapPointList);
		return true;
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
				// TODO: SE
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				changeGameState(GameStateType.PLAYER_ATTACK);
				showAttackDistCursor(mSelectActorPlayer);
				break;
			case MENU_MOVE: // 移動
				// TODO: SE
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				changeGameState(GameStateType.PLAYER_MOVE);
				if (!showMoveDistCursor(mSelectActorPlayer)) {
					changeGameState(GameStateType.PLAYER_TURN);
				}
				break;
			case MENU_WAIT: // 待機
				// TODO: SE
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				// 待機状態にする
				mSelectActorPlayer.setWaitDone(true);
				// アニメーション停止
				mSRPGGameManagerListener.stopWalkingPlayerAnimation(mSelectActorPlayer.getSeqNo());
				
				changeGameState(GameStateType.PLAYER_TURN);
				mSRPGGameManagerListener.hideCursor();
				break;
			case MENU_CANCEL: // キャンセル
				// TODO: SE
//				mBaseScene.getMediaManager().play(SoundType.BTN_PRESSED_SE);
				break;
			default:
				break;
			}
		}
		hideSelectMenu();
		mSRPGGameManagerListener.refresh();
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
		
		mSRPGGameManagerListener.showSelectMenu(
				mSelectActorPlayer.isAttackDone(), 
				mSelectActorPlayer.isMoveDone(), 
				mMapManager.getMapItemToMapPoint(mSelectActorPlayer));
		
		// プレイヤーのステータスを表示
		mSRPGGameManagerListener.showPlayerStatusWindow(mSelectActorPlayer.getSeqNo());
	}
	private void hideSelectMenu() {
		mSRPGGameManagerListener.hideSelectMenu();
		// プレイヤーのステータスも非表示
		mSRPGGameManagerListener.hidePlayerStatusWindow();
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
				// タイマーを停止
				mSRPGGameManagerListener.stopEnemyTurnTimer();
				
			} else {
				// ターン終了判定
				if (mMapManager.checkPlayerTurnEnd(MapDataType.ENEMY)) {
					// プレイヤーターン開始
					changePlayerTurn();
					// タイマー停止
					mSRPGGameManagerListener.stopEnemyTurnTimer();
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
		List<Integer> seqNoList = new ArrayList<Integer>();
		int count = mPlayerList.size();
		for (int i = 0; i < count; i++) {
			seqNoList.add(mPlayerList.keyAt(i));
		}
		mSRPGGameManagerListener.showPlayerTurnCutIn(seqNoList, new ICutInCallback() {
			@Override
			public void doAction() {
				// 全プレイヤーを行動可能にしてアニメーションを再開
				mMapManager.refreshAllActorWait(MapDataType.PLAYER);
				// ターン終了=> プレイヤーターン
				mGameState = GameStateType.PLAYER_TURN;
			}
		});
		// カットイン中に操作させないためにコールバック後に操作可能にする
	}
	private void changeEnemyTurn() {
		// 敵のターンアニメーション
		List<Integer> seqNoList = new ArrayList<Integer>();
		int count = mEnemyList.size();
		for (int i = 0; i < count; i++) {
			seqNoList.add(mEnemyList.keyAt(i));
		}
		mSRPGGameManagerListener.showEnemyTurnCutIn(seqNoList, new ICutInCallback() {
			@Override
			public void doAction() {
				// 全エネミーを行動可能にする
				mMapManager.refreshAllActorWait(MapDataType.ENEMY);
				// 敵のターンのタイマーを開始
				mSRPGGameManagerListener.startEnemyTurnTimer();
			}
		});
		// カットイン中に操作させないためにコールバックに入れない
		// ターン終了=> 敵のターン
		mGameState = GameStateType.ENEMY_TURN;
	}
	
	private void changePlayerWin() {
		Log.d(TAG, "changeState Player Win");
		// 勝利のカットインを入れてコールバックで次のシナリオへ
		mSRPGGameManagerListener.showPlayerWinCutIn(new ICutInCallback() {
			@Override
			public void doAction() {
				
			}
		});
		// カットイン中に操作させないためにコールバックに入れない
		mGameState = GameStateType.END;
	}
	private void changeEnemyWin() {
		Log.d(TAG, "changeState GameOver");
		// 敗北のカットインを入れてコールバックでタイトル画面に戻す
		mSRPGGameManagerListener.showGameOverCutIn(new ICutInCallback() {
			@Override
			public void doAction() {
				
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
		if (enemyMovePoint != null && !enemyMovePoint.isMuchMapPoint(mMapManager.getMapItemToMapPoint(enemyMapItem))) {
			
			changeGameState(GameStateType.ENEMY_MOVE);
			
			// 移動List作成
			List<MapPoint> moveMapPointList = mMapManager.actorPlayerCreateMovePointList(
					enemyMapItem, enemyMovePoint);
			if (moveMapPointList != null) {
				// 移動先のカーソルの色を変える
				mSRPGGameManagerListener.touchedCusor(enemyMovePoint);
				
				// 移動リストを引数にScene側の移動アニメーションを呼び出す
				mSRPGGameManagerListener.moveEnemyAnimation(enemyMapItem.getSeqNo(), moveMapPointList, 
						new MainScene.IAnimationCallback() {
					@Override
					public void doAction() {
						
						// カーソルを消す
						mSRPGGameManagerListener.hideCursor();
						
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
									mSRPGGameManagerListener.stopWalkingEnemyAnimation(enemyMapItem.getSeqNo());
								}
								// 攻撃終了後 倒れたアクターをマップ上から削除とかカーソルを初期化など
								mMapManager.attackEndChangeMapItem(enemyMapItem, attackTarget, isDead);
							}						
						} else {
							// アニメーション停止
							mSRPGGameManagerListener.stopWalkingEnemyAnimation(enemyMapItem.getSeqNo());
						}	

						// 待機
						enemyMapItem.setWaitDone(true);
						changeGameState(GameStateType.ENEMY_TURN);
					}
				}); // コールバックEND
				
				return true;
				
			} else {
				// カーソルを消す
				mSRPGGameManagerListener.hideCursor();
				// 待機
				enemyMapItem.setWaitDone(true);
				changeGameState(GameStateType.ENEMY_TURN);
				return false;
			}
		} else {
			// カーソルを消す
			mSRPGGameManagerListener.hideCursor();
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
		
		// 攻撃対象が攻撃者から見てどの方向にいるか取得
		mSRPGGameManagerListener.acotorDirection(fromPlayerMapItem.getSeqNo(), 
				mMapManager.findAttackDirection(fromPlayerMapItem, toPlayerMapItem));
		
		// ダメージを表示
		PointF dispPoint = MapGridUtil.indexToDisp(
				toPlayerMapItem.getMapPointX(), toPlayerMapItem.getMapPointY());
		mSRPGGameManagerListener.showDamageText(damage, dispPoint);
		mSRPGGameManagerListener.acotorDamageEffect(toPlayerMapItem.getSeqNo());
		
		// ステータスウィンドウへの反映と死亡時はマップ上から消す
		if (toPlayerMapItem.getMapDataType() == MapDataType.ENEMY) {
			mSRPGGameManagerListener.refreshEnemyStatusWindow(toPlayerMapItem.getSeqNo());
			if (isDead) {
				mSRPGGameManagerListener.removeEnemy(toPlayerMapItem.getSeqNo());
				mEnemyList.remove(toPlayerMapItem.getSeqNo());
			}
		} else if (toPlayerMapItem.getMapDataType() == MapDataType.PLAYER) {
			mSRPGGameManagerListener.refreshPlayerStatusWindow(toPlayerMapItem.getSeqNo());
			if (isDead) {
				mSRPGGameManagerListener.removePlayer(toPlayerMapItem.getSeqNo());
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
