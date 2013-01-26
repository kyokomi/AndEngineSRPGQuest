package com.kyokomi.srpgquest;

import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.srpgquest.actor.ActorPlayer;
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
	
	private GameStateType gameState;
	
	private SparseArray<ActorPlayer> playerList;
	private SparseArray<ActorPlayer> enemyList;
	
	/** マップ管理. */
	private MapManager mapManager;
	
	/** バトル汎用. */
	private BattleLogic battleLogic;
	
	/**
	 * コンストラクタ.
	 * @param mainActivity
	 */
	public GameManager(MapBattleScene baseScene) {
		this.baseScene = baseScene;

		// 初期化
		battleLogic = new BattleLogic();
		playerList = new SparseArray<ActorPlayer>();
		enemyList = new SparseArray<ActorPlayer>();
		
		gameState = GameStateType.INIT;
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
		
		gameState = GameStateType.PLAYER_TURN;
		
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
	
	/** 選択したプレイヤーテンポラリ. */
	private ActorPlayerMapItem selectActorPlayer;
	
	public ActorPlayerMapItem getSelectActorPlayer() {
		return selectActorPlayer;
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
		Log.d(TAG, "GameState = [" + gameState + "]");
		/* 現在のゲームステータスに応じて処理を行う */
		switch (gameState) {
		case INIT:
			break;
			
		/* プレイヤーのターン */
		case PLAYER_TURN:
			
			// TODO: 敵もしくはプレイヤーだったらステータスウィンドウを表示
			
			// プレイヤーキャラ選択が可能なので行動可能であればウィンドウ表示
			if (touchMapDataType == MapDataType.PLAYER) {
				ActorPlayerMapItem actorPlayerMapItem = (ActorPlayerMapItem) mapItem;
		
				// 攻撃もしくは移動が完了していなければ行動可能とする
				if (!actorPlayerMapItem.isMoveDone() || !actorPlayerMapItem.isAttackDone()) {
					gameState = GameStateType.PLAYER_SELECT;
					selectActorPlayer = actorPlayerMapItem;
					// 行動ウィンドウを表示
					baseScene.showSelectMenu();
				}
			}
			break;
		
		/* キャラ選択中 */
		case PLAYER_SELECT:
			gameState = GameStateType.PLAYER_TURN;
			selectActorPlayer = null;
			// 行動ウィンドウ以外を押したら行動ウィンドウを閉じる
			baseScene.hideSelectMenu();
			break;
			
		/* 攻撃選択中 */
		case PLAYER_ATTACK:
			// 攻撃を選択したときは敵しかタップイベントに反応しない
			if (touchMapDataType == MapDataType.ATTACK_DIST) {
				// 敵が存在するカーソルかチェック
				ActorPlayerMapItem enemy = mapManager.getMapPointToActorPlayer(mapPoint);
				if (enemy != null) {
					// TODO: [将来対応]攻撃確認ウィンドウ表示				
					
					// TODO: 攻撃処理
					
					// バトルロジックで計算
					// キャラが攻撃モーション
					// ダメージを表示
					// 敵キャラがダメージモーション
					
					// 攻撃終了後
					mapManager.attackEndChangeMapItem();
					// カーソル消去
					baseScene.hideCursorSprite();
					// プレイヤーターンに戻る
					gameState = GameStateType.PLAYER_TURN;
				}
			} else {
				gameState = GameStateType.PLAYER_SELECT;
				
				baseScene.hideCursorSprite();
				// キャンセル
				baseScene.showSelectMenu();
			}
			break;
		
		/* 移動選択中 */
		case PLAYER_MOVE:
			// 移動を選択したときは移動可能カーソルにしか反応しない
			if (touchMapDataType == MapDataType.MOVE_DIST) {
				if (selectActorPlayer != null) {
					
					// 移動List作成
					List<MapPoint> moveMapPointList = mapManager.actorPlayerCreateMovePointList(
							selectActorPlayer, mapPoint);
					
					// 移動先のカーソルの色を変える
					baseScene.selectCursor(mapPoint);
					
					// 移動リストを引数にScene側の移動アニメーションを呼び出す
					baseScene.movePlayerAnimation(selectActorPlayer.getPlayerId(), moveMapPointList);
					// 移動結果をマップ情報に反映
					// TODO: プレイヤーのステータスを移動済みにする
					mapManager.moveEndChangeMapItem(selectActorPlayer, mapPoint);
					
					// TODO: このあと行動選択ウィンドウの移動が押せくなる
					
					gameState = GameStateType.PLAYER_TURN;
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
 		ActorPlayer player = createActorPlayer(playerId);
		playerList.put(playerId, player);
		mapManager.addPlayer(mapPointX, mapPointY, player);
		// Scene側でSpriteを生成
		baseScene.createPlayerSprite(playerId, playerImageId,
				calcGridPosition(mapPointX, mapPointY));
	}
	private void addEnemy(int mapPointX, int mapPointY, int enemyId, int enemyImageId) {
		if (enemyList.indexOfKey(enemyId) >= 0) {
			// すでに追加済み
			return;
		}
 		ActorPlayer enemy = createActorPlayer(enemyId);
		enemyList.put(enemyId, enemy);
		mapManager.addEnemy(mapPointX, mapPointY, enemy);
		// Scene側でSpriteを生成
		baseScene.createEnemySprite(enemyId, enemyImageId,
				calcGridPosition(mapPointX, mapPointY));
	}
	private ActorPlayer createActorPlayer(int playerId) {
		ActorPlayer actorPlayer = new ActorPlayer();
		actorPlayer.setPlayerId(playerId);
		// TODO: DBとかから取得？
		actorPlayer.setMovePoint(5);
		actorPlayer.setAttackRange(1);
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
	
	public void touchMenuBtnEvent(int pressedBtnTag) {
		
		if (selectActorPlayer != null) {
			switch (SelectMenuType.findTag(pressedBtnTag)) {
			case MENU_ATTACK: // 攻撃
				gameState = GameStateType.PLAYER_ATTACK;
				showAttackDistCursor(selectActorPlayer);
				break;
			case MENU_MOVE: // 移動
				gameState = GameStateType.PLAYER_MOVE;
				if (!showMoveDistCursor(selectActorPlayer)) {
					gameState = GameStateType.PLAYER_TURN;
				}
				break;
			case MENU_WAIT: // 待機
				gameState = GameStateType.PLAYER_TURN;
				baseScene.hideCursorSprite();
				break;
			case MENU_CANCEL: // キャンセル
				break;
			default:
				break;
			}
		}
		
		baseScene.hideSelectMenu();
	}
//    /**
//	 * @return the mMainActivity
//	 */
//	public Activity getActivity() {
//		return mMainActivity;
//	}
//	public void addMapItem(View child) {
//		mMainActivity.addBaseView(child);
//    }
//    public void removeMapItem(View view) {
//    	mMainActivity.removeBaseView(view);
//    }
//	public void gameLog(String text) {
//		Log.d(TAG, text);
//		mMainActivity.setGameLog(text);
//	}
//	public void showPlayerStatus(CharacterStatus playerStatus) {
//		mMainActivity.showPlayerStatus(playerStatus);
//	}
//	/**
//	 * 選択メニュー表示.
//	 * @param x
//	 * @param y
//	 */
//	public void showSelectMenu(float x, float y) {
//		mSelectMenuList.setX(x);
//		mSelectMenuList.setY(y);
//		mSelectMenuList.setOnItemClickListener(new ListView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				selectMenuItemClick(position);
//			}
//		});
//		// ベースViewに配置
//		mMainActivity.addBaseView(mSelectMenuList);
//	}
//	public void selectMenuItemClick(int position) {
//		switch (position) {
//		case 0:
//			if(!mMapManager.showCharcterDist()) {
//				return;
//			}
//			break;
//		case 1:
//			if (!mMapManager.showCharcterAttack()) {
//				return;
//			}
//			break;
//		case 2:
//			mMapManager.characterEnd();
//			break;
//		case 3:
//			mMapManager.selectCancel();
//			break;
//		default:
//			// 例外
//			actionCancel();
//			break;
//		}
//		
//		// ベースViewから削除
//		mMainActivity.removeBaseView(mSelectMenuList);
//	}
//	
//	public boolean touchedPlayer(View v) {
//		if (mGameState == GameStateType.PLAYER_TURN) {
//			mGameState = GameStateType.PLAYER_SELECT;
//			
//			showPlayerStatus(((CharacterSpriteView)v).getCharacterStatus());
//			// 選択メニュー表示
//			showSelectMenu(v.getX(), v.getY());
//			return true;
//		} else {
//			return false;
//		}
//	}
//	// ------- 攻撃関連
//	public boolean touchedAttackCursor(View v) {
//		if (mGameState == GameStateType.PLAYER_SELECT) {
//			mGameState = GameStateType.)PLAYER_ATTACK;
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
//	/**
//	 * バトル開始し、倒したかの結果を返します.
//	 * TODO: 反撃できるようにしたら　返り討ちになる場合もあるのでbooleanじゃフラグが足りない
//	 * 
//	 * @param player
//	 * @param enemy 更新しちゃいます (注意)
//	 * @return true:倒した / false:倒してない
//	 */
//	public boolean battleStart(CharacterSpriteView player, CharacterSpriteView enemy) {
//		// バトルロジック呼び出し
//		mBattleLogic.attack(player.getCharacterStatus(), enemy.getCharacterStatus());
//		// 結果HPがどうなったか
//		int hp = enemy.getCharacterStatus().getHitPoint();
//		if (hp <= 0) {
//			// ログ
//			gameLog("攻撃しました。倒しました!");
//			return true;
//		} else {
//			// ログ
//			gameLog("攻撃しました。残りHP[" + hp + "]");
//			return false;
//		}
//	}
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
//	/**
//	 * 待機時.
//	 */
//	public void actionWait(MapDataType mapDataType) {
//		actionEnd(mapDataType);
//		updateGameState();
//	}
//	/**
//	 * 操作キャンセル時.
//	 */
//	public void actionCancel() {
//		mGameState = GameStateType.PLAYER_TURN;
//		updateGameState();
//	}
//		
//	// -------- 移動関連
//	/**
//	 * 移動カーソル選択時.
//	 * @param v
//	 * @return true:移動開始 / false:移動しない
//	 */
//	public boolean touchedMoveCursor(View v) {
//		if (mGameState == GameStateType.PLAYER_SELECT) {
//			mGameState = GameStateType.PLAYER_MOVE;
//			return true;
//		} else {
//			return false;
//		}
//	}
//	/**
//	 * 移動完了時.
//	 */
//	public void moveEnd(MapDataType mapDataType) {
//		actionEnd(mapDataType);
//		checkTouchNotEnd();
//		updateGameState();
//	}
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
