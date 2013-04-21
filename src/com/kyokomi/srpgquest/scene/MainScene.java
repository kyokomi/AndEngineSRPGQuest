package com.kyokomi.srpgquest.scene;

import java.util.List;

import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackInOut;

import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.entity.MItemEntity;
import com.kyokomi.core.logic.ActorPlayerLogic;
import com.kyokomi.core.logic.MapBattleRewardLogic;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.CommonWindowRectangle;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.core.utils.JsonUtil;
import com.kyokomi.srpgquest.logic.TalkLogic;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.layer.CutInLayer;
import com.kyokomi.srpgquest.layer.CutInLayer.ICutInCallback;
import com.kyokomi.srpgquest.layer.MapBattleClearConditionTouchLayer;
import com.kyokomi.srpgquest.layer.MapBattleSelectMenuLayer;
import com.kyokomi.srpgquest.layer.ScenarioStartCutInTouchLayer;
import com.kyokomi.srpgquest.constant.MapBattleCutInLayerType;
import com.kyokomi.srpgquest.manager.GameManager;
import com.kyokomi.srpgquest.manager.GameManager.SRPGGameManagerListener;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.sprite.ActorSprite;
import com.kyokomi.srpgquest.sprite.CursorRectangle;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle.PlayerStatusRectangleType;
import com.kyokomi.srpgquest.utils.SRPGSpriteUtil;

public class MainScene extends SrpgBaseScene implements IOnSceneTouchListener {
	/**
	 * ゲームパート
	 * @author kyokomi
	 */
	public enum GamePartType {
		/** ノベルパート */
		NOVEL_PART(1),
		/** SRPGパート */
		SRPG_PART(2),
		/** リザルトパート */
		RESULT_PART(3),
		;
		private Integer value;
		private GamePartType(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
	}
	
	public MainScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
		
		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}
	
	/**
	 * サウンド周りの準備
	 */
	@Override
	public void prepareSoundAndMusic() {
		
	}

	@Override
	public void initSoundAndMusic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * キーイベント制御
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
			return true;
		}
		return false;
	}
	
	// ------------------------------------------------------------------
	// ゲーム進行関連
	// ------------------------------------------------------------------
	private GamePartType mGamePartType;
	
	/**
	 * ゲーム起動時初期処理
	 */
	@Override
	public void init() {
		SaveDataDto saveDataDto = getBaseActivity().getGameController().
				createSaveDataDto(this);
		init(saveDataDto);
	}
	public void init(SaveDataDto saveDataDto) {
		// タッチイベントを初期化
		setOnSceneTouchListener(null);
		
		// セーブを読み込み
		if (saveDataDto == null) {
			getBaseActivity().backToInitial();
			return;
		}
		switch (saveDataDto.getSceneType()) {
		case SCENE_TYPE_NOVEL:
			mGamePartType = GamePartType.NOVEL_PART;
			initNovel(saveDataDto);
			break;
		case SCENE_TYPE_MAP:
			mGamePartType = GamePartType.SRPG_PART;
			initMap(saveDataDto);
			break;
		case SCENE_TYPE_RESULT:
			mGamePartType = GamePartType.RESULT_PART;
			initResult(saveDataDto);
			break;
		default:
			getBaseActivity().backToInitial();
			return;
		}
		// タッチイベント登録
		setOnSceneTouchListener(this);
	}
	
	/**
	 * 次シナリオへ
	 */
	public void nextScenario() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildByIndex(i).getTag() == FPS_TAG) {
				continue;
			}
			// タッチの検知も無効にする
			if (getChildByIndex(i) instanceof ButtonSprite) {
				unregisterTouchArea((ButtonSprite) getChildByIndex(i));
			}
			detachEntity(getChildByIndex(i));
		}
		// セーブAnd次シナリオへ進行
		getBaseActivity().getGameController().nextScenarioAndSave(this);
		init();
	}
	
	/**
	 * 画面タッチイベント
	 * プレイ中のパートに振り分ける
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// 共通タッチイベント
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		switch (mGamePartType) {
		case NOVEL_PART:
			touchEventNovelPart(pScene, pSceneTouchEvent);
			break;
		case SRPG_PART:
			touchEventSRPGPart(pScene, pSceneTouchEvent);
			break;
		case RESULT_PART:
			touchEventResultPart(pScene, pSceneTouchEvent);
			break;
		default:
			break;
		}
		return false;
	}
	
	// ------------------------------------------------------------------
	// ノベルパート関連
	// ------------------------------------------------------------------
	
	/**
	 * ノベルパートの初期化処理
	 */
	private void initNovel(SaveDataDto saveDataDto) {
		// 会話内容取得
		TalkLogic talkLogic = new TalkLogic();
		List<PlayerTalkDto> talks = talkLogic.getTalkDtoList(this,
				saveDataDto.getScenarioNo(), 
				saveDataDto.getSeqNo());
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = talkLogic.getTalkFaceSparse(this, talks);
		// 会話レイヤー作成
		TalkLayer talkLayer = new TalkLayer(this);
		talkLayer.initTalk(actorFaces, talks);
		talkLayer.hide();
		talkLayer.setZIndex(LayerZIndexType.TALK_LAYER.getValue());
		talkLayer.setTag(TALK_LAYER_TAG);
		attachChild(talkLayer);
		
		// まずは章開始カットイン
		if (saveDataDto.getSeqNo().intValue() == 1) {
			ScenarioStartCutInTouchLayer scenarioStartCutInTouchLayer = 
					new ScenarioStartCutInTouchLayer(this, saveDataDto);
			attachChild(scenarioStartCutInTouchLayer);
			scenarioStartCutInTouchLayer.showTouchLayer(this);
		} else {
			talkLayer.nextTalk();
		}
	}
	
	/**
	 * ノベルパートのタッチイベント
	 */
	private void touchEventNovelPart(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			ScenarioStartCutInTouchLayer startTouchLayer = (ScenarioStartCutInTouchLayer) getChildByTag(
					ScenarioStartCutInTouchLayer.TAG);
			TalkLayer talkLayer = (TalkLayer) getChildByTag(TALK_LAYER_TAG);
			if (startTouchLayer != null && startTouchLayer.isTouchLayer(x, y)) {
				// タップで消える
				startTouchLayer.hideTouchLayer((KeyListenScene) pScene);
				// 会話を開始
				if (talkLayer != null) {
					talkLayer.nextTalk();
				}
				detachEntity(startTouchLayer);
				
			} else if (talkLayer != null && talkLayer.contains(x, y)) {
				// TODO: SE再生
				
				if (talkLayer.isNextTalk()) {
					talkLayer.nextTalk();
					
				} else {
					talkLayer.hide();
					// 次の会話がなくなれば、会話レイヤーを開放
					detachEntity(talkLayer);
					
					// ノベルパート終了
					endNovelPart();
				}
			}
		}
	}
	
	private void endNovelPart() {
		// 次のシナリオへ
		nextScenario();
	}
	
	// ------------------------------------------------------------------
	// SRPGマップバトル関連
	// ------------------------------------------------------------------
	/** ゲーム管理クラス */
	private GameManager mGameManager;
	private SRPGGameManagerListener mSrpgGameManagerListener = new SRPGGameManagerListener() {
		@Override
		public ActorPlayerDto createPlayer(int seqNo, int playerId, MapPoint mapPoint, float size) {
			ActorPlayerDto actorPlayerDto = mActorPlayerLogic.createActorPlayerDto(MainScene.this, playerId);
			createPlayerSprite(seqNo, actorPlayerDto, mapPoint, size);
			return actorPlayerDto;
		}
		
		@Override
		public ActorPlayerDto createEnemy(int seqNo, int enemyId, MapPoint mapPoint, float size) {
			ActorPlayerDto actorPlayerDto = mActorPlayerLogic.createActorPlayerDto(MainScene.this, enemyId);
			createEnemySprite(seqNo, actorPlayerDto, mapPoint, size);
			return actorPlayerDto;
		}

		@Override
		public void createObstacle(int obstractId, MapPoint mapPoint, float size) {
			createObstacleSprite(obstractId, mapPoint, size);
		}

		@Override
		public void refresh() {
			sortChildren();
		}

		@Override
		public void createMoveCursors(List<MapPoint> cursorMapPointList) {
			for (MapPoint mapPoint : cursorMapPointList) {
				createMoveCursorSprite(mapPoint);
			}
		}

		@Override
		public void createAttackCursors(List<MapPoint> cursorMapPointList) {
			for (MapPoint mapPoint : cursorMapPointList) {
				createAttackCursorSprite(mapPoint);
			}
		}

		@Override
		public void touchedCusor(MapPoint mapPoint) {
			touchedCusorRectangle(mapPoint);
			
		}

		@Override
		public void hideCursor() {
			hideCursorSprite();
		}

		@Override
		public void showPlayerWinCutIn(final ICutInCallback cutInCallback) {
			showCutIn(MapBattleCutInLayerType.PLAYER_WIN_CUTIN, new ICutInCallback() {
				@Override
				public void doAction() {
					clearMapBattle();
					cutInCallback.doAction();					
				}
			});
		}

		@Override
		public void showGameOverCutIn(final ICutInCallback cutInCallback) {
			showCutIn(MapBattleCutInLayerType.GAME_OVER_CUTIN, new ICutInCallback() {
				@Override
				public void doAction() {
					gameOverMapBattle();
					cutInCallback.doAction();					
				}
			});
		}

		@Override
		public void showPlayerTurnCutIn(final List<Integer> playerSeqNoList, final ICutInCallback cutInCallback) {
			showCutIn(MapBattleCutInLayerType.PLAYER_TURN_CUTIN, new ICutInCallback() {
				@Override
				public void doAction() {
					for (Integer seqNo : playerSeqNoList) {
						startWalkingPlayerAnimation(seqNo);
					}
					cutInCallback.doAction();					
				}
			});
		}
		@Override
		public void showEnemyTurnCutIn(final List<Integer> enemySeqNoList, final ICutInCallback cutInCallback) {
			showCutIn(MapBattleCutInLayerType.PLAYER_TURN_CUTIN, new ICutInCallback() {
				@Override
				public void doAction() {
					for (Integer seqNo : enemySeqNoList) {
						startWalkingEnemyAnimation(seqNo);
					}
					cutInCallback.doAction();					
				}
			});
		}
	};
	
	/** アクターロジック */
	private ActorPlayerLogic mActorPlayerLogic;
	
	/** 敵のターンタイマー. */
	private TimerHandler mEnemyTurnUpdateHandler;
	
	public TimerHandler getEnemyTurnUpdateHandler() {
		return mEnemyTurnUpdateHandler;
	}
	
	MapBattleSelectMenuLayer mMapBattleSelectMenuLayer;
	
	/**
	 * SRPGマップバトルパートの初期化処理
	 */
	private void initMap(SaveDataDto saveDataDto) {
		// 初期化
		mActorPlayerLogic = new ActorPlayerLogic();
		
		// 背景
		initBackground();
		// ダメージテキスト初期化
		initDamageText();
		// グリッド線表示
		showGrid();
		
		// タッチレイヤー初期化
		MapBattleClearConditionTouchLayer mMapBattleTouchLayer = new MapBattleClearConditionTouchLayer(this);
		mMapBattleTouchLayer.setTag(MapBattleClearConditionTouchLayer.TAG);
		attachChild(mMapBattleTouchLayer);
		
		// カットイン初期化
		attachWithCreateCutInLayer(MapBattleCutInLayerType.PLAYER_TURN_CUTIN);
		attachWithCreateCutInLayer(MapBattleCutInLayerType.PLAYER_WIN_CUTIN);
		attachWithCreateCutInLayer(MapBattleCutInLayerType.ENEMY_TURN_CUTIN);
		attachWithCreateCutInLayer(MapBattleCutInLayerType.GAME_OVER_CUTIN);
		
		// メニュー初期化
		mMapBattleSelectMenuLayer = new MapBattleSelectMenuLayer(this, new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				touchSprite(mMapBattleSelectMenuLayer.getMenuRectangle().getX() + pTouchAreaLocalX, 
						mMapBattleSelectMenuLayer.getMenuRectangle().getY() + pTouchAreaLocalY);
				
				mGameManager.touchMenuBtnEvent(pButtonSprite.getTag());
			}
		});

		// マップ情報を読み込む
		MapBattleInfoDto mMapBattleInfoDto = new MapBattleInfoDto();
		mMapBattleInfoDto.createMapJsonData(saveDataDto.getSceneId(), 
				JsonUtil.toJson(getBaseActivity(), "map/"+ saveDataDto.getSceneId()));
		// ゲーム開始
		mGameManager = new GameManager(this, mSrpgGameManagerListener);
		mGameManager.mapInit(mMapBattleInfoDto); // 10 x 10 スケール1倍のグリッドマップ
		
		// プレイヤー情報ができてから呼び出さないといけないので注意
		// 会話レイヤーを生成
		initTalk(saveDataDto.getScenarioNo(), saveDataDto.getSeqNo());
		
	}
	
	/**
	 * 背景表示.
	 */
	private void initBackground() {
		Sprite backgroundSprite = getResourceSprite("bk/main_bg.jpg");
		backgroundSprite.setSize(getWindowWidth(), getWindowHeight());
		backgroundSprite.setZIndex(-1);
		attachChild(backgroundSprite);
	}
	
	private static final int DAMAGE_TEXT_TAG = 1000;
	/**
	 * ダメージテキスト初期化
	 */
	private void initDamageText() {
		Text damageText = new Text(0, 0, getFont(), "00000", getBaseActivity().getVertexBufferObjectManager());
		damageText.setColor(Color.TRANSPARENT);
		damageText.setZIndex(LayerZIndexType.TEXT_LAYER.getValue());
		damageText.setTag(DAMAGE_TEXT_TAG); //TODO: TAG管理
		attachChild(damageText);
	}
	
	/**
	 * グリッド表示
	 */
	private void showGrid() {
		int base = 40;
		int baseGrid = 0;
		
		for (int x = -10 ; x < 20; x++) {
			final Line line = new Line(base * x, 0, (x * base) + baseGrid, getWindowHeight(), 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			attachChild(line);
		}
		
		for (int y = -10 ; y < 20; y++) {
			final Line line = new Line(0, (base * y), getWindowWidth(), (y * base) - (baseGrid / 2), 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			attachChild(line);
		}
	}
	
	// ----------------- actor ----------------------
	private static final int OBSTACLE_TAG_START = 10000;
	private int obstacleIndex = 0;
	
	/**
	 * プレイヤーキャラ描画.
	 * @param playerSeqNo
	 * @param imageId
	 * @param mapPoint
	 */
	private void createPlayerSprite(int playerSeqNo, ActorPlayerDto playerActor, MapPoint mapPoint, float size) {
		ActorSprite player = new ActorSprite(playerActor, this, 0, 0, size, size, 1.0f);
		
		player.setPlayerToDefaultPosition();
		player.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		player.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		player.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		player.setTag(playerSeqNo);
		attachChild(player);
		
		PlayerStatusRectangle playerStatusRect = initStatusWindow(player, 0);
		playerStatusRect.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
		playerStatusRect.setColor(Color.BLUE);
		playerStatusRect.setAlpha(0.5f);
	}
	
	/**
	 * 敵キャラ描画.
	 * @param enemySeqNo
	 * @param enemyActor
	 * @param mapPoint
	 * @param size
	 */
	private void createEnemySprite(int enemySeqNo, ActorPlayerDto enemyActor, MapPoint mapPoint, float size) {
		ActorSprite enemy = new ActorSprite(enemyActor, this, 0, 0, size, size, 1.0f);
		
		enemy.setPlayerToDefaultPosition();
		enemy.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		enemy.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		enemy.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		enemy.setTag(enemySeqNo);
		attachChild(enemy);
		
		PlayerStatusRectangle enemyStatusRect = initStatusWindow(enemy, 0);
		enemyStatusRect.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
		enemyStatusRect.setColor(Color.RED);
		enemyStatusRect.setAlpha(0.5f);
	}
	
	/**
	 * 障害物描画.
	 * @param mapPoint
	 */
	private void createObstacleSprite(int currentTileIndex, MapPoint mapPoint, float size) {
		Sprite obstacle = getResourceSprite("icon_ob.png");
		obstacle.setPosition(mapPoint.getX(), mapPoint.getY());
		obstacle.setSize(size, size);
		obstacle.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		obstacle.setTag(OBSTACLE_TAG_START + obstacleIndex); obstacleIndex++;
		attachChild(obstacle);
	}
	
	/**
	 * ステータスウィンドウ初期化.
	 * @param actorSprite
	 * @param y
	 * @return
	 */
	private PlayerStatusRectangle initStatusWindow(ActorSprite actorSprite, float y) {
		if (actorSprite == null) {
			return null;
		}
		PlayerStatusRectangle mPlayerStatusRectangle = actorSprite.getPlayerStatusRectangle();
		if (mPlayerStatusRectangle == null) {
			mPlayerStatusRectangle = actorSprite.createPlayerStatusWindow(
					this, getFont(), 
					getWindowWidth() / 2, y, 
					getWindowWidth() / 2, getWindowHeight() / 2);
			mPlayerStatusRectangle.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
			CommonWindowRectangle commonWindowRectangle = new CommonWindowRectangle(
					0, 0, 
					mPlayerStatusRectangle.getWidth(), 
					mPlayerStatusRectangle.getHeight() / 2,
					Color.TRANSPARENT, 0.0f, this);
			mPlayerStatusRectangle.attachChild(commonWindowRectangle);
		}
		return mPlayerStatusRectangle;
	}
	
	/**
	 * プレイヤーステータス更新.
	 * @param playerSeqNo
	 */
	public void refreshPlayerStatusWindow(int playerSeqNo) {
		ActorSprite player = getActorSprite(playerSeqNo);
		player.getPlayerStatusRectangle().refresh();
	}
	/**
	 * 敵ステータス更新.
	 * @param enemySeqNo
	 */
	public void refreshEnemyStatusWindow(int enemySeqNo) {
		ActorSprite enemy = getActorSprite(enemySeqNo);
		enemy.getPlayerStatusRectangle().refresh();
	}
	/**
	 * プレイヤーキャラ消去.
	 * @param playerSeqNo
	 */
	public void removePlayer(final int playerSeqNo) {
		// 別スレッドで削除
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				ActorSprite player = getActorSprite(playerSeqNo);
				player.detachChildren();
				player.detachSelf();
			}
		});
	}
	/**
	 * 敵キャラ消去.
	 * @param enemyId
	 */
	public void removeEnemy(final int enemyId) {
		// 別スレッドで削除
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				ActorSprite enemy = getActorSprite(enemyId);
				enemy.detachChildren();
				enemy.detachSelf();
			}
		});
	}
	
	private ActorSprite getActorSprite(int playerSeqNo) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if (getChildByIndex(i) instanceof ActorSprite) {
				if (playerSeqNo == getChildByIndex(i).getTag()) {
					return (ActorSprite) getChildByIndex(i);
				}
			}
		}
		return null;
	}
	
	// ------------------------ カーソル --------------------------
	/**
	 * 移動カーソル描画.
	 * @param mapPoint
	 */
	public void createMoveCursorSprite(MapPoint mapPoint) {
		CursorRectangle cursorRectangle = createCursorSprite(mapPoint, Color.GREEN);
		cursorRectangle.setZIndex(LayerZIndexType.MOVECURSOR_LAYER.getValue());
	}
	/**
	 * 攻撃カーソル描画.
	 * @param mapPoint
	 */
	public void createAttackCursorSprite(MapPoint mapPoint) {
		CursorRectangle cursorRectangle = createCursorSprite(mapPoint, Color.YELLOW);
		cursorRectangle.setZIndex(LayerZIndexType.ATTACKCURSOR_LAYER.getValue());
	}
	/**
	 * カーソル選択.
	 */
	public void touchedCusorRectangle(final MapPoint mapPoint) {
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				int count = getChildCount();
				for (int i = 0; i < count; i++) {
					if (getChildByIndex(i) instanceof CursorRectangle) {
						CursorRectangle cursorRectangle = (CursorRectangle) getChildByIndex(i);
						if (mapPoint.isMuchMapPoint(cursorRectangle.getmMapPointX(), 
								cursorRectangle.getmMapPointY())) {
							cursorRectangle.setColor(Color.BLUE);
							break;
						}
					}
				}
				sortChildren();
			}
		});
	}
	/**
	 * カーソル描画.
	 * @param mapPoint
	 */
	private CursorRectangle createCursorSprite(MapPoint mapPoint, Color color) {
		// 移動または攻撃可能範囲のカーソル
		CursorRectangle cursor = new CursorRectangle(
				mapPoint.getMapPointX(), mapPoint.getMapPointY(),
				mapPoint.getX(), mapPoint.getY(),
				mapPoint.getGridSize(), 
				mapPoint.getGridSize(), 
				getBaseActivity().getVertexBufferObjectManager());
		cursor.setColor(color);
		cursor.setAlpha(0.2f);
		// 点滅表示設定
		cursor.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new AlphaModifier(0.5f, 0.2f, 0.6f),
				new AlphaModifier(0.5f, 0.6f, 0.2f)
				)));
		attachChild(cursor);
		
		return cursor;
	}
	
	/**
	 * カーソル消去.
	 */
	public void hideCursorSprite() {
		// 別スレッドで削除
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < getChildCount(); i++) {
					if (getChildByIndex(i) instanceof CursorRectangle) {
						detachEntity(getChildByIndex(i));
					}
				}
			}
		});
	}

	// ----------------- アニメーション　演出 -------------------
	
	/**
	 * アニメーション系の共通コールバック.
	 * @author kyokomi
	 *
	 */
	public interface IAnimationCallback {
		public void doAction();
	}
	
	/**
	 * プレイヤー移動アニメーション.
	 * @param playerSeqNo
	 * @param moveMapPointList
	 */
	public void movePlayerAnimation(int playerSeqNo, List<MapPoint> moveMapPointList, 
			final IAnimationCallback animationCallback) {
		ActorSprite ActorSprite = getActorSprite(playerSeqNo);
		ActorSprite.move(1.0f, moveMapPointList, new IEntityModifier.IEntityModifierListener() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				// コールバック
				animationCallback.doAction();
			}
		});
	}
	/**
	 * 敵移動アニメーション.
	 * @param enemySeqNo
	 * @param moveMapPointList
	 */
	public void moveEnemyAnimation(int enemySeqNo, List<MapPoint> moveMapPointList, 
			final IAnimationCallback animationCallback) {
		ActorSprite enemySprite = getActorSprite(enemySeqNo);
		enemySprite.move(1.0f, moveMapPointList, new IEntityModifier.IEntityModifierListener() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				// コールバック
				animationCallback.doAction();
			}
		});
	}
	/**
	 * プレイヤー歩行スタート.
	 * @param playerSeqNo
	 */
	public void startWalkingPlayerAnimation(int playerSeqNo) {
		ActorSprite ActorSprite = getActorSprite(playerSeqNo);
		ActorSprite.setPlayerToDefaultPosition();
	}
	/**
	 * プレイヤー歩行停止.
	 * @param playerSeqNo
	 */
	public void stopWalkingPlayerAnimation(int playerSeqNo) {
		ActorSprite ActorSprite = getActorSprite(playerSeqNo);
		ActorSprite.setPlayerToDefaultPositionStop();
	}

	/**
	 * エネミー歩行スタート.
	 * @param enemySeqNo
	 */
	public void startWalkingEnemyAnimation(int enemySeqNo) {
		ActorSprite enemySprite = getActorSprite(enemySeqNo);
		enemySprite.setPlayerToDefaultPosition();
	}
	/**
	 * エネミー歩行停止.
	 * @param enemySeqNo
	 */
	public void stopWalkingEnemyAnimation(int enemySeqNo) {
		ActorSprite enemySprite = getActorSprite(enemySeqNo);
		enemySprite.setPlayerToDefaultPositionStop();
	}
	
	/**
	 * 指定のMapPointの位置にダメージ値をテキストで拡大アニメーション表示する.
	 * 表示が終わったら消えます。
	 * @param damage ダメージ値
	 * @param mapPoint 表示位置
	 */
	public void showDamageText(final int damage, final MapPoint mapPoint) {
		
//		getMediaManager().play(SoundType.ATTACK_SE);
		final Text damageText = (Text) getChildByTag(DAMAGE_TEXT_TAG);
		
		damageText.setScale(0.5f);
		damageText.setX(mapPoint.getX());
		damageText.setY(mapPoint.getY());
		damageText.setText(String.valueOf(damage));
		damageText.setColor(Color.WHITE);
		
		damageText.registerEntityModifier(new SequenceEntityModifier(
				new ParallelEntityModifier(
					new ScaleModifier(0.5f, 0.5f, 2.0f, EaseBackInOut.getInstance()),
					new SequenceEntityModifier(
							new MoveModifier(0.25f, damageText.getX(), damageText.getX(), 
									damageText.getY(), damageText.getY() - 15, 
									EaseBackInOut.getInstance()),
							new MoveModifier(0.25f, damageText.getX(), damageText.getX(), 
									damageText.getY() - 15, damageText.getY(), 
									EaseBackInOut.getInstance()))
					),
				new DelayModifier(0.2f, new IEntityModifier.IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						damageText.setColor(Color.TRANSPARENT);
					}
				})));
	}
	
	// ---------------- メニュー -------------------
	public void showSelectMenu(boolean isAttackDone, boolean isMovedDone, MapPoint mapPoint) {
		mMapBattleSelectMenuLayer.showSelectMenu(this, 
				mapPoint.getX(), mapPoint.getY(), 
				isAttackDone, isMovedDone);
	}
	public void hideSelectMenu() {
		mMapBattleSelectMenuLayer.hideSelectMenu();
	}
	
	// ---------------- カットイン関連 ----------------------
	/**
	 * カットイン取得.
	 * @param pMapBattleCutInLayerType
	 * @return カットイン
	 */
	private CutInLayer getCutInLayer(MapBattleCutInLayerType pMapBattleCutInLayerType) {
		return (CutInLayer) getChildByTag(pMapBattleCutInLayerType.getValue());
	}
	
	/**
	 * カットインの追加
	 * すでに追加済みの場合、解除してから追加します
	 * @param pMapBattleCutInLayerType
	 */
	private void attachWithCreateCutInLayer(MapBattleCutInLayerType pMapBattleCutInLayerType) {
		CutInLayer cutInLayer = getCutInLayer(pMapBattleCutInLayerType);
		if (cutInLayer != null) {
			cutInLayer.detachChildren();
			cutInLayer.detachSelf();
		}
		cutInLayer = new CutInLayer(getDispStartX(), getDispStartY(), 
				getWindowWidth(), getWindowHeight(), this, pMapBattleCutInLayerType);
		attachChild(cutInLayer);
	}
	
	/**
	 * カットイン表示
	 * TODO: GameManegerから呼ばれます
	 * @param pMapBattleCutInLayerType
	 * @param pAnimationCallback
	 */
	public void showCutIn(MapBattleCutInLayerType pMapBattleCutInLayerType, final ICutInCallback pCutInCallback) {
		// カットイン表示
		getCutInLayer(pMapBattleCutInLayerType).showCutInSprite(2.0f, pCutInCallback);
	}
	// --------------- 会話パート用 --------------------
	private static final int TALK_LAYER_TAG = 999;
	private void initTalk(int scenarioNo, int seqNo) {
		TalkLogic talkLogic = new TalkLogic();
		// 会話内容取得
		List<PlayerTalkDto> talks = talkLogic.getTalkDtoList(this, scenarioNo, seqNo);
		// 顔リスト作成
		SparseArray<TiledSprite> actorFaces = talkLogic.getTalkFaceSparse(this, talks);
		// 会話レイヤー作成
		TalkLayer talkLayer = new TalkLayer(this);
		talkLayer.initTalk(actorFaces, talks);
		talkLayer.hide();
		talkLayer.setZIndex(LayerZIndexType.TALK_LAYER.getValue());
		talkLayer.setTag(TALK_LAYER_TAG);
		attachChild(talkLayer);
		// 会話表示
		talkLayer.nextTalk();
	}
	
	// --------------- ステータスウィンドウ --------------
	private static final int PLAYER_STATUS_WINDOW_TAG = 2000;
	private static final int ENEMY_STATUS_WINDOW_TAG = 2001;
	public void showPlayerStatusWindow(int playerSeqNo, float x) {
		PlayerStatusRectangle mPlayerStatusRect = (PlayerStatusRectangle) getChildByTag(PLAYER_STATUS_WINDOW_TAG);
		if (mPlayerStatusRect != null) {
			detachChild(mPlayerStatusRect);
		}
		// エネミーが表示されていたら下に表示
		PlayerStatusRectangle mEnemyStatusRect = (PlayerStatusRectangle) getChildByTag(ENEMY_STATUS_WINDOW_TAG);
		float y = 0;
		if (mEnemyStatusRect != null && mEnemyStatusRect.isVisible()) {
			y = mEnemyStatusRect.getY() + mEnemyStatusRect.getHeight();
		}
		mPlayerStatusRect = getActorSprite(playerSeqNo).getPlayerStatusRectangle();
		if (mPlayerStatusRect != null) {
			mPlayerStatusRect.setTag(PLAYER_STATUS_WINDOW_TAG);
			mPlayerStatusRect.show(PlayerStatusRectangleType.MINI_STATUS);
			mPlayerStatusRect.setX(x);
			mPlayerStatusRect.setY(y);
			attachChild(mPlayerStatusRect);	
		}
		sortChildren();
	}
	public void showEnemyStatusWindow(int enemySeqNo) {
		PlayerStatusRectangle mEnemyStatusRect = (PlayerStatusRectangle) getChildByTag(ENEMY_STATUS_WINDOW_TAG);
		if (mEnemyStatusRect != null) {
			detachChild(mEnemyStatusRect);
		}
		// プレイヤーが表示されていたら下に表示
		PlayerStatusRectangle mPlayerStatusRect = (PlayerStatusRectangle) getChildByTag(PLAYER_STATUS_WINDOW_TAG);
		float y = 0;
		if (mPlayerStatusRect != null && mPlayerStatusRect.isVisible()) {
			y = mPlayerStatusRect.getY() + mPlayerStatusRect.getHeight();
		}
		mEnemyStatusRect = getActorSprite(enemySeqNo).getPlayerStatusRectangle();
		if (mEnemyStatusRect != null) {
			mEnemyStatusRect.setTag(ENEMY_STATUS_WINDOW_TAG);
			mEnemyStatusRect.show(PlayerStatusRectangleType.MINI_STATUS);
			mEnemyStatusRect.setY(y);
			attachChild(mEnemyStatusRect);
		}
		sortChildren();
	}
	public void hidePlayerStatusWindow() {
		PlayerStatusRectangle mPlayerStatusRect = (PlayerStatusRectangle) getChildByTag(PLAYER_STATUS_WINDOW_TAG);
		if (mPlayerStatusRect != null) {
			mPlayerStatusRect.hide();
		}
	}
	public void hideEnemyStatusWindow() {
		PlayerStatusRectangle mEnemyStatusRect = (PlayerStatusRectangle) getChildByTag(ENEMY_STATUS_WINDOW_TAG);
		if (mEnemyStatusRect != null) {
			mEnemyStatusRect.hide();
		}
	}
	// ------ タッチイベント ------
	private void touchEventSRPGPart(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			
			// TODO: 一旦会話はGameManager外にする
			TalkLayer mTalkLayer = (TalkLayer) getChildByTag(TALK_LAYER_TAG);
			MapBattleClearConditionTouchLayer mMapBattleTouchLayer = 
					(MapBattleClearConditionTouchLayer) getChildByTag(MapBattleClearConditionTouchLayer.TAG);
			
			if (mTalkLayer != null && mTalkLayer.contains(x, y)) {
				
//				getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				if (mTalkLayer.isNextTalk()) {
					mTalkLayer.nextTalk();
					
				} else {
					mTalkLayer.hide();
					// 次の会話がなくなれば、会話レイヤーを開放
					detachEntity(mTalkLayer);
					mTalkLayer = null;
					
					// 勝利条件表示
//					getMediaManager().play(MusicType.BATTLE1_BGM);
					mMapBattleTouchLayer.showTouchLayer(this);
				}
				
			} else if (mMapBattleTouchLayer.isTouchLayer(x, y)) {
				
//				getMediaManager().play(SoundType.BTN_PRESSED_SE);
				
				// 勝利条件を非表示にする
				mMapBattleTouchLayer.hideTouchLayer(this);
				
				// ゲーム開始
				mGameManager.gameStart();
				
			} else {
				// タッチイベント振り分け処理を呼ぶ
				mGameManager.onTouchMapItemEvent(x, y);
			}
		}
	}
	
	public void clearMapBattle() {
		int mapBattleId = getBaseActivity().getGameController().createSaveDataDto(this).getSceneId();
		
		// 報酬振込み(アイテムだけ)
		MapBattleRewardLogic mapBattleRewardLogic = new MapBattleRewardLogic();
		MapBattleRewardDto mapBattleRewardDto = mapBattleRewardLogic.addMapBattleReward(this, 
				getBaseActivity().getGameController().getSaveId(), mapBattleId);
		// セーブ更新(DB更新はしません。シナリオ進行時にまとめて更新してもらいます）
		getBaseActivity().getGameController().addExp(mapBattleRewardDto.getTotalExp());
		getBaseActivity().getGameController().addGold(mapBattleRewardDto.getTotalGold());
				
		// 次のシナリオへ
		endSRPGPart();
	}
	public void gameOverMapBattle() {
//		getMediaManager().stopPlayingMusic();
		// TODO: タイトルへ？
		showScene(new InitialScene(getBaseActivity()));
	}
	
	private void endSRPGPart() {
		// 次のシナリオへ
		nextScenario();
	}
	
	// ------------------------------------------------------------------
	// リザルト画面パート関連
	// ------------------------------------------------------------------
	
	/**
	 * リザルト画面パートの初期化処理
	 */
	private void initResult(SaveDataDto saveDataDto) {
		
		// フォントの作成
		Font defaultFont = createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font largeFont = createFont(Typeface.SANS_SERIF, 36, Color.WHITE);
		
		MapBattleRewardLogic mapBattleRewardLogic = new MapBattleRewardLogic();
		MapBattleRewardDto mapBattleRewardDto = mapBattleRewardLogic.createMapBattleRewardDto(
				this, saveDataDto.getSceneId());

		// 背景
		Sprite backImage = getResourceSprite("bk/back_mori2.jpg");
		backImage.setSize(getWindowWidth(), getWindowHeight());
		attachChild(backImage);

		// 共通ウィンドウを作成
		CommonWindowRectangle comonWindowRectangle = new CommonWindowRectangle(
				getWindowWidth() / 4, 5,
				getWindowWidth() / 2, getWindowHeight() / 2 + getWindowHeight() / 4,
				this);
		attachChild(comonWindowRectangle);
		
		// ---------------------------------------------------------------
		// 獲得物表示
		// ---------------------------------------------------------------
		Text titleText = createWithAttachText(largeFont, "- Result -");
		placeToCenterX(titleText, 20);
		
		float titleBaseX = (getWindowWidth() / 2) - (getWindowWidth() / 6);
		Text expTitleText = createWithAttachText(defaultFont, "獲得経験値:");
		expTitleText.setPosition(titleBaseX, titleText.getY() + titleText.getHeight() + 20);
		
		Text expText = createWithAttachText(defaultFont, mapBattleRewardDto.getTotalExp() + " Exp");
		expText.setPosition(getWindowWidth() / 2, titleText.getY() + titleText.getHeight() + 20);

		Text goldTitleText = createWithAttachText(defaultFont, "獲得ゴールド:");
		goldTitleText.setPosition(titleBaseX, expText.getY() + expText.getHeight() + 20);
		
		Text goldText = createWithAttachText(defaultFont, mapBattleRewardDto.getTotalGold() + " Gold");
		goldText.setPosition(getWindowWidth() / 2, expText.getY() + expText.getHeight() + 20);
		
		Text itemTitleText = createWithAttachText(defaultFont, "獲得アイテム:");
		itemTitleText.setPosition(titleBaseX, goldText.getY() + goldText.getHeight() + 20);
		if (mapBattleRewardDto.getItemList().isEmpty()) {
			Text notGetItemText = createWithAttachText(defaultFont, "なし");
			placeToCenterX(notGetItemText, itemTitleText.getY() + itemTitleText.getHeight() + 20);
		} else {
			Rectangle itemIconRectangle = new Rectangle(0, 
					itemTitleText.getY() + itemTitleText.getHeight() + 20, 
					getWindowWidth() / 4, getWindowHeight() / 4, 
					getBaseActivity().getVertexBufferObjectManager());
			itemIconRectangle.setColor(Color.TRANSPARENT);
			attachChild(itemIconRectangle);
			
			float baseX = 0;
			float baseY = 0;
			for (MItemEntity itemEntity : mapBattleRewardDto.getItemList()) {
				// アイコン
				TiledSprite itemIconTiled = SRPGSpriteUtil.getIconSetTiledSprite(this);
				itemIconTiled.setCurrentTileIndex(itemEntity.getItemImageId());
				itemIconTiled.setPosition(baseX, baseY);
				itemIconRectangle.attachChild(itemIconTiled);
				baseX += itemIconTiled.getWidth() + 5;
				
				// テキスト
				Text itemText = createText(defaultFont, itemEntity.getItemName());
				itemText.setPosition(baseX, baseY);
				itemIconRectangle.attachChild(itemText);
				baseX += itemText.getWidth() + 5;
				
				baseY += itemIconTiled.getHeight() + 5;
				baseX = 0;
			}
			placeToCenterX(itemIconRectangle, itemIconRectangle.getY());
		}
		
		// 次へボタン生成
		ButtonSprite nextSceneButtonSprite = getResourceButtonSprite("btn/next_btn.png", "btn/next_btn_p.png");
		placeToCenterX(nextSceneButtonSprite, getWindowHeight() - nextSceneButtonSprite.getHeight() - 40);
		registerTouchArea(nextSceneButtonSprite);
		nextSceneButtonSprite.setOnClickListener(new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				// 次のシナリオへ
				nextScenario();
			}
		});
		attachChild(nextSceneButtonSprite);
	}

	private void touchEventResultPart(Scene pScene, TouchEvent pSceneTouchEvent) {
		
	}
	
	// ----------------------------------------------------------
	// 汎用
	// ----------------------------------------------------------
	
	private Text createWithAttachText(Font font, String textStr) {
		Text text = createText(font, textStr);
		attachChild(text);
		return text;
	}
	private Text createText(Font font, String textStr) {
		Text text = new Text(16, 16, font, textStr, 
				new TextOptions(HorizontalAlign.CENTER), 
				getBaseActivity().getVertexBufferObjectManager());
		return text;
	}
	
	private float getDispStartX() {
		return 0;
	}
	
	private float getDispStartY() {
		return 0;
	}
}
