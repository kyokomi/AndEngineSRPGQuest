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
	private MapManager mapManager;
	
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
		mapManager = new MapManager(this, mapX, mapY, scale);
		
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
		return mapManager.getMapPointToActorPlayerId(mapPoint);
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
	 * TODO: タッチするタイミングは色々あるのでその辺理解して振り分けないといけない
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
		final MapItem mapItem = mapManager.getMapPointToMapItem(mapPoint);
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
				if (!actorPlayerMapItem.isMoveDone() || !actorPlayerMapItem.isAttackDone()) {
					// 行動ウィンドウを表示
					showSelectMenu(actorPlayerMapItem);
				} else {
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
				ActorPlayerMapItem enemy = mapManager.getMapPointToActorPlayer(mapPoint);
				if (enemy != null) {
					// TODO: [将来対応]攻撃確認ウィンドウ表示				
					
					// ------- 攻撃処理 --------
					battleStart(mSelectActorPlayer, enemy);
					
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
					List<MapPoint> moveMapPointList = mapManager.actorPlayerCreateMovePointList(
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
							// TODO: プレイヤーのステータスを移動済みにする
							mapManager.moveEndChangeMapItem(mSelectActorPlayer, mapPoint);
							
							// TODO: このあと行動選択ウィンドウの移動が押せくなる
							// TODO: 行動可能な場合
							if (true) {
								// ポップアップ表示
								showSelectMenu();
							} 
//							else {
//								// TODO: このキャラを待機モードにする
//								
//								changeGameState(GameStateType.PLAYER_TURN);
//							}	
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
		mapManager.addPlayer(mapPointX, mapPointY, player);
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
		mapManager.addEnemy(mapPointX, mapPointY, enemy);
		// Scene側でSpriteを生成
		baseScene.createEnemySprite(enemy, 
				calcGridPosition(mapPointX, mapPointY));
	}
	private ActorPlayerDto createActorPlayer(int playerId, int imageResId) {
		ActorPlayerDto actorPlayer = new ActorPlayerDto();
		actorPlayer.setPlayerId(playerId);
		actorPlayer.setImageResId(imageResId);
		// TODO: DBとかから取得
		
		actorPlayer.setName("アスリーン");
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
		mapManager.addObstacle(mapPointX, mapPointY);
		baseScene.createObstacleSprite(calcGridPosition(mapPointX, mapPointY), 16 * 12 + 0);
	}
	//---------------------------------------------------------
	// Sceneから呼ばれる
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
		List<MapItem> mapItems = mapManager.actorPlayerFindDist(mapPoint);
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
		List<MapItem> mapItems = mapManager.actorPlayerFindAttackDist(mapPoint);
		for (MapItem mapItem : mapItems) {
			MapPoint mapItemPoint = calcGridPosition(mapItem.getMapPointX(), mapItem.getMapPointY());
			baseScene.createAttackCursorSprite(mapItemPoint);
		}
		baseScene.sortChildren();
	}
	
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
	
	// ----------------------------------------------------------
	// BaseSceneの操作
	// ----------------------------------------------------------
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
		changeGameState(GameStateType.PLAYER_SELECT);
		baseScene.showSelectMenu(getMapItemToMapPoint(mSelectActorPlayer));
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
		mGameState = pGameStateType;
	}
//	public void gameLog(String text) {
//		Log.d(TAG, text);
//		mMainActivity.setGameLog(text);
//	}
//	public void showPlayerStatus(CharacterStatus playerStatus) {
//		mMainActivity.showPlayerStatus(playerStatus);
//	}
//	

	/**
	 * バトル開始し、倒したかの結果を返します.
	 * TODO: 反撃できるようにしたら　返り討ちになる場合もあるのでbooleanじゃフラグが足りない
	 * 
	 * @param player
	 * @param enemy 更新しちゃいます (注意)
	 * @return true:倒した / false:倒してない
	 */
	private boolean battleStart(ActorPlayerMapItem fromPlayerMapItem, ActorPlayerMapItem toPlayerMapItem) {
		ActorPlayerDto formPlayer = getActorMapItemActorPlayer(fromPlayerMapItem);
		ActorPlayerDto toPlayer = getActorMapItemActorPlayer(toPlayerMapItem);

		// バトルロジック実行
		int damage = mBattleLogic.attack(formPlayer, toPlayer);
		
		// TODO: [将来対応]キャラが攻撃モーション,敵キャラがダメージモーション
		
		// ダメージを表示
		baseScene.showDamageText(damage, getMapItemToMapPoint(toPlayerMapItem));
		// TODO: 攻撃終了後 倒れたアクターをマップ上から削除とか
		mapManager.attackEndChangeMapItem();
		
		// 死亡判定
		if (toPlayer.getHitPoint() <= 0) {
			// 死亡
			return true;
		} else {
			// 生き残り
			return false;
		}
	}
	
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
//	
//	/**
//	 * バトル終了後.
//	 */
//	public void battleEnd(MapDataType mapDataType) {
//		actionEnd(mapDataType);
//		checkTouchNotEnd();
//		updateGameState();
//	}
//	
//	
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
//	private void changeEnemyTurn() {
//		gameLog("EnemyTurn");
//		
//		if (mEnemyTurnAnim.getListeners() == null) {
//			mEnemyTurnAnim.addListener(new Animator.AnimatorListener() {
//				@Override public void onAnimationStart(Animator animation) {}
//				@Override public void onAnimationRepeat(Animator animation) {}
//				@Override public void onAnimationCancel(Animator animation) {}
//				// アニメーション後に開始
//				@Override public void onAnimationEnd(Animator animation) {
//					
//					mGameState = GameStateType.ENEMY_TURN;
//					
//					List<CharacterSpriteView> enemys = mMapManager.getEnemyList();
//					for (CharacterSpriteView enemy : enemys) {
//						if (enemy.getCharacterStatus().getHitPoint() > 0) {
//							enemy.setMoveDone(false);
//							enemy.setAttackDone(true);
//							mMapManager.enemyMove(enemy);						
//						}
//					}
//				}
//			});
//		}
//		mGameState = GameStateType.ANIMATOR;
//		if (!mEnemyTurnAnim.isRunning()) {
//			mEnemyTurnAnim.start();
//		}
//	}
//	private void playerWin() {
//		gameLog("playerWin");
//	}
//	private void enemyWin() {
//		gameLog("enemyWin");
//	}
}
