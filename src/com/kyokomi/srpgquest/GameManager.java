package com.kyokomi.srpgquest;

import java.util.List;

import com.kyokomi.core.dto.ActorPlayerDto;
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
	
	private MapBattleScene baseScene;
	
	private GameStateType mGameState;
	
	private SparseArray<ActorPlayerDto> playerList;
	private SparseArray<ActorPlayerDto> enemyList;
	
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
	public GameManager(MapBattleScene baseScene) {
		this.baseScene = baseScene;

		// 初期化
		mBattleLogic = new BattleLogic();
		playerList = new SparseArray<ActorPlayerDto>();
		enemyList = new SparseArray<ActorPlayerDto>();
		
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
		
		changeGameState(GameStateType.PLAYER_TURN);
		
		baseScene.sortChildren();
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
			break;
			
		/* プレイヤーのターン */
		case PLAYER_TURN:
						
			// プレイヤーキャラ選択が可能なので行動可能であればウィンドウ表示
			if (touchMapDataType == MapDataType.PLAYER) {
				ActorPlayerMapItem actorPlayerMapItem = (ActorPlayerMapItem) mapItem;
				
				// 敵のステータスは非表示
				baseScene.hideEnemyStatusWindow();
				
				// 攻撃もしくは移動が完了していなければ行動可能とする
				if (!actorPlayerMapItem.isWaitDone()) {
					// 行動ウィンドウを表示
					showSelectMenu(actorPlayerMapItem);
				} else {
					// アニメーション停止
					baseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
					// プレイヤーのステータスは表示
					baseScene.showPlayerStatusWindow(actorPlayerMapItem.getPlayerId());
				}
			} else if (touchMapDataType == MapDataType.ENEMY) {
				ActorPlayerMapItem actorEnemyMapItem = (ActorPlayerMapItem) mapItem;
				// プレイヤーのステータス非表示
				baseScene.hidePlayerStatusWindow();
				// 敵のステータス表示
				baseScene.showEnemyStatusWindow(actorEnemyMapItem.getPlayerId());
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
						baseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
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
			baseScene.hideCursorSprite();
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
					baseScene.selectCursor(mapPoint);
					
					// 移動リストを引数にScene側の移動アニメーションを呼び出す
					baseScene.movePlayerAnimation(mSelectActorPlayer.getPlayerId(), moveMapPointList, 
							new MapBattleScene.IAnimationCallback() {
						@Override
						public void doAction() {
							// カーソルを消す
							baseScene.hideCursorSprite();
							
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
								baseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
								
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
		if (playerList.indexOfKey(playerId) >= 0) {
			// すでに追加済み
			return;
		}
 		ActorPlayerDto player = createActorPlayer(playerId, playerImageId);
		playerList.put(playerId, player);
		mMapManager.addPlayer(mapPointX, mapPointY, player);
		// Scene側でSpriteを生成
		baseScene.createPlayerSprite(player,
				calcGridPosition(mapPointX, mapPointY));
	}
	private void addEnemy(int mapPointX, int mapPointY, int enemyId, int enemyImageId) {
		if (enemyList.indexOfKey(enemyId) >= 0) {
			// すでに追加済み
			return;
		}
		ActorPlayerDto enemy = createActorPlayer(enemyId, enemyImageId);
		enemyList.put(enemyId, enemy);
		mMapManager.addEnemy(mapPointX, mapPointY, enemy);
		// Scene側でSpriteを生成
		baseScene.createEnemySprite(enemy, 
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
		baseScene.createObstacleSprite(calcGridPosition(mapPointX, mapPointY), 16 * 12 + 0);
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
			baseScene.createMoveCursorSprite(mapItemPoint);
		}
		baseScene.sortChildren();
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
			baseScene.createAttackCursorSprite(mapItemPoint);
		}
		baseScene.sortChildren();
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
				baseScene.stopWalkingPlayerAnimation(mSelectActorPlayer.getPlayerId());
				
				changeGameState(GameStateType.PLAYER_TURN);
				baseScene.hideCursorSprite();
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
		baseScene.showSelectMenu(mSelectActorPlayer.isAttackDone(), mSelectActorPlayer.isMoveDone(), 
				getMapItemToMapPoint(mSelectActorPlayer));
		// プレイヤーのステータスも非表示
		baseScene.showPlayerStatusWindow(mSelectActorPlayer.getPlayerId());
	}
	private void hideSelectMenu() {
		baseScene.hideSelectMenu();
		// プレイヤーのステータスも非表示
		baseScene.hidePlayerStatusWindow();
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
		
		if (pGameStateType == GameStateType.PLAYER_TURN) {
			// プレイヤー勝利判定
			if (!isBeingEnemy()) {
				// プレイヤー処理
				// TODO: 勝利のカットインを入れてコールバックで次のシナリオへ
				Log.d(TAG, "Player Win");
				mGameState = GameStateType.END;
				return ;
			}
			
			// プレイヤーターンエンド判定
			if (mMapManager.checkPlayerTurnEnd()) {
				// ターン終了=> 敵のターン
				mGameState = GameStateType.ENEMY_TURN;
				// 敵のターン処理を実行
				changeEnemyTurn();
				
				// 敵の勝利判定
				if (!isBeingPlayer()) {
					// 敵勝利
					// TODO: 敗北のカットインを入れてコールバックでタイトル画面に戻す
					Log.d(TAG, "Player Lose");
					mGameState = GameStateType.END;
					return ;
				}
				
				// プレイヤーターン開始
				// TODO: アニメーション呼び出し
				
				// 全プレイヤーを行動可能にしてアニメーションを再開
				mMapManager.refreshAllActorWait(MapDataType.PLAYER);
				int count = playerList.size();
				for (int i = 0; i < count; i++) {
					baseScene.startWalkingPlayerAnimation(playerList.valueAt(i).getPlayerId());
				}
			}
			mGameState = GameStateType.PLAYER_TURN;
		} else {
			mGameState = pGameStateType;			
		}
	}
	
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
			
			// 移動List作成
			List<MapPoint> moveMapPointList = mMapManager.actorPlayerCreateMovePointList(
					enemyMapItem, enemyMovePoint);
			if (moveMapPointList != null) {
				// 移動先のカーソルの色を変える
				baseScene.selectCursor(enemyMovePoint);
				
				// 移動リストを引数にScene側の移動アニメーションを呼び出す
				baseScene.moveEnemyAnimation(enemyMapItem.getPlayerId(), moveMapPointList, 
						new MapBattleScene.IAnimationCallback() {
					@Override
					public void doAction() {
						// カーソルを消す
						baseScene.hideCursorSprite();
						
						// 移動結果をマップ情報に反映
						// プレイヤーのステータスを移動済みにする
						mMapManager.moveEndChangeMapItem(enemyMapItem, enemyMovePoint);
						// 移動済みに更新
						enemyMapItem.setMoveDone(true);
						
						// 行動可能であれば攻撃する
						if (!enemyMapItem.isWaitDone()) {
							// 攻撃範囲表示
							showAttackDistCursor(enemyMapItem);
										
							// 攻撃範囲にプレイヤーが存在する場合
							if (true) {
								boolean isDead = battleStart(enemyMapItem, attackTarget);
								// 攻撃済みにする
								enemyMapItem.setAttackDone(true);
								if (enemyMapItem.isWaitDone()) {
									// アニメーション停止
									baseScene.stopWalkingEnemyAnimation(enemyMapItem.getPlayerId());
								}
								// 攻撃終了後 倒れたアクターをマップ上から削除とかカーソルを初期化など
								mMapManager.attackEndChangeMapItem(enemyMapItem, attackTarget, isDead);
							}						
						} else {
							// アニメーション停止
							baseScene.stopWalkingEnemyAnimation(enemyMapItem.getPlayerId());
						}	

						// 待機
						enemyMapItem.setWaitDone(true);
					}
				});
			} else {
				// 待機
				enemyMapItem.setWaitDone(true);
			}
		} else {
			// 待機
			enemyMapItem.setWaitDone(true);
		}
	}
	
	private boolean isBeingPlayer() {
		return isBeingActor(playerList);
	}
	private boolean isBeingEnemy() {
		return isBeingActor(enemyList);
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
		baseScene.showDamageText(damage, getMapItemToMapPoint(toPlayerMapItem));
		// ステータスウィンドウへの反映と死亡時はマップ上から消す
		if (toPlayerMapItem.getMapDataType() == MapDataType.ENEMY) {
			baseScene.refreshEnemyStatusWindow(toPlayerMapItem.getPlayerId());
			if (isDead) {
				baseScene.removeEnemy(toPlayerMapItem.getPlayerId());
				enemyList.remove(toPlayerMapItem.getPlayerId());
			}
		} else if (toPlayerMapItem.getMapDataType() == MapDataType.PLAYER) {
			baseScene.refreshPlayerStatusWindow(toPlayerMapItem.getPlayerId());
			if (isDead) {
				baseScene.removePlayer(toPlayerMapItem.getPlayerId());
				playerList.remove(toPlayerMapItem.getPlayerId());
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
			player = playerList.get(actorMapItem.getPlayerId());
			break;
		case ENEMY:
			player = enemyList.get(actorMapItem.getPlayerId());
			break;
		default:
			break;
		}
		return player;
	}

//	private void checkTouchNotEnd() {
//		View v = mMapManager.isSelectPlayerEnd();
//		if (v != null) {
//			touchedPlayer(v);
//		}
//	}
//	// --------- ターン
//	
//	public void actionEnd(MapDataType mapDataType) {
//		switch (mapDataType) {
//		case PLAYER:
//			mGameState = GameStateType.PLAYER_TURN;
//			break;
//		case ENEMY:
//			mGameState = GameStateType.ENEMY_TURN;
//			break;
//		default:
//			// 例外
//			break;
//		}
//	}
//	/**
//	 * ゲームステータス更新.
//	 * @return true:変化あり / false:変化なし
//	 */
//	public boolean updateGameState() {
//		boolean isUpdate = false;
//		
//		if (mGameState == GameStateType.PLAYER_TURN) {
//			List<CharacterSpriteView> players = mMapManager.getPlayerList();
//			if (players == null || players.isEmpty() || players.size() == 0) {
//				// エネミー勝利
//				enemyWin();
//				isUpdate = true;
//				return isUpdate;
//			}
//			List<CharacterSpriteView> enemys = mMapManager.getEnemyList();
//			if (enemys == null || enemys.isEmpty() || enemys.size() == 0) {
//				// プレイヤー勝利
//				playerWin();
//				isUpdate = true;
//				return isUpdate;
//			}
//			
//			for (CharacterSpriteView player : players) {
//				if (!player.isMoveDone() || !player.isAttackDone()) {
//					return isUpdate;
//				}
//			}
//			// エネミーターン
//			isUpdate = true;
//			changeEnemyTurn();
//			
//		} else if (mGameState == GameStateType.ENEMY_TURN) {
//			List<CharacterSpriteView> players = mMapManager.getPlayerList();
//			if (players == null || players.isEmpty() || players.size() == 0) {
//				// エネミー勝利
//				enemyWin();
//				isUpdate = true;
//				return isUpdate;
//			}
//			List<CharacterSpriteView> enemys = mMapManager.getEnemyList();
//			if (enemys == null || enemys.isEmpty() || enemys.size() == 0) {
//				// プレイヤー勝利
//				playerWin();
//				isUpdate = true;
//				return isUpdate;
//			}
//			
//			for (CharacterSpriteView enemy : enemys) {
//				if (!enemy.isMoveDone() || !enemy.isAttackDone()) {
//					return isUpdate;
//				}
//			}
//			// プレイヤーターン
//			isUpdate = true;
//			changePlayerTurn();
//		}
//		return isUpdate;
//	}
//	
//	private void changePlayerTurn() {
//		gameLog("PlayerTurn");
//		
//		if (mPlayerTurnAnim.getListeners() == null) {
//			mPlayerTurnAnim.addListener(new Animator.AnimatorListener() {
//				@Override public void onAnimationStart(Animator animation) {}
//				@Override public void onAnimationRepeat(Animator animation) {}
//				@Override public void onAnimationCancel(Animator animation) {}
//				// アニメーション後に開始
//				@Override public void onAnimationEnd(Animator animation) {
//					
//					mGameState = GameStateType.PLAYER_TURN;
//					
//					List<CharacterSpriteView> players = mMapManager.getPlayerList();
//					for (CharacterSpriteView player : players) {
//						player.setMoveDone(false);
//						player.setAttackDone(false);
//					}		
//				}
//			});
//		}
//		mGameState = GameStateType.ANIMATOR;
//		if (!mPlayerTurnAnim.isRunning()) {
//			mPlayerTurnAnim.start();
//		} else {
//			Log.d(TAG, "isRunning!!!!!!!!!!!");
//		}
//	}
	private void changeEnemyTurn() {

		// 全エネミーを行動可能にする
		mMapManager.refreshAllActorWait(MapDataType.ENEMY);
		int count = enemyList.size();
		for (int i = 0; i < count; i++) {
			baseScene.startWalkingEnemyAnimation(enemyList.valueAt(i).getPlayerId());
		}
		
		// 敵のターンアニメーション
		// TODO: baseを呼ぶ
		
		// モンスターのリストを取得
		for (int i = 0; i < count; i++) {
			ActorPlayerDto enemy = enemyList.valueAt(i);
			ActorPlayerMapItem enemyMapItem = mMapManager.getPlayerIdToActorMapItem(
					enemy.getPlayerId(), MapDataType.ENEMY);
			
			Log.d(TAG, "Enemy isAttackDone["+ enemyMapItem.isAttackDone() + "] isMoveDoen["+ enemyMapItem.isMoveDone() + "]");
			// 行動させる
			doEnemyAction(enemy, enemyMapItem);
			// TODO: 普通にループしてるとアニメーションのコールバックが終わる前に次のキャラが動いていまう。クリア判定とかもいっちゃう
		}
	}
	
//	private void playerWin() {
//		gameLog("playerWin");
//	}
//	private void enemyWin() {
//		gameLog("enemyWin");
//	}
}
