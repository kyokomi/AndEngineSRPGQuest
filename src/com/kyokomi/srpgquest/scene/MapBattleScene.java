package com.kyokomi.srpgquest.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackInOut;

import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.sprite.ActorSprite;
import com.kyokomi.core.sprite.CommonWindowRectangle;
import com.kyokomi.core.sprite.PlayerStatusRectangle;
import com.kyokomi.core.sprite.PlayerStatusRectangle.PlayerStatusRectangleType;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.core.utils.JsonUtil;
import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.constant.MapDataType;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.layer.MapBattleCutInLayer;
import com.kyokomi.srpgquest.layer.MapBattleCutInLayer.MapBattleCutInLayerType;
import com.kyokomi.srpgquest.layer.MapBattleSelectMenuLayer;
import com.kyokomi.srpgquest.layer.MapBattleClearConditionTouchLayer;
import com.kyokomi.core.logic.MapBattleRewardLogic;
import com.kyokomi.core.manager.MediaManager.MusicType;
import com.kyokomi.core.manager.MediaManager.SoundType;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.sprite.CursorRectangle;

public class MapBattleScene extends SrpgBaseScene 
	implements IOnSceneTouchListener{
	private static final String TAG = "MapBattleScene";
	
	/** SRPGゲームマネージャー. */
	private GameManager gameManager;
	
	/** プレイヤーと敵情報. */
//	private SparseArray<ActorSprite> players;
//	private SparseArray<ActorSprite> enemys;
//	private List<Sprite> obstacleList;
//	/** カーソル表示リスト. */
//	private List<CursorRectangle> cursorList;

	/** ダメージ表示用テキスト. */
	private Text mDamageText;
	/** 背景. */
	private Sprite mBackgroundSprite;
	
	/** メニュー・ステータス周り. */
	private MapBattleSelectMenuLayer mMapBattleSelectMenuLayer;
	
	private PlayerStatusRectangle mPlayerStatusRect;
	private PlayerStatusRectangle mEnemyStatusRect;
	
	/** 会話レイヤー. */
	private TalkLayer mTalkLayer;
	
	/** カットインレイヤー. */
	private MapBattleCutInLayer mMapBattleCutInLayer;
	private MapBattleClearConditionTouchLayer mMapBattleTouchLayer;
	
	/** このマップのシナリオ情報. */
	private MScenarioEntity mScenarioEntity;
	@Override
	public MScenarioEntity getScenarioEntity() {
		return mScenarioEntity;
	}
	/** マップ情報. */
	private MapBattleInfoDto mMapBattleInfoDto;
	
	public MapBattleScene(MultiSceneActivity pBaseActivity, MScenarioEntity pScenarioEntity) {
		super(pBaseActivity);
		this.mScenarioEntity = pScenarioEntity;
		init();
	}
	
	/**
	 * 再開時
	 */
	@Override
	public void onResume() {
//		getMediaManager().playPauseingMusic();
	}
	/**
	 * バックグラウンド時
	 */
	@Override
	public void onPause() {
//		getMediaManager().pausePlayingMusic();
	}
	@Override
	public void initSoundAndMusic() {
		// 効果音をロード
//		try {
//			getMediaManager().resetAllMedia();
//			getMediaManager().createMedia(SoundType.BTN_PRESSED_SE);
//			getMediaManager().createMedia(SoundType.ATTACK_SE);
//			getMediaManager().createMedia(MusicType.TUTORIAL_BGM);
//			getMediaManager().createMedia(MusicType.BATTLE1_BGM);
//			getMediaManager().createMedia(MusicType.CLEAR_BGM);
//			getMediaManager().createMedia(MusicType.GAME_OVER_BGM);
//		} catch (IOException e) {
//			Log.e(TAG, "sound file io error");
//			e.printStackTrace();
//		}	
	}

	/**
	 * キーイベント制御.
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
			return true;
		}
		return false;
	}

	@Override
	public void init() {
		// 初期化
//		players = new SparseArray<ActorSprite>();
//		enemys = new SparseArray<ActorSprite>();
//		cursorList = new ArrayList<CursorRectangle>();
//		obstacleList = new ArrayList<Sprite>();
		// デフォルトフォント初期化
		initFont(16);
		// 背景
		initBackground();
		// ダメージテキスト初期化
		initDamageText();
		// グリッド線表示
		testShowGrid();
		
		// カットイン初期化
		mMapBattleCutInLayer = new MapBattleCutInLayer(this);
		// タッチレイヤー初期化
		mMapBattleTouchLayer = new MapBattleClearConditionTouchLayer(this);
		// メニュー初期化
		mMapBattleSelectMenuLayer = new MapBattleSelectMenuLayer(this, new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				touchSprite(mMapBattleSelectMenuLayer.getMenuRectangle().getX() + pTouchAreaLocalX, 
						mMapBattleSelectMenuLayer.getMenuRectangle().getY() + pTouchAreaLocalY);
				gameManager.touchMenuBtnEvent(pButtonSprite.getTag());
			}
		});

		// マップ情報を読み込む
		mMapBattleInfoDto = new MapBattleInfoDto();
		mMapBattleInfoDto.createMapJsonData(getScenarioEntity().getSceneId(), 
				JsonUtil.toJson(getBaseActivity(), "map/"+ getScenarioEntity().getSceneId()));
		// ゲーム開始
		gameManager = new GameManager(this);
		gameManager.mapInit(mMapBattleInfoDto); // 10 x 10 スケール1倍のグリッドマップ
			
		// プレイヤー情報ができてから呼び出さないといけないので注意
		// 会話レイヤーを生成
		initTalk();
		
		// BGM再生開始
//		getMediaManager().playStart(MusicType.TUTORIAL_BGM);
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}
	
	/**
	 * 背景表示.
	 */
	private void initBackground() {
		mBackgroundSprite = getResourceSprite("bk/main_bg.jpg");
		mBackgroundSprite.setSize(getWindowWidth(), getWindowHeight());
		mBackgroundSprite.setZIndex(-1);
		attachChild(mBackgroundSprite);
	}
	
	// --------------------- ダメージテキスト ------------------------
	
	private void initDamageText() {
		mDamageText = new Text(0, 0, getFont(), "00000", getBaseActivity().getVertexBufferObjectManager());
		mDamageText.setColor(Color.TRANSPARENT);
		mDamageText.setZIndex(LayerZIndexType.TEXT_LAYER.getValue());
		attachChild(mDamageText);
	}
	
	// ----------------------- actor layer ---------------------------
	
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
//		players.put(playerSeqNo, player);
		
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
//		enemys.put(enemySeqNo, enemy);
		
		PlayerStatusRectangle enemyStatusRect = initStatusWindow(enemy, 0);
		enemyStatusRect.setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
		enemyStatusRect.setColor(Color.RED);
		enemyStatusRect.setAlpha(0.5f);
	}
	
	private static final int OBSTACLE_TAG_START = 10000;
	private int obstacleIndex = 0;
	
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
	
	/**
	 * アニメーション系の共通コールバック.
	 * @author kyokomi
	 *
	 */
	public interface IAnimationCallback {
		public void doAction();
	}
	
	// ----------------- アニメーション　演出 -------------------
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
		
		mDamageText.setScale(0.5f);
		mDamageText.setX(mapPoint.getX());
		mDamageText.setY(mapPoint.getY());
		mDamageText.setText(String.valueOf(damage));
		mDamageText.setColor(Color.WHITE);
		
		mDamageText.registerEntityModifier(new SequenceEntityModifier(
				new ParallelEntityModifier(
					new ScaleModifier(0.5f, 0.5f, 2.0f, EaseBackInOut.getInstance()),
					new SequenceEntityModifier(
							new MoveModifier(0.25f, mDamageText.getX(), mDamageText.getX(), 
									mDamageText.getY(), mDamageText.getY() - 15, 
									EaseBackInOut.getInstance()),
							new MoveModifier(0.25f, mDamageText.getX(), mDamageText.getX(), 
									mDamageText.getY() - 15, mDamageText.getY(), 
									EaseBackInOut.getInstance()))
					),
				new DelayModifier(0.2f, new IEntityModifier.IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						mDamageText.setColor(Color.TRANSPARENT);
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
//		switch (pMapBattleCutInLayerType) {
//		case PLAYER_TURN_CUTIN:
//			getMediaManager().play(MusicType.BATTLE1_BGM);
//			break;
//		case ENEMY_TURN_CUTIN:
//			getMediaManager().play(MusicType.BATTLE1_BGM);
//			break;
//		case PLAYER_WIN_CUTIN:
//			getMediaManager().play(MusicType.CLEAR_BGM);
//			break;
//		case GAME_OVER_CUTIN:
//			getMediaManager().play(MusicType.GAME_OVER_BGM);
//			break;
//		default:
//			return;
//		}
		// カットイン表示
		mMapBattleCutInLayer.showCutIn(pMapBattleCutInLayerType, new MapBattleCutInLayer.ICutInCallback() {
			@Override public void doAction() { if (pAnimationCallback != null) pAnimationCallback.doAction(); }
		});
	}
	// --------------- 会話パート用 --------------------
	private void initTalk() {
		// 会話内容取得
		List<PlayerTalkDto> talks = getTalkDtoList(
				getScenarioEntity().getScenarioNo(), 
				getScenarioEntity().getSeqNo());
		// 顔リスト作成
		SparseArray<TiledSprite> actorFaces = getTalkFaceSparse(talks);
		// 会話レイヤー作成
		mTalkLayer = new TalkLayer(this);
		mTalkLayer.initTalk(actorFaces, talks);
		mTalkLayer.hide();
		mTalkLayer.setZIndex(LayerZIndexType.TALK_LAYER.getValue());
		attachChild(mTalkLayer);
		// 会話表示
		mTalkLayer.nextTalk();
	}
	
	// --------------- ステータスウィンドウ --------------
	public void showPlayerStatusWindow(int playerSeqNo, float x) {
//		// 共通ウィンドウを作成
//		if (commonWindowRectangle == null) {
//			commonWindowRectangle = new CommonWindowRectangle(
//					0, 0,
//					getWindowWidth() / 2, getWindowHeight() / 2,
//					this);
//			attachChild(commonWindowRectangle);
//		}
		
		if (mPlayerStatusRect != null) {
			detachChild(mPlayerStatusRect);
		}
		// エネミーが表示されていたら下に表示
		float y = 0;
		if (mEnemyStatusRect != null && mEnemyStatusRect.isVisible()) {
			y = mEnemyStatusRect.getY() + mEnemyStatusRect.getHeight();
		}
		mPlayerStatusRect = getActorSprite(playerSeqNo).getPlayerStatusRectangle();
		if (mPlayerStatusRect != null) {
			mPlayerStatusRect.show(PlayerStatusRectangleType.MINI_STATUS);
			mPlayerStatusRect.setX(x);
			mPlayerStatusRect.setY(y);
			attachChild(mPlayerStatusRect);	
		}
		sortChildren();
	}
	public void showEnemyStatusWindow(int enemySeqNo) {
		if (mEnemyStatusRect != null) {
			detachChild(mEnemyStatusRect);
		}
		// プレイヤーが表示されていたら下に表示
		float y = 0;
		if (mPlayerStatusRect != null && mPlayerStatusRect.isVisible()) {
			y = mPlayerStatusRect.getY() + mPlayerStatusRect.getHeight();
		}
		mEnemyStatusRect = getActorSprite(enemySeqNo).getPlayerStatusRectangle();
		if (mEnemyStatusRect != null) {
			mEnemyStatusRect.show(PlayerStatusRectangleType.MINI_STATUS);
			mEnemyStatusRect.setY(y);
			attachChild(mEnemyStatusRect);
		}
		sortChildren();
	}
	public void hidePlayerStatusWindow() {
		if (mPlayerStatusRect != null) {
			mPlayerStatusRect.hide();
		}
	}
	public void hideEnemyStatusWindow() {
		if (mEnemyStatusRect != null) {
			mEnemyStatusRect.hide();
		}
	}
	// --------------- イベント系 -------------------

	/**
	 * 画面タッチイベント.
	 * TODO: 画面暗いときに押せちゃうのを制御しないといけない
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			
			// TODO: 一旦会話はGameManager外にする
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
				gameManager.gameStart();
				
			} else {
				// タッチイベント振り分け処理を呼ぶ
				gameManager.onTouchMapItemEvent(x, y);
			}
		}
		return false;
	}
	
	// ---- クリア時の処理 ----
	public void clearMapBattle() {
		int mapBattleId = getScenarioEntity().getSceneId();
		
		// 報酬振込み(アイテムだけ)
		MapBattleRewardLogic mapBattleRewardLogic = new MapBattleRewardLogic();
		MapBattleRewardDto mapBattleRewardDto = mapBattleRewardLogic.addMapBattleReward(this, 
				getBaseActivity().getGameController().getSaveId(), mapBattleId);
		// セーブ更新(DB更新はしません。シナリオ進行時にまとめて更新してもらいます）
		getBaseActivity().getGameController().addExp(mapBattleRewardDto.getTotalExp());
		getBaseActivity().getGameController().addGold(mapBattleRewardDto.getTotalGold());
				
		// 次のシナリオへ
		nextScenario(getScenarioEntity());
	}
	public void gameOverMapBattle() {
//		getMediaManager().stopPlayingMusic();
		// TODO: タイトルへ？
		showScene(new InitialScene(getBaseActivity()));
	}
	// ---- グリッド表示 ----
	
	private void testShowGrid() {
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
	
	@Override
	public void destory() {
//		if (players != null) {
//			for (int i = 0; i < players.size(); i++) {
//				detachEntity(players.valueAt(i));
//			}
//			players = null;
//		}
//		if (enemys != null) {
//			for (int i = 0; i < enemys.size(); i++) {
//				detachEntity(enemys.valueAt(i));
//			}
//			enemys = null;
//		}
//		
//		if (cursorList != null) {
//			for (int i = 0; i < cursorList.size(); i++) {
//				detachEntity(cursorList.get(i));
//			}
//			cursorList = null;
//		}
//		if (obstacleList != null) {
//			for (int i = 0; i < obstacleList.size(); i++) {
//				detachEntity(obstacleList.get(i));
//			}
//			obstacleList = null;
//		}
		
		gameManager = null;
		if (mDamageText != null) {
			detachEntity(mDamageText);
		}
	
		if (mBackgroundSprite != null) {
			detachEntity(mBackgroundSprite);
		}
		
		if (mMapBattleSelectMenuLayer != null) {
			detachEntity(mMapBattleSelectMenuLayer.getMenuRectangle());
			mMapBattleSelectMenuLayer = null;
		}
		if (mMapBattleCutInLayer != null) {
			getBaseActivity().runOnUpdateThread(new Runnable() {
				@Override
				public void run() {
					mMapBattleCutInLayer.removeAll();
					mMapBattleCutInLayer = null;
				}
			});
		}
		if (mMapBattleTouchLayer != null) {
			detachEntity(mMapBattleTouchLayer);
		}
		if (mPlayerStatusRect != null) {
			detachEntity(mPlayerStatusRect);
		}
		if (mEnemyStatusRect != null) {
			detachEntity(mEnemyStatusRect);
		}
		
		if (mTalkLayer != null) {
			detachEntity(mTalkLayer);
			mTalkLayer = null;
		}
		
		mMapBattleInfoDto = null;
	}
}
