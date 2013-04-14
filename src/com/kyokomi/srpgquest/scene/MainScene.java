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
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackInOut;

import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.logic.MapBattleRewardLogic;
import com.kyokomi.core.logic.TalkLogic;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.ActorSprite;
import com.kyokomi.core.sprite.CommonWindowRectangle;
import com.kyokomi.core.sprite.PlayerStatusRectangle;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.core.sprite.PlayerStatusRectangle.PlayerStatusRectangleType;
import com.kyokomi.core.utils.JsonUtil;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.layer.MapBattleClearConditionTouchLayer;
import com.kyokomi.srpgquest.layer.MapBattleCutInLayer;
import com.kyokomi.srpgquest.layer.MapBattleSelectMenuLayer;
import com.kyokomi.srpgquest.layer.ScenarioStartCutInTouchLayer;
import com.kyokomi.srpgquest.layer.MapBattleCutInLayer.MapBattleCutInLayerType;
import com.kyokomi.srpgquest.manager.GameManager;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.sprite.CursorRectangle;

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
		}
		// タッチイベント登録
		setOnSceneTouchListener(this);
	}
	
	/**
	 * 次シナリオへ
	 */
	@Override
	public void nextScenario() {
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
		// TODO: 共通タッチイベント
//		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		switch (mGamePartType) {
		case NOVEL_PART:
			touchEventNovelPart(pScene, pSceneTouchEvent);
			break;
		case SRPG_PART:
			touchEventSRPGPart(pScene, pSceneTouchEvent);
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
		if (saveDataDto.getSeqNo() == 1) {
			ScenarioStartCutInTouchLayer scenarioStartCutInTouchLayer = 
					new ScenarioStartCutInTouchLayer(this);
			scenarioStartCutInTouchLayer.initLayer(this, saveDataDto);
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
	/** 敵のターンタイマー. */
	private TimerHandler mEnemyTurnUpdateHandler;
	
	public TimerHandler getEnemyTurnUpdateHandler() {
		return mEnemyTurnUpdateHandler;
	}
	
	MapBattleCutInLayer mMapBattleCutInLayer;
	MapBattleSelectMenuLayer mMapBattleSelectMenuLayer;
	/**
	 * SRPGマップバトルパートの初期化処理
	 */
	private void initMap(SaveDataDto saveDataDto) {
		
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
		mMapBattleCutInLayer = new MapBattleCutInLayer(this);
		// メニュー初期化
		mMapBattleSelectMenuLayer = new MapBattleSelectMenuLayer(this, new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
//				touchSprite(mMapBattleSelectMenuLayer.getMenuRectangle().getX() + pTouchAreaLocalX, 
//						mMapBattleSelectMenuLayer.getMenuRectangle().getY() + pTouchAreaLocalY);
				
				mGameManager.touchMenuBtnEvent(pButtonSprite.getTag());
			}
		});

		// マップ情報を読み込む
		MapBattleInfoDto mMapBattleInfoDto = new MapBattleInfoDto();
		mMapBattleInfoDto.createMapJsonData(saveDataDto.getSceneId(), 
				JsonUtil.toJson(getBaseActivity(), "map/"+ saveDataDto.getSceneId()));
		// ゲーム開始
		mGameManager = new GameManager(this);
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
	public void createPlayerSprite(int playerSeqNo, ActorPlayerDto playerActor, MapPoint mapPoint, float size) {
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
	public void createEnemySprite(int enemySeqNo, ActorPlayerDto enemyActor, MapPoint mapPoint, float size) {
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
	public void createObstacleSprite(MapPoint mapPoint, int currentTileIndex) {
		Sprite obstacle = getResourceSprite("icon_ob.png");
		obstacle.setPosition(mapPoint.getX(), mapPoint.getY());
		obstacle.setSize(mapPoint.getGridSize(), mapPoint.getGridSize());
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
	public void showCutIn(MapBattleCutInLayerType pMapBattleCutInLayerType, 
			final IAnimationCallback pAnimationCallback) {
		// カットイン表示
		mMapBattleCutInLayer.showCutIn(pMapBattleCutInLayerType, 
				new MapBattleCutInLayer.ICutInCallback() {
			@Override public void doAction() { 
				if (pAnimationCallback != null) { 
					pAnimationCallback.doAction(); 
				}
			}
		});
	}
	// --------------- 会話パート用 --------------------
	private static final int TALK_LAYER_TAG = 999;
	private void initTalk(int scenarioNo, int seqNo) {
		// 会話内容取得
		List<PlayerTalkDto> talks = getTalkDtoList(scenarioNo, seqNo);
		// 顔リスト作成
		SparseArray<TiledSprite> actorFaces = getTalkFaceSparse(talks);
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
					(MapBattleClearConditionTouchLayer) getChildByTag(
							MapBattleClearConditionTouchLayer.TAG);
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
		for (int i = 0; i < getChildCount(); i++) {
			detachEntity(getChildByIndex(i));
		}
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
		// 次のシナリオへ
		nextScenario();
	}

	/**
	 * @deprecated
	 */
	@Override
	public MScenarioEntity getScenarioEntity() {
		return null;
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

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		
	}

}