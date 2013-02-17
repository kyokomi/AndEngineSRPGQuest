package com.kyokomi.srpgquest.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
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

import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.sprite.ActorSprite;
import com.kyokomi.core.sprite.PlayerStatusRectangle;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.core.utils.JsonUtil;
import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.dto.MapBattleInfoDto;
import com.kyokomi.srpgquest.layer.MapBattleCutInLayer;
import com.kyokomi.srpgquest.layer.MapBattleCutInLayer.MapBattleCutInLayerType;
import com.kyokomi.srpgquest.layer.MapBattleSelectMenuLayer;
import com.kyokomi.srpgquest.layer.MapBattleTouchLayer.MapBattleTouchLayerType;
import com.kyokomi.srpgquest.layer.MapBattleTouchLayer;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.sprite.CursorRectangle;

public class MapBattleScene extends SrpgBaseScene 
	implements IOnSceneTouchListener{
	
	public enum LayerZIndex {
		TALK_LAYER(80),
		CUTIN_LAYER(70),
		TEXT_LAYER(60),
		EFFETCT_LAYER(50),
		POPUP_LAYER(40),
		ATTACKCURSOR_LAYER(30),
		ACTOR_LAYER(20),
		MOVECURSOR_LAYER(10),
		BACKGROUND_LAYER(0),
		;
		private Integer value;
		private LayerZIndex(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
	}
	/** SRPGゲームマネージャー. */
	private GameManager gameManager;
	
	/** プレイヤーと敵情報. */
	private SparseArray<ActorSprite> players;
	private SparseArray<ActorSprite> enemys;
	/** カーソル表示リスト. */
	private List<CursorRectangle> cursorList;

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
	private MapBattleTouchLayer mMapBattleTouchLayer;
	
	// ----- SE, BGM -----
	private Sound mAttackSound;
	private Music mTutorialBGM;
	private Music mBattleBGM;
	private Music mClearBGM;
	private Music mGameOverBGM;
	
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
		// TODO: 本当はどのBGMが再生中か調べて再生するけど一旦バトルで
		playMusic(mBattleBGM);
	}
	/**
	 * バックグラウンド時
	 */
	@Override
	public void onPause() {
		stopPlayMusic();
	}
	@Override
	public void initSoundAndMusic() {
		// 効果音をロード
		try {
			mAttackSound = createSoundFromFileName("SE_ATTACK_ZANGEKI_01.wav");
			mTutorialBGM = createMusicFromFileName("tutorial_bgm1.mp3");
			mBattleBGM = createMusicFromFileName("battle_bgm1.mp3");
			mClearBGM = createMusicFromFileName("clear_bgm1.mp3");
			mGameOverBGM = createMusicFromFileName("game_over_bgm1.mp3");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	// ------- BGM関連 -----------
	public enum SoundType {
		ATTACK_SOUND,
		BTN_PRESSED_SOUND
	}
	public void playSound(SoundType soundType) {
		switch (soundType) {
		case ATTACK_SOUND:
			mAttackSound.play();
			break;
		case BTN_PRESSED_SOUND:
			getBtnPressedSound().play();
			break;
		default:
			break;
		}
		
	}
	private void playMusic(Music music) {
		if (music.isPlaying()) {
			return;
		}
		stopPlayMusic();
		
		music.setLooping(true);
		music.play();
	}
	private Music getPlayMusic() {
		if (mTutorialBGM.isPlaying()) {
			return mTutorialBGM;
		}
		if (mBattleBGM.isPlaying()) {
			return mBattleBGM;
		}
		if (mClearBGM.isPlaying()) {
			return mClearBGM;
		}
		if (mGameOverBGM.isPlaying()) {
			return mGameOverBGM;
		}
		return null;
	}
	private void stopPlayMusic() {
		Music music = getPlayMusic();
		if (music != null) {
			music.pause();
		}
	}
	private void releseMusic() {
		if (!mTutorialBGM.isReleased()) {
			mTutorialBGM.release();
		}
		if (!mBattleBGM.isReleased()) {
			mBattleBGM.release();
		}
		if (!mClearBGM.isReleased()) {
			mClearBGM.release();
		}
		if (!mGameOverBGM.isReleased()) {
			mGameOverBGM.release();
		}
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
		players = new SparseArray<ActorSprite>();
		enemys = new SparseArray<ActorSprite>();
		cursorList = new ArrayList<CursorRectangle>();
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
		mMapBattleTouchLayer = new MapBattleTouchLayer(this);
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
		playMusic(mTutorialBGM);
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
		mDamageText.setZIndex(LayerZIndex.TEXT_LAYER.getValue());
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
		player.setZIndex(LayerZIndex.ACTOR_LAYER.getValue());
		attachChild(player);
		players.put(playerSeqNo, player);
		
		PlayerStatusRectangle playerStatusRect = initStatusWindow(player, 0);
		playerStatusRect.setZIndex(LayerZIndex.POPUP_LAYER.getValue());
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
		enemy.setZIndex(LayerZIndex.ACTOR_LAYER.getValue());
		attachChild(enemy);
		enemys.put(enemySeqNo, enemy);
		
		PlayerStatusRectangle enemyStatusRect = initStatusWindow(enemy, 0);
		enemyStatusRect.setZIndex(LayerZIndex.POPUP_LAYER.getValue());
		enemyStatusRect.setColor(Color.RED);
		enemyStatusRect.setAlpha(0.5f);
	}
	
	/**
	 * 障害物描画.
	 * @param mapPoint
	 */
	public void createObstacleSprite(MapPoint mapPoint, int currentTileIndex) {
		TiledSprite obstacle = getIconSetTiledSprite();
		obstacle.setPosition(mapPoint.getX(), mapPoint.getY());
		obstacle.setCurrentTileIndex(currentTileIndex);
		obstacle.setSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		obstacle.setZIndex(LayerZIndex.ACTOR_LAYER.getValue());
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
			mPlayerStatusRectangle.setZIndex(LayerZIndex.POPUP_LAYER.getValue());
		}
		return mPlayerStatusRectangle;
	}
	
	/**
	 * プレイヤーステータス更新.
	 * @param playerSeqNo
	 */
	public void refreshPlayerStatusWindow(int playerSeqNo) {
		ActorSprite player = players.get(playerSeqNo);
		player.getPlayerStatusRectangle().refresh();
	}
	/**
	 * 敵ステータス更新.
	 * @param enemySeqNo
	 */
	public void refreshEnemyStatusWindow(int enemySeqNo) {
		ActorSprite enemy = enemys.get(enemySeqNo);
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
				ActorSprite player = players.get(playerSeqNo);
				player.detachSelf();
				players.remove(playerSeqNo);
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
				ActorSprite enemy = enemys.get(enemyId);
				enemy.detachSelf();
				enemys.remove(enemyId);
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
		cursorRectangle.setZIndex(LayerZIndex.MOVECURSOR_LAYER.getValue());
	}
	/**
	 * 攻撃カーソル描画.
	 * @param mapPoint
	 */
	public void createAttackCursorSprite(MapPoint mapPoint) {
		CursorRectangle cursorRectangle = createCursorSprite(mapPoint, Color.YELLOW);
		cursorRectangle.setZIndex(LayerZIndex.ATTACKCURSOR_LAYER.getValue());
	}
	/**
	 * カーソル選択.
	 */
	public void selectCursor(MapPoint mapPoint) {
		for (CursorRectangle cursorRectangle : cursorList) {
			if (mapPoint.isMuchMapPoint(cursorRectangle.getmMapPointX(), cursorRectangle.getmMapPointY())) {
				cursorRectangle.setColor(Color.BLUE);
				break;
			}
		}
		sortChildren();
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
		cursorList.add(cursor);
		
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
				for (Rectangle cursor : cursorList) {
					cursor.detachChildren();
					cursor.detachSelf();
				}
				cursorList = new ArrayList<CursorRectangle>();
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
		ActorSprite ActorSprite = players.get(playerSeqNo);
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
		ActorSprite enemySprite = enemys.get(enemySeqNo);
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
		ActorSprite ActorSprite = players.get(playerSeqNo);
		ActorSprite.setPlayerToDefaultPosition();
	}
	/**
	 * プレイヤー歩行停止.
	 * @param playerSeqNo
	 */
	public void stopWalkingPlayerAnimation(int playerSeqNo) {
		ActorSprite ActorSprite = players.get(playerSeqNo);
		ActorSprite.setPlayerToDefaultPositionStop();
	}

	/**
	 * エネミー歩行スタート.
	 * @param enemySeqNo
	 */
	public void startWalkingEnemyAnimation(int enemySeqNo) {
		ActorSprite enemySprite = enemys.get(enemySeqNo);
		enemySprite.setPlayerToDefaultPosition();
	}
	/**
	 * エネミー歩行停止.
	 * @param enemySeqNo
	 */
	public void stopWalkingEnemyAnimation(int enemySeqNo) {
		ActorSprite enemySprite = enemys.get(enemySeqNo);
		enemySprite.setPlayerToDefaultPositionStop();
	}
	
	/**
	 * 指定のMapPointの位置にダメージ値をテキストで拡大アニメーション表示する.
	 * 表示が終わったら消えます。
	 * @param damage ダメージ値
	 * @param mapPoint 表示位置
	 */
	public void showDamageText(final int damage, final MapPoint mapPoint) {
		
		playSound(SoundType.ATTACK_SOUND);
		
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
		switch (pMapBattleCutInLayerType) {
		case PLAYER_TURN_CUTIN:
			playMusic(mBattleBGM);
			break;
		case ENEMY_TURN_CUTIN:
			playMusic(mBattleBGM);
			break;
		case PLAYER_WIN_CUTIN:
			playMusic(mClearBGM);
			break;
		case GAME_OVER_CUTIN:
			playMusic(mGameOverBGM);
			break;
		default:
			return;
		}
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
		mTalkLayer.setZIndex(LayerZIndex.TALK_LAYER.getValue());
		attachChild(mTalkLayer);
		// 会話表示
		mTalkLayer.nextTalk();
	}
	
	// --------------- ステータスウィンドウ --------------
	public void showPlayerStatusWindow(int playerSeqNo, float x) {
		if (mPlayerStatusRect != null) {
			detachChild(mPlayerStatusRect);
		}
		// エネミーが表示されていたら下に表示
		float y = 0;
		if (mEnemyStatusRect != null && mEnemyStatusRect.isVisible()) {
			y = mEnemyStatusRect.getY() + mEnemyStatusRect.getHeight();
		}
		mPlayerStatusRect = players.get(playerSeqNo).getPlayerStatusRectangle();
		if (mPlayerStatusRect != null) {
			mPlayerStatusRect.setVisible(true);
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
		mEnemyStatusRect = enemys.get(enemySeqNo).getPlayerStatusRectangle();
		if (mEnemyStatusRect != null) {
			mEnemyStatusRect.setVisible(true);
			mEnemyStatusRect.setY(y);
			attachChild(mEnemyStatusRect);
		}
		sortChildren();
	}
	public void hidePlayerStatusWindow() {
		if (mPlayerStatusRect != null) {
			mPlayerStatusRect.setVisible(false);
		}
	}
	public void hideEnemyStatusWindow() {
		if (mEnemyStatusRect != null) {
			mEnemyStatusRect.setVisible(false);
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
				
				playSound(SoundType.BTN_PRESSED_SOUND);
				
				if (mTalkLayer.isNextTalk()) {
					mTalkLayer.nextTalk();
					
				} else {
					mTalkLayer.hide();
					// 次の会話がなくなれば、会話レイヤーを開放
					detachEntity(mTalkLayer);
					mTalkLayer = null;
					
					// 勝利条件表示
					playMusic(mBattleBGM);
					registerTouchArea(mMapBattleTouchLayer.showTouchLayer(
							MapBattleTouchLayerType.CLEAR_CONDITION_TOUCH));
				}
				
			} else if (mMapBattleTouchLayer.isTouchClerCondition(
					MapBattleTouchLayerType.CLEAR_CONDITION_TOUCH, x, y)) {
				
				playSound(SoundType.BTN_PRESSED_SOUND);
				
				// 勝利条件を非表示にする
				unregisterTouchArea(mMapBattleTouchLayer.hideTouchLayer(
						MapBattleTouchLayerType.CLEAR_CONDITION_TOUCH));
				
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
		// 音源を開放
		releseMusic();
		// 次のシナリオへ
		nextScenario(getScenarioEntity());
	}
	public void gameOverMapBattle() {
		// TODO: タイトルへ？
		releseMusic();
		getBaseActivity().backToInitial();
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
}
