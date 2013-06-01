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
import org.andengine.entity.shape.IAreaShape;
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

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.Log;
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
import com.kyokomi.core.utils.CollidesUtil;
import com.kyokomi.core.utils.JsonUtil;
import com.kyokomi.core.utils.CollidesUtil.TouchEventFlick;
import com.kyokomi.srpgquest.logic.TalkLogic;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.layer.CutInLayer;
import com.kyokomi.srpgquest.layer.CutInLayer.ICutInCallback;
import com.kyokomi.srpgquest.layer.ExpDistributionLayer;
import com.kyokomi.srpgquest.layer.MapBattleClearConditionTouchLayer;
import com.kyokomi.srpgquest.layer.MapBattleSelectMenuLayer;
import com.kyokomi.srpgquest.layer.ScenarioStartCutInTouchLayer;
import com.kyokomi.srpgquest.constant.MapBattleCutInLayerType;
import com.kyokomi.srpgquest.manager.GameManager;
import com.kyokomi.srpgquest.manager.GameManager.SRPGGameManagerListener;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.sprite.ActorSprite;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle.PlayerStatusRectangleType;
import com.kyokomi.srpgquest.utils.MapGridUtil;
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
	private static final int SPRITE_SIZE = 64;
	
	/** ゲーム管理クラス */
	private GameManager mGameManager;
	/** 敵のターンタイマー. */
	private TimerHandler mEnemyTurnUpdateHandler;
	
	private SRPGGameManagerListener mSrpgGameManagerListener = new SRPGGameManagerListener() {
		@Override
		public ActorPlayerDto createPlayer(int seqNo, int playerId, MapPoint mapPoint) {
			ActorPlayerDto actorPlayerDto = mActorPlayerLogic.createActorPlayerDto(MainScene.this, playerId);
			createPlayerSprite(seqNo, actorPlayerDto, mapPoint, SPRITE_SIZE);
			return actorPlayerDto;
		}
		
		@Override
		public ActorPlayerDto createEnemy(int seqNo, int enemyId, MapPoint mapPoint) {
			ActorPlayerDto actorPlayerDto = mActorPlayerLogic.createActorPlayerDto(MainScene.this, enemyId);
			createEnemySprite(seqNo, actorPlayerDto, mapPoint, SPRITE_SIZE);
			return actorPlayerDto;
		}

		@Override
		public void createObstacle(int obstractId, MapPoint mapPoint) {
			createObstacleSprite(obstractId, mapPoint, SPRITE_SIZE);
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
						ActorSprite actorSprite = getActorSprite(seqNo);
						actorSprite.setPlayerToDefaultPosition();
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
						ActorSprite enemySprite = getActorSprite(seqNo);
						enemySprite.setPlayerToDefaultPosition();
					}
					cutInCallback.doAction();					
				}
			});
		}

		@Override
		public void setEnemyTurnUpdateHandler(TimerHandler timerHandler) {
			mEnemyTurnUpdateHandler = timerHandler;
		}

		@Override
		public TimerHandler getEnemyTurnUpdateHandler() {
			return mEnemyTurnUpdateHandler;
		}

		@Override
		public void stopEnemyTurnTimer() {
			unregisterUpdateHandler(mEnemyTurnUpdateHandler);
		}

		@Override
		public void startEnemyTurnTimer() {
			registerUpdateHandler(mEnemyTurnUpdateHandler);
		}

		@Override
		public void removeEnemy(int enemyId) {
			detachEnemy(enemyId);
		}

		@Override
		public void removePlayer(int playerId) {
			detachPlayer(playerId);
		}

		@Override
		public void stopWalkingPlayerAnimation(int playerSeqNo) {
			ActorSprite ActorSprite = getActorSprite(playerSeqNo);
			ActorSprite.setPlayerToDefaultPositionStop();
		}

		@Override
		public void stopWalkingEnemyAnimation(int enemySeqNo) {
			ActorSprite enemySprite = getActorSprite(enemySeqNo);
			enemySprite.setPlayerToDefaultPositionStop();
		}

		/**
		 * プレイヤー歩行アニメーション
		 */
		@Override
		public void movePlayerAnimation(int playerSeqNo, List<MapPoint> moveMapPointList,
				final IAnimationCallback animationCallback) {
			ActorSprite ActorSprite = getActorSprite(playerSeqNo);
			// クォータービュー対応
			for (MapPoint mapPoint : moveMapPointList) {
				mapPoint.setX(
						mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (ActorSprite.getWidth() / 2) - (ActorSprite.getWidth() / 8)); 
				mapPoint.setY(
						mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - ActorSprite.getHeight() + (ActorSprite.getHeight() / 8));
			}
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
		 * 敵歩行アニメーション
		 */
		@Override
		public void moveEnemyAnimation(int enemySeqNo, List<MapPoint> moveMapPointList,
				final IAnimationCallback animationCallback) {
			ActorSprite enemySprite = getActorSprite(enemySeqNo);
			// クォータービュー対応
			for (MapPoint mapPoint : moveMapPointList) {
				mapPoint.setX(
						mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (enemySprite.getWidth() / 2) - (enemySprite.getWidth() / 8)); 
				mapPoint.setY(
						mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - enemySprite.getHeight() + (enemySprite.getHeight() / 8));
			}
			
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

		@Override
		public void showDamageText(int damage, final PointF dispPoint) {
//			getMediaManager().play(SoundType.ATTACK_SE);
			Rectangle baseMap = getBaseMap();
			final Text damageText = (Text) baseMap.getChildByTag(DAMAGE_TEXT_TAG);
			
			damageText.setScale(0.5f);
			// 頭の上くらいに表示
			damageText.setX(dispPoint.x + MapGridUtil.GRID_X / 2);
			damageText.setY(dispPoint.y - MapGridUtil.GRID_Y / 2);
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

		/**
		 * プレイヤーステータス更新.
		 */
		@Override
		public void refreshPlayerStatusWindow(int playerSeqNo) {
			getPlayerStatusRectangle(playerSeqNo).refresh();
		}

		/**
		 * 敵ステータス更新.
		 */
		@Override
		public void refreshEnemyStatusWindow(int enemySeqNo) {
			getPlayerStatusRectangle(enemySeqNo).refresh();
		}

		/**
		 * プレイヤーステータスウィンドウ表示
		 */
		@Override
		public void showPlayerStatusWindow(int playerSeqNo) {
			// エネミーが表示されていたら下に表示
			float y = 0;
			PlayerStatusRectangle playerStatusRect = getPlayerStatusRectangle(playerSeqNo);
			if (playerStatusRect != null) {
				playerStatusRect.show(PlayerStatusRectangleType.MINI_STATUS);
				playerStatusRect.setY(y);
				playerStatusRect.setVisible(true);
			}
			sortChildren();
		}

		/**
		 * 敵ステータスウィンドウ表示
		 */
		@Override
		public void showEnemyStatusWindow(int enemySeqNo) {
			// プレイヤーが表示されていたら下に表示
			float y = 0;
			PlayerStatusRectangle enemyStatusRect = getPlayerStatusRectangle(getActorSprite(enemySeqNo).getPlayerId());
			if (enemyStatusRect != null) {
				enemyStatusRect.show(PlayerStatusRectangleType.MINI_STATUS);
				enemyStatusRect.setY(y);
				enemyStatusRect.setVisible(true);
			}
			sortChildren();
		}

		/**
		 * プレイヤーステータス非表示
		 */
		@Override
		public void hidePlayerStatusWindow() {
			hideAllPlayerStatus();
		}

		/**
		 * 敵ステータス非表示
		 */
		@Override
		public void hideEnemyStatusWindow() {
			hideAllPlayerStatus();
		}

		/**
		 * 行動メニュー表示
		 */
		@Override
		public void showSelectMenu(boolean isAttackDone, boolean isMovedDone, MapPoint mapPoint) {
			// マップ座標を採用したのでキャラの座標はイマイチなので画面中央に表示
			mMapBattleSelectMenuLayer.showSelectMenu(MainScene.this, 
					getWindowWidth() / 2, getWindowHeight() / 2, isAttackDone, isMovedDone);
		}

		@Override
		public void hideSelectMenu() {
			mMapBattleSelectMenuLayer.hideSelectMenu();
		}
	};
	
	/** アクターロジック */
	private ActorPlayerLogic mActorPlayerLogic;
	
	private MapBattleSelectMenuLayer mMapBattleSelectMenuLayer;
	
	private MapBattleInfoDto mMapBattleInfoDto;
	/**
	 * SRPGマップバトルパートの初期化処理
	 */
	private void initMap(SaveDataDto saveDataDto) {
		// 初期化
		mActorPlayerLogic = new ActorPlayerLogic();
		touchStartPoint = new float[2];
		
		// 背景
		initBackground();
		
		// マップ情報を読み込む
		mMapBattleInfoDto = new MapBattleInfoDto();
		mMapBattleInfoDto.createMapJsonData(saveDataDto.getSceneId(), 
				JsonUtil.toJson(getBaseActivity(), "map/"+ saveDataDto.getSceneId()));
		
		// ベースマップ生成
		Rectangle mapBaseRect = new Rectangle(0, 0, 
				getWindowWidth(), getWindowHeight(), 
				getBaseActivity().getVertexBufferObjectManager());
		mapBaseRect.setTag(9999999); // TODO:どうにかして
		mapBaseRect.setColor(Color.TRANSPARENT);
		attachChild(mapBaseRect);
		
		// ダメージテキスト初期化
		initDamageText(mapBaseRect);
				
		// グリッド線表示
		showGrid(mapBaseRect);

		// 選択カーソルを用意
		Sprite cursorSprite = getResourceSprite("grid128.png");
		cursorSprite.setColor(Color.CYAN);
		cursorSprite.setVisible(false);
		cursorSprite.setZIndex(LayerZIndexType.SELECTCURSOR_LAYER.getValue());
		cursorSprite.setSize(MapGridUtil.GRID_X, MapGridUtil.GRID_Y);
		cursorSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new AlphaModifier(0.5f, 0.2f, 0.6f),
				new AlphaModifier(0.5f, 0.6f, 0.2f)
				)));
		mapBaseRect.attachChild(cursorSprite);
		
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
				touchSprite(mMapBattleSelectMenuLayer.getX() + pTouchAreaLocalX, 
						mMapBattleSelectMenuLayer.getY() + pTouchAreaLocalY);
				
				mGameManager.touchMenuBtnEvent(pButtonSprite.getTag());
			}
		});

		// ゲーム開始
		mGameManager = new GameManager(mSrpgGameManagerListener);
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
	private void initDamageText(IEntity entity) {
		Text damageText = new Text(0, 0, getFont(), "00000", getBaseActivity().getVertexBufferObjectManager());
		damageText.setColor(Color.TRANSPARENT);
		damageText.setZIndex(LayerZIndexType.TEXT_LAYER.getValue());
		damageText.setTag(DAMAGE_TEXT_TAG); //TODO: TAG管理
		entity.attachChild(damageText);
	}
	
//	/**
//	 * グリッド表示
//	 */
//	private void showGrid() {
//		int base = 40;
//		int baseGrid = 0;
//		
//		for (int x = -10 ; x < 20; x++) {
//			final Line line = new Line(base * x, 0, (x * base) + baseGrid, getWindowHeight(), 
//					getBaseActivity().getVertexBufferObjectManager());
//			line.setLineWidth(1);
//			line.setColor(Color.WHITE);
//			line.setAlpha(0.5f);
//			attachChild(line);
//		}
//		
//		for (int y = -10 ; y < 20; y++) {
//			final Line line = new Line(0, (base * y), getWindowWidth(), (y * base) - (baseGrid / 2), 
//					getBaseActivity().getVertexBufferObjectManager());
//			line.setLineWidth(1);
//			line.setColor(Color.WHITE);
//			line.setAlpha(0.5f);
//			attachChild(line);
//		}
//	}
	
	/**
	 * グリッド表示
	 */
	private void showGrid(IEntity entity) {
		
		for (int x = 0; x <= mMapBattleInfoDto.getMapSizeX(); x++) {
			PointF pointStart = MapGridUtil.indexToDisp(new Point(x, 0));
			PointF pointEnd = MapGridUtil.indexToDisp(new Point(x, mMapBattleInfoDto.getMapSizeY()));
			pointStart.y = pointStart.y + MapGridUtil.GRID_Y / 2;
			pointEnd.y = pointEnd.y + MapGridUtil.GRID_Y / 2;
			final Line line = new Line(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			line.setZIndex(1);
			entity.attachChild(line);
		}
		for (int y = 0; y <= mMapBattleInfoDto.getMapSizeY(); y++) {
			PointF pointStart = MapGridUtil.indexToDisp(new Point(0, y));
			PointF pointEnd = MapGridUtil.indexToDisp(new Point(mMapBattleInfoDto.getMapSizeX(), y));
			pointStart.y = pointStart.y + MapGridUtil.GRID_Y / 2;
			pointEnd.y = pointEnd.y + MapGridUtil.GRID_Y / 2;
			final Line line = new Line(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			line.setZIndex(1);
			entity.attachChild(line);
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
		player.setPlayerSize(size, size);
		player.setPlayerPosition(
				mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (player.getWidth() / 2) - (player.getWidth() / 8), 
				mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - player.getHeight() + (player.getHeight() / 8));
		player.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		player.setTag(playerSeqNo);
		getBaseMap().attachChild(player);
		
		PlayerStatusRectangle playerStatusRect = initStatusWindow(player, 0);
		playerStatusRect.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
		playerStatusRect.setColor(Color.BLUE);
		playerStatusRect.setAlpha(0.5f);
		playerStatusRect.setTag(80000 + playerSeqNo);// TODO: TAG
		attachChild(playerStatusRect);
		playerStatusRect.setVisible(false);
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
//		enemy.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		enemy.setPlayerSize(size, size);
		enemy.setPlayerPosition(
				mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (enemy.getWidth() / 2) - (enemy.getWidth() / 8), 
				mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - enemy.getHeight() + (enemy.getHeight() / 8));
		enemy.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		enemy.setTag(enemySeqNo);
		getBaseMap().attachChild(enemy);
		
		PlayerStatusRectangle enemyStatusRect = initStatusWindow(enemy, 0);
		enemyStatusRect.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
		enemyStatusRect.setColor(Color.RED);
		enemyStatusRect.setAlpha(0.5f);
		enemyStatusRect.setTag(80000 + enemySeqNo);// TODO: TAG
		attachChild(enemyStatusRect);
		enemyStatusRect.setVisible(false);
	}
	
	/**
	 * 障害物描画.
	 * @param mapPoint
	 */
	private void createObstacleSprite(int currentTileIndex, MapPoint mapPoint, float size) {
		Sprite obstacle = getResourceSprite("icon_ob.png");
//		obstacle.setPosition(mapPoint.getX(), mapPoint.getY());
		obstacle.setSize(size, size);
		obstacle.setPosition(
				mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (obstacle.getWidth() / 2) - (obstacle.getWidth() / 8), 
				mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - obstacle.getHeight() + (obstacle.getHeight() / 8));
		obstacle.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		obstacle.setTag(OBSTACLE_TAG_START + obstacleIndex); obstacleIndex++;
		getBaseMap().attachChild(obstacle);
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
		PlayerStatusRectangle playerStatusRectangle = getPlayerStatusRectangle(actorSprite.getPlayerId());
		if (playerStatusRectangle == null) {
			playerStatusRectangle = new PlayerStatusRectangle(this, 
					getFont(), actorSprite.getActorPlayer(), 
					ActorSprite.getFaceFileName(actorSprite.getActorPlayer().getImageResId()), 
					getWindowWidth() / 2, y, 
					getWindowWidth() / 2, getWindowHeight() / 2);
			playerStatusRectangle.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
			CommonWindowRectangle commonWindowRectangle = new CommonWindowRectangle(
					0, 0, 
					playerStatusRectangle.getWidth(), 
					playerStatusRectangle.getHeight() / 2,
					Color.TRANSPARENT, 0.0f, this);
			playerStatusRectangle.attachChild(commonWindowRectangle);
		}
		return playerStatusRectangle;
	}
	
	private PlayerStatusRectangle getPlayerStatusRectangle(int playerSeqNo) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if (getChildByIndex(i) instanceof PlayerStatusRectangle) {
				if ((playerSeqNo + 80000) == getChildByIndex(i).getTag()) {
					return (PlayerStatusRectangle) getChildByIndex(i);
				}
			}
		}
		return null;
	}
	private void hideAllPlayerStatus() {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if (getChildByIndex(i) instanceof PlayerStatusRectangle) {
				((PlayerStatusRectangle)getChildByIndex(i)).hide();
			}
		}
	}
	
	/**
	 * プレイヤーキャラ消去.
	 * @param playerSeqNo
	 */
	private void detachPlayer(final int playerSeqNo) {
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
	private void detachEnemy(final int enemyId) {
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
		Rectangle baseMap = getBaseMap();
		int count = baseMap.getChildCount();
		for (int i = 0; i < count; i++) {
			if (baseMap.getChildByIndex(i) instanceof ActorSprite) {
				if (playerSeqNo == baseMap.getChildByIndex(i).getTag()) {
					return (ActorSprite) baseMap.getChildByIndex(i);
				}
			}
		}
		return null;
	}
	
	private Rectangle getBaseMap() {
		Rectangle mapBaseRect = null;
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			IEntity entity = getChildByIndex(i);
			if (entity instanceof Rectangle && entity.getTag() == 9999999) {
				mapBaseRect = (Rectangle) entity;
			}
		}
		return mapBaseRect;
	}
	
	// ------------------------ カーソル --------------------------
	/**
	 * 移動カーソル描画.
	 * @param mapPoint
	 */
	private void createMoveCursorSprite(MapPoint mapPoint) {
		IAreaShape cursorRectangle = createCursorSprite(mapPoint, Color.GREEN);
		cursorRectangle.setZIndex(LayerZIndexType.MOVECURSOR_LAYER.getValue());
	}
	/**
	 * 攻撃カーソル描画.
	 * @param mapPoint
	 */
	private void createAttackCursorSprite(MapPoint mapPoint) {
		IAreaShape cursorRectangle = createCursorSprite(mapPoint, Color.YELLOW);
		cursorRectangle.setZIndex(LayerZIndexType.ATTACKCURSOR_LAYER.getValue());
	}
	/**
	 * カーソル選択.
	 */
	private void touchedCusorRectangle(final MapPoint mapPoint) {
		final PointF pointF = MapGridUtil.indexToDisp(new Point(mapPoint.getMapPointX(), mapPoint.getMapPointY()));
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				Rectangle baseMap = getBaseMap();
				int count = baseMap.getChildCount();
				for (int i = 0; i < count; i++) {
					if (baseMap.getChildByIndex(i) instanceof Sprite && 
							baseMap.getChildByIndex(i).getZIndex() == LayerZIndexType.SELECTCURSOR_LAYER.getValue().intValue()) {
						Sprite cursor = (Sprite) baseMap.getChildByIndex(i);
						cursor.setPosition(pointF.x, pointF.y);
						cursor.setVisible(true);
					}
				}
				baseMap.sortChildren();
			}
		});
	}
	/**
	 * カーソル描画.
	 * @param mapPoint
	 */
	private Sprite createCursorSprite(MapPoint mapPoint, Color color) {
		PointF pointF = MapGridUtil.indexToDisp(new Point(mapPoint.getMapPointX(), mapPoint.getMapPointY()));
		
		Sprite cursor = getResourceSprite("grid128.png");
		cursor.setColor(color);
		cursor.setSize(MapGridUtil.GRID_X, MapGridUtil.GRID_Y);
		cursor.setPosition(pointF.x, pointF.y);
		cursor.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new AlphaModifier(0.5f, 0.2f, 0.6f),
				new AlphaModifier(0.5f, 0.6f, 0.2f)
				)));
		getBaseMap().attachChild(cursor);
		
		return cursor;
	}
	
	/**
	 * カーソル消去.
	 */
	private void hideCursorSprite() {
		// 別スレッドで削除
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				Rectangle baseMap = getBaseMap();
				for (int i = 0; i < baseMap.getChildCount(); i++) {
					if (baseMap.getChildByIndex(i) instanceof Sprite) {
						Sprite sprite = (Sprite) baseMap.getChildByIndex(i);
						// 攻撃カーソルと移動カーソルはさようなら
						if (sprite.getZIndex() == LayerZIndexType.MOVECURSOR_LAYER.getValue().intValue() ||
								sprite.getZIndex() == LayerZIndexType.ATTACKCURSOR_LAYER.getValue().intValue()) {
							detachEntity(sprite);
							
						// 選択カーソルは使いまわす
						} else if (sprite.getZIndex() == LayerZIndexType.SELECTCURSOR_LAYER.getValue().intValue()) {
							sprite.setVisible(false);
						} 
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
	 * @param pMapBattleCutInLayerType
	 * @param pAnimationCallback
	 */
	private void showCutIn(MapBattleCutInLayerType pMapBattleCutInLayerType, final ICutInCallback pCutInCallback) {
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
	
	// ------ タッチイベント ------
	/** ドラッグ判定用 */
	private float[] touchStartPoint;
	// 補正幅(たぶん使わない)
	private static final int OVER_START_DISP_X = 0; // -220
	private static final int OVER_END_DISP_X   = 0;
	private static final int OVER_START_DISP_Y = 0;
	private static final int OVER_END_DISP_Y   = 0; // 100
	
	private float getStartDispX() {
		return 0 + OVER_START_DISP_X - (
				(
						(mMapBattleInfoDto.getMapSizeX() - (MapGridUtil.BASE_Y - 1))
				) * MapGridUtil.GRID_X) 
				+ (
				(
						(mMapBattleInfoDto.getMapSizeX()- mMapBattleInfoDto.getMapSizeY()) / 2
				) * MapGridUtil.GRID_X)
				;
	}
	private float getStartDispY() {
		return 0 + OVER_START_DISP_Y + ((MapGridUtil.BASE_Y - mMapBattleInfoDto.getMapSizeY()) * (MapGridUtil.GRID_Y / 2));
	}
	private float getEndDispX(IAreaShape entity) {
		return entity.getWidth() + OVER_END_DISP_X;
	}
	private float getEndDispY(IAreaShape entity) {
//		return entity.getHeight() + OVER_END_DISP_Y + (((mMapBattleInfoDto.getMapSizeY() - (MapGridUtil.BASE_Y - 1)) * (MapGridUtil.GRID_Y / 2)));
		return entity.getHeight() + OVER_END_DISP_Y + (
				(
						(mMapBattleInfoDto.getMapSizeY() - (MapGridUtil.BASE_Y - 1) + 
						(mMapBattleInfoDto.getMapSizeX() - mMapBattleInfoDto.getMapSizeY())
				) * (MapGridUtil.GRID_Y / 2)));
	}
	
	private void touchEventSRPGPart(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		Rectangle mapBaseRect = getBaseMap();
		if (mapBaseRect == null) {
			return;
		}
		float mapDispX = pSceneTouchEvent.getX() - mapBaseRect.getX();
		float mapDispY = pSceneTouchEvent.getY() - mapBaseRect.getY();
		// タッチ位置をスクロールを考慮したマップ座標に変換
		Point mapPoint = MapGridUtil.dispToIndex(
				mapDispX, 
				mapDispY);
		Log.d("", " x = " + mapPoint.x + " y = " + mapPoint.y);

		// スクロールチェック
		TouchEventFlick touchEventFlick = TouchEventFlick.UN_FLICK;
		float xDistance = 0;
		float yDistance = 0;
		if (pSceneTouchEvent.isActionDown()) {
			// 開始点を登録
			touchStartPoint[0] = pSceneTouchEvent.getX();
			touchStartPoint[1] = pSceneTouchEvent.getY();
		} else if (pSceneTouchEvent.isActionUp() || pSceneTouchEvent.isActionCancel()) {
			float[] touchEndPoint = new float[2];
			touchEndPoint[0] = pSceneTouchEvent.getX();
			touchEndPoint[1] = pSceneTouchEvent.getY();
			// フリックチェック
			touchEventFlick = CollidesUtil.checkToushFlick(touchStartPoint, touchEndPoint);
			if (touchEventFlick != TouchEventFlick.UN_FLICK) {
				xDistance = touchEndPoint[0] -touchStartPoint[0];
				yDistance = touchEndPoint[1] -touchStartPoint[1];
			}
		}
		// スクロール時
		if (touchEventFlick != TouchEventFlick.UN_FLICK) {
			// マップをスクロール
			float moveToX = mapBaseRect.getX() + xDistance;
			float moveToY = mapBaseRect.getY() + yDistance;
			// 表示可能領域で補正
			if (getStartDispX() > moveToX) {
				moveToX = getStartDispX();
			}
			if (getEndDispX(mapBaseRect) < (moveToX + mapBaseRect.getWidth())) {
				moveToX = getEndDispX(mapBaseRect) - mapBaseRect.getWidth();
			}
			if (getStartDispY() > moveToY) {
				moveToY = getStartDispY();
			}
			if (getEndDispY(mapBaseRect) < (moveToY + mapBaseRect.getHeight())) {
				moveToY = getEndDispY(mapBaseRect) - mapBaseRect.getHeight();
			}
			
			mapBaseRect.registerEntityModifier(new MoveModifier(0.2f, 
					mapBaseRect.getX(), moveToX,
					mapBaseRect.getY(), moveToY));
			
		// スクロール以外のとき
		} else {
			if (pSceneTouchEvent.isActionUp()) {
				// TODO: 一旦会話はGameManager外にする
				TalkLayer mTalkLayer = (TalkLayer) getChildByTag(TALK_LAYER_TAG);
				MapBattleClearConditionTouchLayer mMapBattleTouchLayer = 
						(MapBattleClearConditionTouchLayer) getChildByTag(MapBattleClearConditionTouchLayer.TAG);
				
				if (mTalkLayer != null && mTalkLayer.contains(x, y)) {
					
//					getMediaManager().play(SoundType.BTN_PRESSED_SE);
					
					if (mTalkLayer.isNextTalk()) {
						mTalkLayer.nextTalk();
						
					} else {
						mTalkLayer.hide();
						// 次の会話がなくなれば、会話レイヤーを開放
						detachEntity(mTalkLayer);
						mTalkLayer = null;
						
						// 勝利条件表示
//						getMediaManager().play(MusicType.BATTLE1_BGM);
						mMapBattleTouchLayer.showTouchLayer(this);
					}
					
				} else if (mMapBattleTouchLayer.isTouchLayer(x, y)) {
					
//					getMediaManager().play(SoundType.BTN_PRESSED_SE);
					
					// 勝利条件を非表示にする
					mMapBattleTouchLayer.hideTouchLayer(this);
					
					// ゲーム開始
					mGameManager.gameStart();
					
				} else {
					// タッチイベント振り分け処理を呼ぶ
					mGameManager.onTouchMapItemEvent(mapDispX, mapDispY);
				}				
			}
		}
	}
	
	private void clearMapBattle() {
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
	private void gameOverMapBattle() {
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
				ExpDistributionLayer expDistributionLayer = new ExpDistributionLayer(0, 0, 
						getWindowWidth(), getWindowHeight(), MainScene.this);
				attachChild(expDistributionLayer);
				// 次のシナリオへ
//				nextScenario();
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
	
	public void sortChildren() {
		super.sortChildren();
		getBaseMap().sortChildren();
	}
}
