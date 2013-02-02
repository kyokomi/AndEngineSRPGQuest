package com.kyokomi.srpgquest.scene;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.FadeInModifier;
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
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.MenuRectangle;
import com.kyokomi.core.sprite.PlayerSprite;
import com.kyokomi.core.sprite.PlayerStatusRectangle;
import com.kyokomi.core.sprite.MenuRectangle.MenuDirection;
import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.sprite.CursorRectangle;

public class MapBattleScene extends KeyListenScene 
	implements IOnSceneTouchListener{
	
	enum LayerZIndex {
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
	
	private GameManager gameManager;
	
	private SparseArray<PlayerSprite> players;
	private SparseArray<PlayerSprite> enemys;
	private List<CursorRectangle> cursorList;
	
	private Text mDamageText;
	
	private MenuRectangle mMenuRectangle;
	private PlayerStatusRectangle mPlayerStatusRect;
	private PlayerStatusRectangle mEnemyStatusRect;
	
	public MapBattleScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}
	
	@Override
	public void prepareSoundAndMusic() {
			
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}
	
	@Override
	public void init() {
		// 初期化
		players = new SparseArray<PlayerSprite>();
		enemys = new SparseArray<PlayerSprite>();
		cursorList = new ArrayList<CursorRectangle>();
		// デフォルトフォント初期化
		initFont(16);
	
		// ダメージテキスト初期化
		initDamageText();
		
		// グリッド線表示
		testShowGrid();
		
		// カットイン初期化
		initCutInSprite();
	
		// メニューを作っておく
		createSelectMenuSprite();

		// ゲーム開始
		gameManager = new GameManager(this);
		gameManager.mapInit(10, 10, 1f); // 10 x 10 スケール1倍のグリッドマップ
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
			
		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
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
	 * @param playerId
	 * @param imageId
	 * @param mapPoint
	 */
	public void createPlayerSprite(ActorPlayerDto playerActor, MapPoint mapPoint) {
		PlayerSprite player = new PlayerSprite(playerActor, this, 
				0, 0, getWindowWidth(), getWindowHeight(), 1.0f,
				getBaseActivity().getVertexBufferObjectManager());
		
		player.setPlayerToDefaultPosition();
		player.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		player.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		player.setZIndex(LayerZIndex.ACTOR_LAYER.getValue());
		attachChild(player);
		players.put(playerActor.getPlayerId(), player);
		
		PlayerStatusRectangle playerStatusRect = initStatusWindow(player, 0);
		playerStatusRect.setZIndex(LayerZIndex.POPUP_LAYER.getValue());
		playerStatusRect.setColor(Color.BLUE);
		playerStatusRect.setAlpha(0.5f);
	}
	/**
	 * 敵キャラ描画.
	 * @param enemyId
	 * @param imageId
	 * @param mapPoint
	 */
	public void createEnemySprite(ActorPlayerDto enemyActor, MapPoint mapPoint) {
		PlayerSprite enemy = new PlayerSprite(enemyActor, this, 
				0, 0, getWindowWidth(), getWindowHeight(), 1.0f,
				getBaseActivity().getVertexBufferObjectManager());
		
		enemy.setPlayerToDefaultPosition();
		enemy.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		enemy.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		enemy.setZIndex(LayerZIndex.ACTOR_LAYER.getValue());
		attachChild(enemy);
		enemys.put(enemyActor.getPlayerId(), enemy);
		
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
		TiledSprite obstacle = getResourceTiledSprite("icon_set.png", 16, 48);
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
	private PlayerStatusRectangle initStatusWindow(PlayerSprite actorSprite, float y) {
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
	 * @param playerId
	 */
	public void refreshPlayerStatusWindow(int playerId) {
		PlayerSprite player = players.get(playerId);
		player.getPlayerStatusRectangle().refresh();
	}
	/**
	 * 敵ステータス更新.
	 * @param enemyId
	 */
	public void refreshEnemyStatusWindow(int enemyId) {
		PlayerSprite enemy = enemys.get(enemyId);
		enemy.getPlayerStatusRectangle().refresh();
	}
	/**
	 * プレイヤーキャラ消去.
	 * @param playerId
	 */
	public void removePlayer(final int playerId) {
		// 別スレッドで削除
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				PlayerSprite player = players.get(playerId);
				player.detachSelf();
				players.remove(playerId);
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
				PlayerSprite enemy = enemys.get(enemyId);
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
	 * @param playerId
	 * @param moveMapPointList
	 */
	public void movePlayerAnimation(int playerId, List<MapPoint> moveMapPointList, 
			final IAnimationCallback animationCallback) {
		PlayerSprite playerSprite = players.get(playerId);
		playerSprite.move(1.0f, moveMapPointList, new IEntityModifier.IEntityModifierListener() {
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
	 * @param playerId
	 * @param moveMapPointList
	 */
	public void moveEnemyAnimation(int enemyId, List<MapPoint> moveMapPointList, 
			final IAnimationCallback animationCallback) {
		PlayerSprite enemySprite = enemys.get(enemyId);
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
	 * @param playerId
	 */
	public void startWalkingPlayerAnimation(int playerId) {
		PlayerSprite playerSprite = players.get(playerId);
		playerSprite.setPlayerToDefaultPosition();
	}
	/**
	 * プレイヤー歩行停止.
	 * @param playerId
	 */
	public void stopWalkingPlayerAnimation(int playerId) {
		PlayerSprite playerSprite = players.get(playerId);
		playerSprite.setPlayerToDefaultPositionStop();
	}

	/**
	 * エネミー歩行スタート.
	 * @param enemyId
	 */
	public void startWalkingEnemyAnimation(int enemyId) {
		PlayerSprite enemySprite = enemys.get(enemyId);
		enemySprite.setPlayerToDefaultPosition();
	}
	/**
	 * エネミー歩行停止.
	 * @param enemyId
	 */
	public void stopWalkingEnemyAnimation(int enemyId) {
		PlayerSprite enemySprite = enemys.get(enemyId);
		enemySprite.setPlayerToDefaultPositionStop();
	}
	
	/**
	 * 指定のMapPointの位置にダメージ値をテキストで拡大アニメーション表示する.
	 * 表示が終わったら消えます。
	 * @param damage ダメージ値
	 * @param mapPoint 表示位置
	 */
	public void showDamageText(final int damage, final MapPoint mapPoint) {
		
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
		float x = mapPoint.getX();
		// 横は画面半分のどっち側にいるかで表示位置を垂直方向に反転させる
		if (x < getWindowWidth() / 2) {
			x = x + 40;
		} else {
			x = x - mMenuRectangle.getWidth();
		}
		// 縦が画面外に入る場合は補正
		float y = mapPoint.getY();
		if ((y + mMenuRectangle.getHeight()) > getWindowHeight()) {
			y = getWindowHeight() - mMenuRectangle.getHeight();
		}
		mMenuRectangle.setX(x);
		mMenuRectangle.setY(y);
		
		// 攻撃
		IEntity attackMenuItem = mMenuRectangle.getChildByTag(
				SelectMenuType.SELECT_MENU_ATTACK_TYPE.getValue());
		if (attackMenuItem instanceof ButtonSprite) {
			((ButtonSprite) attackMenuItem).setEnabled(!isAttackDone);
			((ButtonSprite) attackMenuItem).setVisible(!isAttackDone); // 非活性ボタンがあればイラナイ
		}
		// 移動
		IEntity moveMenuItem = mMenuRectangle.getChildByTag(
				SelectMenuType.SELECT_MENU_MOVE_TYPE.getValue());
		if (moveMenuItem instanceof ButtonSprite) {
			((ButtonSprite) moveMenuItem).setEnabled(!isMovedDone);
			((ButtonSprite) moveMenuItem).setVisible(!isMovedDone); // 非活性ボタンがあればイラナイ
		}
		
		// 非表示
		mMenuRectangle.setEnabled(true);
		mMenuRectangle.setVisible(true);
	}
	public void hideSelectMenu() {
		// 非表示
		mMenuRectangle.setEnabled(false);
		mMenuRectangle.setVisible(false);
	}
	
	enum SelectMenuType {
		SELECT_MENU_ATTACK_TYPE(1),
		SELECT_MENU_MOVE_TYPE(2),
		SELECT_MENU_WAIT_TYPE(3),
		SELECT_MENU_CANCEL_TYPE(4),
		;
		
		private Integer value;
		
		private SelectMenuType(Integer value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
	}
	public void createSelectMenuSprite() {
					
		mMenuRectangle = new MenuRectangle(
				getWindowWidth() / 2, getWindowHeight() / 2, 
				getWindowWidth(), getWindowHeight(), 
				getBaseActivity().getVertexBufferObjectManager());
		mMenuRectangle.setZIndex(LayerZIndex.POPUP_LAYER.getValue());
		
		// 各ボタン配置
		ButtonSprite btnAttack = getResourceButtonSprite("attack_btn.gif", "attack_btn_p.gif");
		mMenuRectangle.addMenuItem(SelectMenuType.SELECT_MENU_ATTACK_TYPE.getValue(), btnAttack);
		btnAttack.setOnClickListener(selectMenuOnClickListener);
		
		ButtonSprite btnMove = getResourceButtonSprite("move_btn.gif", "move_btn_p.gif");
		mMenuRectangle.addMenuItem(SelectMenuType.SELECT_MENU_MOVE_TYPE.getValue(), btnMove);
		btnMove.setOnClickListener(selectMenuOnClickListener);
		
		ButtonSprite btnWait = getResourceButtonSprite("wait_btn.gif", "wait_btn_p.gif");
		mMenuRectangle.addMenuItem(SelectMenuType.SELECT_MENU_WAIT_TYPE.getValue(), btnWait);
		btnWait.setOnClickListener(selectMenuOnClickListener);
		
		ButtonSprite btnCancel = getResourceButtonSprite("cancel_btn.gif", "cancel_btn_p.gif");
		mMenuRectangle.addMenuItem(SelectMenuType.SELECT_MENU_CANCEL_TYPE.getValue(), btnCancel);
		btnCancel.setOnClickListener(selectMenuOnClickListener);
		
		mMenuRectangle.create(MenuDirection.MENU_DIRECTION_Y);
		attachChild(mMenuRectangle);
		registerTouchArea(mMenuRectangle);

		// 非表示にする
		hideSelectMenu();
	}
	// --------------- カットイン系 --------------
	private Sprite mPlayerTurnCutInSprite;
	private Sprite mEnemyTurnCutInSprite;
	private Sprite mPlayerWinCutInSprite;
	private Sprite mGameOverCutInSprite;
	
	private void initCutInSprite() {
		mPlayerTurnCutInSprite = getResourceSprite("player_turn.png");
		attachChildCutInSprite(mPlayerTurnCutInSprite);
		mEnemyTurnCutInSprite = getResourceSprite("enemy_turn.png");
		attachChildCutInSprite(mEnemyTurnCutInSprite);
		mPlayerWinCutInSprite = getResourceSprite("player_win.png");
		attachChildCutInSprite(mPlayerWinCutInSprite);
		mGameOverCutInSprite = getResourceSprite("game_over.png");
		attachChildCutInSprite(mGameOverCutInSprite);
	}
	private void attachChildCutInSprite(Sprite sprite) {
		placeToCenter(sprite);
		sprite.setAlpha(0.0f);
		sprite.setZIndex(LayerZIndex.CUTIN_LAYER.getValue());
		attachChild(sprite);
	}
	public void showPlayerTurn(IAnimationCallback animationCallback) {
		showCutInSprite(mPlayerTurnCutInSprite, animationCallback);
	}
	public void showEnemyTurn(IAnimationCallback animationCallback) {
		showCutInSprite(mEnemyTurnCutInSprite, animationCallback);
	}
	public void showPlayerWin(IAnimationCallback animationCallback) {
		showCutInSprite(mPlayerWinCutInSprite, animationCallback);
	}
	public void showGameOver(IAnimationCallback animationCallback) {
		showCutInSprite(mGameOverCutInSprite, animationCallback);
	}
	private void showCutInSprite(final IEntity pEntity, final IAnimationCallback animationCallback) {
		pEntity.registerEntityModifier(new FadeInModifier(2.0f, new IEntityModifier.IEntityModifierListener() {
			@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
			@Override public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				animationCallback.doAction(); // コールバック呼び出し
				pEntity.setAlpha(0.0f);
			}
		}));
	}
	// --------------- ステータスウィンドウ --------------
	public void showPlayerStatusWindow(int playerId) {
		if (mPlayerStatusRect != null) {
			detachChild(mPlayerStatusRect);
		}
		// エネミーが表示されていたら下に表示
		float y = 0;
		if (mEnemyStatusRect != null && mEnemyStatusRect.isVisible()) {
			y = mEnemyStatusRect.getY() + mEnemyStatusRect.getHeight();
		}
		mPlayerStatusRect = players.get(playerId).getPlayerStatusRectangle();
		if (mPlayerStatusRect != null) {
			mPlayerStatusRect.setVisible(true);
			mPlayerStatusRect.setY(y);
			attachChild(mPlayerStatusRect);	
		}
		sortChildren();
	}
	public void showEnemyStatusWindow(int enemyId) {
		if (mEnemyStatusRect != null) {
			detachChild(mEnemyStatusRect);
		}
		// プレイヤーが表示されていたら下に表示
		float y = 0;
		if (mPlayerStatusRect != null && mPlayerStatusRect.isVisible()) {
			y = mPlayerStatusRect.getY() + mPlayerStatusRect.getHeight();
		}
		mEnemyStatusRect = enemys.get(enemyId).getPlayerStatusRectangle();
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
	 * キャラ選択画面のボタン押下時.
	 */
	private ButtonSprite.OnClickListener selectMenuOnClickListener = new ButtonSprite.OnClickListener() {
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			gameManager.touchMenuBtnEvent(pButtonSprite.getTag());
		}
	};

	/**
	 * 画面タッチイベント.
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			// タッチイベント振り分け処理を呼ぶ
			gameManager.onTouchMapItemEvent(x, y);
		}
		return false;
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
