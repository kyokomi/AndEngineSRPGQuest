package com.kyokomi.srpgquest.scene.part;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackInOut;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.logic.ActorPlayerLogic;
import com.kyokomi.core.logic.MapBattleRewardLogic;
import com.kyokomi.core.sprite.CommonWindowRectangle;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.core.utils.CollidesUtil;
import com.kyokomi.core.utils.JsonUtil;
import com.kyokomi.core.utils.CollidesUtil.TouchEventFlick;
import com.kyokomi.srpgquest.constant.CommonTag;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.constant.MapBattleCutInLayerType;
import com.kyokomi.srpgquest.constant.MoveDirectionType;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.layer.CutInLayer;
import com.kyokomi.srpgquest.layer.MapBattleClearConditionTouchLayer;
import com.kyokomi.srpgquest.layer.MapBattleSelectMenuLayer;
import com.kyokomi.srpgquest.layer.CutInLayer.ICutInCallback;
import com.kyokomi.srpgquest.logic.TalkLogic;
import com.kyokomi.srpgquest.manager.GameManager;
import com.kyokomi.srpgquest.manager.GameManager.SRPGGameManagerListener;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.scene.InitialScene;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.srpgquest.sprite.ActorSprite;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle.PlayerStatusRectangleType;
import com.kyokomi.srpgquest.utils.MapGridUtil;

public class SRPGPart extends AbstractGamePart {

	private static final int SPRITE_SIZE = 64;
	private static final int DAMAGE_TEXT_TAG = 1000;
	
	/** ゲーム管理クラス */
	private GameManager mGameManager;
	/** 敵のターンタイマー. */
	private TimerHandler mEnemyTurnUpdateHandler;
	
	/** アクターロジック */
	private ActorPlayerLogic mActorPlayerLogic;
	
	private MapBattleSelectMenuLayer mMapBattleSelectMenuLayer;
	
	private MapBattleInfoDto mMapBattleInfoDto;
	
	private SRPGGameManagerListener mSrpgGameManagerListener = new SRPGGameManagerListener() {
		
		@Override
		public ActorPlayerDto createPlayer(int seqNo, int playerId, MapPoint mapPoint) {
			ActorPlayerDto actorPlayerDto = mActorPlayerLogic.createActorPlayerDto(getBaseScene(), playerId);
			createPlayerSprite(seqNo, actorPlayerDto, mapPoint, SPRITE_SIZE);
			return actorPlayerDto;
		}
		
		@Override
		public ActorPlayerDto createEnemy(int seqNo, int enemyId, MapPoint mapPoint) {
			ActorPlayerDto actorPlayerDto = mActorPlayerLogic.createActorPlayerDto(getBaseScene(), enemyId);
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
		public void createMoveCursors(List<Point> cursorMapPointList) {
			List<Sprite> cursorSpriteList = getCursorSpriteList(LayerZIndexType.MOVECURSOR_LAYER);
			int size = cursorMapPointList.size();
			for (int i = 0; i < size; i++) {
				Point mapPoint = cursorMapPointList.get(i);
				IAreaShape cursor = null;
				if (cursorSpriteList.size() >= i) {
					cursor = cursorSpriteList.get(i);
					PointF pointF = MapGridUtil.indexToDisp(new Point(mapPoint.x, mapPoint.y));
					cursor.setPosition(pointF.x, pointF.y);
					cursor.setVisible(true);
				} else {
					cursor = createMoveCursorSprite(mapPoint);					
				}
			}
		}

		@Override
		public void createAttackCursors(List<Point> cursorMapPointList) {
			List<Sprite> cursorSpriteList = getCursorSpriteList(LayerZIndexType.ATTACKCURSOR_LAYER);
			int size = cursorMapPointList.size();
			for (int i = 0; i < size; i++) {
				Point mapPoint = cursorMapPointList.get(i);
				IAreaShape cursor = null;
				if (cursorSpriteList.size() >= i) {
					cursor = cursorSpriteList.get(i);
					PointF pointF = MapGridUtil.indexToDisp(new Point(mapPoint.x, mapPoint.y));
					cursor.setPosition(pointF.x, pointF.y);
					cursor.setVisible(true);
				} else {
					cursor = createAttackCursorSprite(mapPoint);					
				}
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
						actorSprite.getPlayer().setColor(Color.WHITE);
					}
					cutInCallback.doAction();					
				}
			});
			// カットイン中に1番目のプレイヤーまで画面を移動する
			int playerSeqNo = playerSeqNoList.iterator().next();
			mapMoveToPlayer(playerSeqNo);
		}
		@Override
		public void showEnemyTurnCutIn(final List<Integer> enemySeqNoList, final ICutInCallback cutInCallback) {
			showCutIn(MapBattleCutInLayerType.ENEMY_TURN_CUTIN, new ICutInCallback() {
				@Override
				public void doAction() {
					for (Integer seqNo : enemySeqNoList) {
						ActorSprite enemySprite = getActorSprite(seqNo);
						enemySprite.setPlayerToDefaultPosition();
						enemySprite.getPlayer().setColor(Color.WHITE);
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
			getBaseScene().unregisterUpdateHandler(mEnemyTurnUpdateHandler);
		}

		@Override
		public void startEnemyTurnTimer() {
			getBaseScene().registerUpdateHandler(mEnemyTurnUpdateHandler);
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
			ActorSprite actorSprite = getActorSprite(playerSeqNo);
			actorSprite.setPlayerToDefaultPositionStop();
			actorSprite.getPlayer().setColor(new Color(0.4f, 0.4f, 0.4f));
		}

		@Override
		public void stopWalkingEnemyAnimation(int enemySeqNo) {
			ActorSprite enemySprite = getActorSprite(enemySeqNo);
			enemySprite.setPlayerToDefaultPositionStop();
			enemySprite.getPlayer().setColor(new Color(0.4f, 0.4f, 0.4f));
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
			
			// ウィンドウを移動する
			mapMoveToPlayer(enemySeqNo);
			
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
		
		/**
		 * アクターの向きを設定.
		 */
		@Override
		public void acotorDirection(int acoterSeqNo, MoveDirectionType directionType) {
			getActorSprite(acoterSeqNo).setPlayerDirection(directionType);
		}
		
		/**
		 * アクターのダメージエフェクト.
		 */
		@Override
		public void acotorDamageEffect(int acoterSeqNo) {
//			getMediaManager().play(SoundType.ATTACK_SE);
			
			final ActorSprite actorSprite = getActorSprite(acoterSeqNo);
			final Color color = actorSprite.getPlayer().getColor();
			// 3回赤くなる
			actorSprite.getPlayer().registerEntityModifier(new LoopEntityModifier(
					new SequenceEntityModifier(
							new ColorModifier(0.1f, color, Color.RED),
							new ColorModifier(0.1f, Color.RED, color)
					), 3)
			);
		}

		/**
		 * ダメージテキスト表示.
		 */
		@Override
		public void showDamageText(int damage, final PointF dispPoint) {
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
			PlayerStatusRectangle enemyStatusRect = getPlayerStatusRectangle(enemySeqNo);
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
			mMapBattleSelectMenuLayer.showSelectMenu(getBaseScene(), 
					getBaseScene().getWindowWidth() / 2, getBaseScene().getWindowHeight() / 2, isAttackDone, isMovedDone);
		}

		@Override
		public void hideSelectMenu() {
			mMapBattleSelectMenuLayer.hideSelectMenu();
		}
	};
	
	public SRPGPart(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}

	@Override
	public void init(SaveDataDto saveDataDto) {
		// 初期化
		mActorPlayerLogic = new ActorPlayerLogic();
		touchStartPoint = new float[2];
		
		// 背景
		initBackground();
		
		// マップ情報を読み込む
		mMapBattleInfoDto = new MapBattleInfoDto();
		mMapBattleInfoDto.createMapJsonData(saveDataDto.getSceneId(), 
				JsonUtil.toJson(getBaseScene().getBaseActivity(), "map/"+ saveDataDto.getSceneId()));
		
		// ベースマップ生成
		Rectangle mapBaseRect = new Rectangle(0, 0, 
				getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight(), 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		mapBaseRect.setTag(9999999); // TODO:どうにかして
		mapBaseRect.setColor(Color.TRANSPARENT);
		getBaseScene().attachChild(mapBaseRect);
		
		// ダメージテキスト初期化
		initDamageText(mapBaseRect);
				
		// グリッド線表示
		showGrid(mapBaseRect);

		// 選択カーソルを用意		
		Sprite cursorSprite = createCursorSprite(new Point(0, 0), Color.CYAN);
		cursorSprite.setVisible(false);
		cursorSprite.setZIndex(LayerZIndexType.SELECTCURSOR_LAYER.getValue());
		
		// タッチレイヤー初期化
		MapBattleClearConditionTouchLayer mMapBattleTouchLayer = new MapBattleClearConditionTouchLayer(getBaseScene());
		mMapBattleTouchLayer.setTag(MapBattleClearConditionTouchLayer.TAG);
		getBaseScene().attachChild(mMapBattleTouchLayer);
		
		// カットイン初期化
		attachWithCreateCutInLayer(MapBattleCutInLayerType.PLAYER_TURN_CUTIN);
		attachWithCreateCutInLayer(MapBattleCutInLayerType.PLAYER_WIN_CUTIN);
		attachWithCreateCutInLayer(MapBattleCutInLayerType.ENEMY_TURN_CUTIN);
		attachWithCreateCutInLayer(MapBattleCutInLayerType.GAME_OVER_CUTIN);
		
		// メニュー初期化
		mMapBattleSelectMenuLayer = new MapBattleSelectMenuLayer(getBaseScene(), new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				getBaseScene().touchSprite(mMapBattleSelectMenuLayer.getX() + pTouchAreaLocalX, 
						mMapBattleSelectMenuLayer.getY() + pTouchAreaLocalY);
				
				mGameManager.touchMenuBtnEvent(pButtonSprite.getTag());
			}
		});

		// ゲーム開始
		mGameManager = new GameManager(mSrpgGameManagerListener);
		mGameManager.mapInit(mMapBattleInfoDto); // 10 x 10 スケール1倍のグリッドマップ
		
		startMap(saveDataDto);
	}

	@Override
	public void touchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
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
			mapMove(new PointF(moveToX, moveToY));
			
		// スクロール以外のとき
		} else {
			if (pSceneTouchEvent.isActionUp()) {
				// TODO: 一旦会話はGameManager外にする
				TalkLayer mTalkLayer = (TalkLayer) getBaseScene().getChildByTag(CommonTag.TALK_LAYER_TAG.getValue());
				MapBattleClearConditionTouchLayer mMapBattleTouchLayer = 
						(MapBattleClearConditionTouchLayer) getBaseScene().getChildByTag(MapBattleClearConditionTouchLayer.TAG);
				
				if (mTalkLayer != null && mTalkLayer.contains(x, y)) {
					
//					getMediaManager().play(SoundType.BTN_PRESSED_SE);
					
					if (mTalkLayer.isNextTalk()) {
						mTalkLayer.nextTalk();
						
					} else {
						mTalkLayer.hide();
						// 次の会話がなくなれば、会話レイヤーを開放
						getBaseScene().detachEntity(mTalkLayer);
						mTalkLayer = null;
						
						// 勝利条件表示
//						getMediaManager().play(MusicType.BATTLE1_BGM);
						mMapBattleTouchLayer.showTouchLayer(getBaseScene());
					}
					
				} else if (mMapBattleTouchLayer.isTouchLayer(x, y)) {
					
//					getMediaManager().play(SoundType.BTN_PRESSED_SE);
					
					// 勝利条件を非表示にする
					mMapBattleTouchLayer.hideTouchLayer(getBaseScene());
					
					// ゲーム開始
					mGameManager.gameStart();
					
				} else {
					// タッチイベント振り分け処理を呼ぶ
					mGameManager.onTouchMapItemEvent(mapDispX, mapDispY);
				}				
			}
		}
	}

	@Override
	public void end() {
		getBaseScene().nextScenario();
	}
	
	/**
	 * initMapの後に行う処理
	 */
	private void startMap(SaveDataDto saveDataDto) {
		// プレイヤー情報ができてから呼び出さないといけないので注意
		// 会話レイヤーを生成
		initTalk(saveDataDto.getScenarioNo(), saveDataDto.getSeqNo());

		// 移動カーソルを予め生成しておく
		int maxMoveCount = 0;
		int maxAttakRange = 0;
		List<ActorSprite> actorSpriteList = getActorSpriteList();
		for (ActorSprite actorSprite : actorSpriteList) {
			int attackRange = actorSprite.getActorPlayer().getAttackRange();
			int movePoint = actorSprite.getActorPlayer().getMovePoint();
			if (movePoint > maxMoveCount) {
				maxMoveCount = movePoint;
			}
			if (attackRange > maxAttakRange) {
				maxAttakRange = attackRange;
			}
		}
		// 自分のマス + 4方向の移動可能数累乗が移動可能カーソル描画最大数
		int cursorCount = 1 + (int) Math.pow(4, maxMoveCount);
		for (int i = 0; i < cursorCount; i++) {
			IAreaShape cursor = createMoveCursorSprite(new Point(0, 0));
			cursor.setVisible(false);
		}
		int attackCursorCount = 1 + (int) Math.pow(4, maxAttakRange);
		for (int i = 0; i < attackCursorCount; i++) {
			IAreaShape cursor = createAttackCursorSprite(new Point(0, 0));
			cursor.setVisible(false);
		}
	}
	
	/**
	 * 背景表示.
	 */
	private void initBackground() {
		Sprite backgroundSprite = getBaseScene().getResourceSprite("bk/main_bg.jpg");
		backgroundSprite.setSize(getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight());
		backgroundSprite.setZIndex(-1);
		getBaseScene().attachChild(backgroundSprite);
	}
	

	
	/**
	 * ダメージテキスト初期化
	 */
	private void initDamageText(IEntity entity) {
		Text damageText = new Text(0, 0, getBaseScene().getFont(), "00000", getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		damageText.setColor(Color.TRANSPARENT);
		damageText.setZIndex(LayerZIndexType.TEXT_LAYER.getValue());
		damageText.setTag(DAMAGE_TEXT_TAG); //TODO: TAG管理
		entity.attachChild(damageText);
	}

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
					getBaseScene().getBaseActivity().getVertexBufferObjectManager());
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
					getBaseScene().getBaseActivity().getVertexBufferObjectManager());
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
		ActorSprite player = new ActorSprite(playerActor, getBaseScene(), 0, 0, size, size, 1.0f);
		
		player.setPlayerToDefaultPosition();
		player.setPlayerSize(size, size);
		player.setPlayerPosition(
				mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (player.getWidth() / 2) - (player.getWidth() / 8), 
				mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - player.getHeight() + (player.getHeight() / 8));
		player.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		player.setTag(playerSeqNo);
		getBaseMap().attachChild(player);
		
		PlayerStatusRectangle playerStatusRect = getPlayerStatusRectangle(playerSeqNo);
		if (playerStatusRect == null) { 
			playerStatusRect = initStatusWindow(player, 0);
			playerStatusRect.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
			playerStatusRect.setColor(Color.BLUE);
			playerStatusRect.setAlpha(0.5f);
			playerStatusRect.setTag(80000 + playerSeqNo);// TODO: TAG
			getBaseScene().attachChild(playerStatusRect);
		}
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
		ActorSprite enemy = new ActorSprite(enemyActor, getBaseScene(), 0, 0, size, size, 1.0f);
		
		enemy.setPlayerToDefaultPosition();
		enemy.setPlayerSize(size, size);
		enemy.setPlayerPosition(
				mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (enemy.getWidth() / 2) - (enemy.getWidth() / 8), 
				mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - enemy.getHeight() + (enemy.getHeight() / 8));
		enemy.setZIndex(LayerZIndexType.ACTOR_LAYER.getValue());
		enemy.setTag(enemySeqNo);
		getBaseMap().attachChild(enemy);
		
		PlayerStatusRectangle enemyStatusRect = getPlayerStatusRectangle(enemySeqNo);
		if (enemyStatusRect == null) {
			enemyStatusRect = initStatusWindow(enemy, 0);
			enemyStatusRect.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
			enemyStatusRect.setColor(Color.RED);
			enemyStatusRect.setAlpha(0.5f);
			enemyStatusRect.setTag(80000 + enemySeqNo);// TODO: TAG
			getBaseScene().attachChild(enemyStatusRect);			
		}
		enemyStatusRect.setVisible(false);
	}
	
	/**
	 * 障害物描画.
	 * @param mapPoint
	 */
	private void createObstacleSprite(int currentTileIndex, MapPoint mapPoint, float size) {
		Sprite obstacle = getBaseScene().getResourceSprite("ob.png");
		obstacle.setSize(size, size);
		obstacle.setPosition(
				mapPoint.getX() + (MapGridUtil.GRID_X / 2) - (obstacle.getWidth() / 2) - (obstacle.getWidth() / 8), 
				mapPoint.getY() + (MapGridUtil.GRID_Y / 2) - obstacle.getHeight() + (obstacle.getHeight() / 2));
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
		PlayerStatusRectangle playerStatusRectangle = new PlayerStatusRectangle(getBaseScene(), 
				getBaseScene().getFont(), actorSprite.getActorPlayer(), 
				ActorSprite.getFaceFileName(actorSprite.getActorPlayer().getImageResId()), 
				getBaseScene().getWindowWidth() / 2, y, 
				getBaseScene().getWindowWidth() / 2, getBaseScene().getWindowHeight() / 2);
		playerStatusRectangle.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
		CommonWindowRectangle commonWindowRectangle = new CommonWindowRectangle(
				0, 0, 
				playerStatusRectangle.getWidth(), 
				playerStatusRectangle.getHeight() / 2,
				Color.TRANSPARENT, 0.0f, getBaseScene());
		playerStatusRectangle.attachChild(commonWindowRectangle);
		return playerStatusRectangle;
	}
	
	private PlayerStatusRectangle getPlayerStatusRectangle(int playerSeqNo) {
		int count = getBaseScene().getChildCount();
		for (int i = 0; i < count; i++) {
			if (getBaseScene().getChildByIndex(i) instanceof PlayerStatusRectangle) {
				if ((playerSeqNo + 80000) == getBaseScene().getChildByIndex(i).getTag()) {
					return (PlayerStatusRectangle) getBaseScene().getChildByIndex(i);
				}
			}
		}
		return null;
	}
	private void hideAllPlayerStatus() {
		int count = getBaseScene().getChildCount();
		for (int i = 0; i < count; i++) {
			if (getBaseScene().getChildByIndex(i) instanceof PlayerStatusRectangle) {
				((PlayerStatusRectangle) getBaseScene().getChildByIndex(i)).hide();
			}
		}
	}
	
	/**
	 * プレイヤーキャラ消去.
	 * @param playerSeqNo
	 */
	private void detachPlayer(final int playerSeqNo) {
		// 別スレッドで削除
		getBaseScene().getBaseActivity().runOnUpdateThread(new Runnable() {
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
		getBaseScene().getBaseActivity().runOnUpdateThread(new Runnable() {
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
	
	/**
	 * パフォーマンス悪そうなので多用しないように
	 * @return
	 */
	List<ActorSprite> getActorSpriteList() {
		List<ActorSprite> actorSpriteList = new ArrayList<ActorSprite>();
		Rectangle baseMap = getBaseMap();
		int count = baseMap.getChildCount();
		for (int i = 0; i < count; i++) {
			if (baseMap.getChildByIndex(i) instanceof ActorSprite) {
				actorSpriteList.add((ActorSprite) baseMap.getChildByIndex(i));
			}
		}
		return actorSpriteList;
	}
	
	public Rectangle getBaseMap() {
		Rectangle mapBaseRect = null;
		int count = getBaseScene().getChildCount();
		for (int i = 0; i < count; i++) {
			IEntity entity = getBaseScene().getChildByIndex(i);
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
	private IAreaShape createMoveCursorSprite(Point mapIndexPoint) {
		IAreaShape cursorRectangle = createCursorSprite(mapIndexPoint, Color.GREEN);
		cursorRectangle.setZIndex(LayerZIndexType.MOVECURSOR_LAYER.getValue());
		return cursorRectangle;
	}
	/**
	 * 攻撃カーソル描画.
	 * @param mapPoint
	 */
	private IAreaShape createAttackCursorSprite(Point mapIndexPoint) {
		IAreaShape cursorRectangle = createCursorSprite(mapIndexPoint, Color.YELLOW);
		cursorRectangle.setZIndex(LayerZIndexType.ATTACKCURSOR_LAYER.getValue());
		return cursorRectangle;
	}
	/**
	 * カーソル選択.
	 */
	private void touchedCusorRectangle(final MapPoint mapPoint) {
		final PointF pointF = MapGridUtil.indexToDisp(new Point(mapPoint.getMapPointX(), mapPoint.getMapPointY()));
		getBaseScene().getBaseActivity().runOnUpdateThread(new Runnable() {
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
	private Sprite createCursorSprite(Point mapIndexPoint, Color color) {
		PointF pointF = MapGridUtil.indexToDisp(new Point(mapIndexPoint.x, mapIndexPoint.y));
		
		Sprite cursor = getBaseScene().getResourceSprite("grid128.png");
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
		getBaseScene().getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				Rectangle baseMap = getBaseMap();
				for (int i = 0; i < baseMap.getChildCount(); i++) {
					if (baseMap.getChildByIndex(i) instanceof Sprite) {
						Sprite sprite = (Sprite) baseMap.getChildByIndex(i);
						// 移動カーソル使いまわす
						if (sprite.getZIndex() == LayerZIndexType.MOVECURSOR_LAYER.getValue().intValue()) {
							sprite.setVisible(false);
							
						// 攻撃カーソルは使いまわす
						} else if (sprite.getZIndex() == LayerZIndexType.ATTACKCURSOR_LAYER.getValue().intValue()) {
							sprite.setVisible(false);
							
						// 選択カーソルは使いまわす
						} else if (sprite.getZIndex() == LayerZIndexType.SELECTCURSOR_LAYER.getValue().intValue()) {
							sprite.setVisible(false);
						} 
					}
				}
			}
		});
	}
	
	/**
	 * 指定レイヤーのカーソルをリストで取得
	 * 重そうなので多用しない。あと描画中に使わない。（countが増えたりすると落ちるので
	 * @param layerZIndexType
	 * @return
	 */
	private List<Sprite> getCursorSpriteList(LayerZIndexType layerZIndexType) {
		List<Sprite> cursorList = new ArrayList<Sprite>();
		Rectangle baseMap = getBaseMap();
		for (int i = 0; i < baseMap.getChildCount(); i++) {
			if (baseMap.getChildByIndex(i) instanceof Sprite) {
				Sprite sprite = (Sprite) baseMap.getChildByIndex(i);
				if (sprite.getZIndex() == layerZIndexType.getValue().intValue()) {
					cursorList.add(sprite);
				} 
			}
		}
		return cursorList;
	}

	// ---------------- カットイン関連 ----------------------
	/**
	 * カットイン取得.
	 * @param pMapBattleCutInLayerType
	 * @return カットイン
	 */
	private CutInLayer getCutInLayer(MapBattleCutInLayerType pMapBattleCutInLayerType) {
		return (CutInLayer) getBaseScene().getChildByTag(pMapBattleCutInLayerType.getValue());
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
		cutInLayer = new CutInLayer(getBaseScene().getDispStartX(), getBaseScene().getDispStartY(), 
				getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight(), getBaseScene(), pMapBattleCutInLayerType);
		getBaseScene().attachChild(cutInLayer);
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
	
	private void initTalk(int scenarioNo, int seqNo) {
		TalkLogic talkLogic = new TalkLogic();
		// 会話内容取得
		List<PlayerTalkDto> talks = talkLogic.getTalkDtoList(getBaseScene(), scenarioNo, seqNo);
		// 顔リスト作成
		SparseArray<TiledSprite> actorFaces = talkLogic.getTalkFaceSparse(getBaseScene(), talks);
		// 会話レイヤー作成
		TalkLayer talkLayer = new TalkLayer(getBaseScene());
		talkLayer.initTalk(actorFaces, talks);
		talkLayer.hide();
		talkLayer.setZIndex(LayerZIndexType.TALK_LAYER.getValue());
		talkLayer.setTag(CommonTag.TALK_LAYER_TAG.getValue());
		getBaseScene().attachChild(talkLayer);
		// 会話表示
		talkLayer.nextTalk();
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
	
	private void mapMoveToPlayer(int playerSeqNo) {
		ActorSprite actorSprite = getActorSprite(playerSeqNo);
		float moveX = (actorSprite.getPlayer().getX() - (getBaseScene().getWindowWidth() / 2)) * -1;
		float moveY = (actorSprite.getPlayer().getY() - (getBaseScene().getWindowHeight() / 2)) * -1;
		mapMove(new PointF(moveX, moveY));
	}
 	private void mapMove(PointF movePointF) {
		Rectangle mapBaseRect = getBaseMap();
		// 表示可能領域で補正
		if (getStartDispX() > movePointF.x) {
			movePointF.x = getStartDispX();
		}
		if (getEndDispX(mapBaseRect) < (movePointF.x + mapBaseRect.getWidth())) {
			movePointF.x = getEndDispX(mapBaseRect) - mapBaseRect.getWidth();
		}
		if (getStartDispY() > movePointF.y) {
			movePointF.y = getStartDispY();
		}
		if (getEndDispY(mapBaseRect) < (movePointF.y + mapBaseRect.getHeight())) {
			movePointF.y = getEndDispY(mapBaseRect) - mapBaseRect.getHeight();
		}
		
		mapBaseRect.registerEntityModifier(new MoveModifier(0.2f, 
				mapBaseRect.getX(), movePointF.x,
				mapBaseRect.getY(), movePointF.y));
	}
	private void clearMapBattle() {
		int mapBattleId = getBaseScene().getBaseActivity().getGameController().createSaveDataDto(getBaseScene()).getSceneId();
		
		// 報酬振込み(アイテムだけ)
		MapBattleRewardLogic mapBattleRewardLogic = new MapBattleRewardLogic();
		MapBattleRewardDto mapBattleRewardDto = mapBattleRewardLogic.addMapBattleReward(getBaseScene(), 
				getBaseScene().getBaseActivity().getGameController().getSaveId(), mapBattleId);
		// セーブ更新(DB更新はしません。シナリオ進行時にまとめて更新してもらいます）
		getBaseScene().getBaseActivity().getGameController().addExp(mapBattleRewardDto.getTotalExp());
		getBaseScene().getBaseActivity().getGameController().addGold(mapBattleRewardDto.getTotalGold());
				
		// 次のシナリオへ
		end();
	}
	private void gameOverMapBattle() {
//		getMediaManager().stopPlayingMusic();
		// TODO: タイトルへ？
		getBaseScene().showScene(new InitialScene(getBaseScene().getBaseActivity()));
	}
	public void sortChildren() {
		getBaseScene().sortChildren();
		getBaseMap().sortChildren();
	}
}
