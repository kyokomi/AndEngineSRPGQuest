package com.kyokomi.srpgquest;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.actor.ActorPlayer;
import com.kyokomi.srpgquest.constant.GameStateType;
import com.kyokomi.srpgquest.logic.BattleLogic;
import com.kyokomi.srpgquest.map.MapManager;
import com.kyokomi.srpgquest.map.common.MapData.MapDataType;
import com.kyokomi.srpgquest.map.item.ActorPlayerMapItem;
import com.kyokomi.srpgquest.map.item.CharacterSpriteView;
import com.kyokomi.srpgquest.scene.MapBattleScene;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * ゲーム全体を管理するクラス.
 * @author kyokomi
 *
 */
public class GameManager {
	private final static String TAG = "GameManager";
	
	private MapBattleScene mBaseScene;
	
	private GameStateType mGameState;
	
	private SparseArray<ActorPlayer> playerList;
	private SparseArray<ActorPlayer> enemyList;
	
	/** マップ管理. */
	private MapManager mMapManager;
	
	/** バトル汎用. */
	private BattleLogic mBattleLogic;
	
	/**
	 * コンストラクタ.
	 * @param mainActivity
	 */
	public GameManager(MapBattleScene baseScene) {
		this.mBaseScene = baseScene;

		// 初期化
		mBattleLogic = new BattleLogic();
		playerList = new SparseArray<ActorPlayer>();
		enemyList = new SparseArray<ActorPlayer>();
		
		mGameState = GameStateType.INIT;
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
		
		int playerId = 1;
		addPlayer(3, 3, playerId);
		
		int enemyId = 2;
		addEnemy(5, 5, enemyId);
		
		// 障害物配置
		mMapManager.addObstacle(3, 5);
		mMapManager.addObstacle(6, 9);
		mMapManager.addObstacle(7, 9);
		mMapManager.addObstacle(8, 7);
		mMapManager.addObstacle(8, 8);
		mMapManager.addObstacle(8, 9);
		mMapManager.addObstacle(0, 2);
		mMapManager.addObstacle(1, 2);
		mMapManager.addObstacle(2, 2);
		mMapManager.addObstacle(3, 2);
		mMapManager.addObstacle(4, 2);
		mMapManager.addObstacle(5, 2);
		
		mGameState = GameStateType.PLAYER_TURN;
	}
	
	private void addPlayer(int mapPointX, int mapPointY, int playerId) {
		if (playerList.indexOfKey(playerId) >= 0) {
			// すでに追加済み
			return;
		}
 		ActorPlayer player = createActorPlayer(playerId);
		playerList.put(playerId, player);
		mMapManager.addPlayer(mapPointX, mapPointY, player);
		// Scene側でSpriteを生成
		mBaseScene.createPlayerSprite(playerId); // TODO: X,Y軸とか渡す感じ?
	}
	private void addEnemy(int mapPointX, int mapPointY, int enemyId) {
		if (enemyList.indexOfKey(enemyId) >= 0) {
			// すでに追加済み
			return;
		}
 		ActorPlayer enemy = createActorPlayer(enemyId);
		enemyList.put(enemyId, enemy);
		mMapManager.addEnemy(mapPointX, mapPointY, enemy);
		// Scene側でSpriteを生成
		mBaseScene.createEnemySprite(enemyId); // TODO: X,Y軸とか渡す感じ?
	}
	private ActorPlayer createActorPlayer(int playerId) {
		ActorPlayer actorPlayer = new ActorPlayer();
		actorPlayer.setPlayerId(playerId);
		// TODO: DBとかから取得？
		actorPlayer.setMovePoint(5);
		actorPlayer.setAttackRange(1);
		return actorPlayer;
	}
	
//	public void createSelectMenu() {
//		mSelectMenuList = new ListView(mMainActivity);
//		List<String> buttonText = new ArrayList<String>();
//		buttonText.add("移動");
//		buttonText.add("攻撃");
//		buttonText.add("待機");
//		buttonText.add("キャンセル");
//		mSelectMenuList.setAdapter(new ArrayAdapter<String>(
//				mMainActivity, android.R.layout.simple_list_item_1, buttonText));
//	}
//	
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
//			mGameState = GameStateType.PLAYER_ATTACK;
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
