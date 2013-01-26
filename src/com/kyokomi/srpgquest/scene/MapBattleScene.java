package com.kyokomi.srpgquest.scene;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.MenuRectangle;
import com.kyokomi.core.sprite.PlayerSprite;
import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.map.common.MapPoint;

public class MapBattleScene extends KeyListenScene 
	implements IOnSceneTouchListener{
	
	private GameManager gameManager;
	
	private SparseArray<PlayerSprite> players;
	private SparseArray<PlayerSprite> enemys;
	private List<Rectangle> cursorList;
	
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
		cursorList = new ArrayList<Rectangle>();
		// デフォルトフォント初期化
		initFont(16);
		
		// グリッド線表示
		testShowGrid();
		
		// ゲーム開始
		gameManager = new GameManager(this);
		gameManager.mapInit(10, 10, 1f);
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
		
		// メニューを作っておく
		createSelectMenuSprite();

		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}
	
	/**
	 * プレイヤーキャラ描画.
	 * @param playerId
	 * @param imageId
	 * @param mapPoint
	 */
	public void createPlayerSprite(int playerId, int imageId, MapPoint mapPoint) {
		PlayerSprite player = new PlayerSprite(this, 
				0, 0, getWindowWidth(), getWindowHeight(), 
				imageId, playerId, 1.0f,
				getBaseActivity().getVertexBufferObjectManager());
		
		player.setPlayerToDefaultPosition();
		player.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		player.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		attachChild(player);
		players.put(playerId, player);
	}
	/**
	 * 敵キャラ描画.
	 * @param enemyId
	 * @param imageId
	 * @param mapPoint
	 */
	public void createEnemySprite(int enemyId, int imageId, MapPoint mapPoint) {
		PlayerSprite enemy = new PlayerSprite(this, 
				0, 0, getWindowWidth(), getWindowHeight(), 
				imageId, enemyId, 1.0f,
				getBaseActivity().getVertexBufferObjectManager());
		
		enemy.setPlayerToDefaultPosition();
		enemy.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		enemy.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		attachChild(enemy);
		enemys.put(enemyId, enemy);
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
		attachChild(obstacle);
	}
	
	/**
	 * 移動カーソル描画.
	 * @param mapPoint
	 */
	public void createMoveCursorSprite(MapPoint mapPoint) {
		createCursorSprite(mapPoint, Color.GREEN);
	}
	/**
	 * 攻撃カーソル描画.
	 * @param mapPoint
	 */
	public void createAttackCursorSprite(MapPoint mapPoint) {
		createCursorSprite(mapPoint, Color.YELLOW);
	}
	/**
	 * カーソル描画.
	 * @param mapPoint
	 */
	private void createCursorSprite(MapPoint mapPoint, Color color) {
		// 移動または攻撃可能範囲のカーソル
		Rectangle cursor = new Rectangle(
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
	}
	
	public void hideCursorSprite() {
		for (Rectangle cursor : cursorList) {
			cursor.detachChildren();
			cursor.detachSelf();
		}
	}
	
	/**
	 * プレイヤー移動アニメーション.
	 * @param playerId
	 * @param moveMapPointList
	 */
	public void movePlayerAnimation(int playerId, List<MapPoint> moveMapPointList) {
		PlayerSprite playerSprite = players.get(playerId);
		playerSprite.move(1.0f, moveMapPointList);
	}
	
	public void showSelectMenu() {
		// 非表示
		mMenuRectangle.setEnabled(true);
		mMenuRectangle.setVisible(true);
	}
	public void hideSelectMenu() {
		// 非表示
		mMenuRectangle.setEnabled(false);
		mMenuRectangle.setVisible(false);
	}
	
	private MenuRectangle mMenuRectangle;
	
	public void createSelectMenuSprite() {
					
		mMenuRectangle = new MenuRectangle(
				getWindowWidth() / 2, getWindowHeight() / 2, 
				getWindowWidth(), getWindowHeight(), 
				getBaseActivity().getVertexBufferObjectManager());
		// 各ボタン配置
		ButtonSprite btnAttack = getResourceButtonSprite("attack_btn.gif", "attack_btn_p.gif");
		mMenuRectangle.addMenuItem(1, btnAttack);
		btnAttack.setOnClickListener(selectMenuOnClickListener);
		
		ButtonSprite btnMove = getResourceButtonSprite("move_btn.gif", "move_btn_p.gif");
		mMenuRectangle.addMenuItem(2, btnMove);
		btnMove.setOnClickListener(selectMenuOnClickListener);
		
		ButtonSprite btnWait = getResourceButtonSprite("wait_btn.gif", "wait_btn_p.gif");
		mMenuRectangle.addMenuItem(3, btnWait);
		btnWait.setOnClickListener(selectMenuOnClickListener);
		
		ButtonSprite btnCancel = getResourceButtonSprite("cancel_btn.gif", "cancel_btn_p.gif");
		mMenuRectangle.addMenuItem(4, btnCancel);
		btnCancel.setOnClickListener(selectMenuOnClickListener);
		
		mMenuRectangle.create(2);
		attachChild(mMenuRectangle);
		registerTouchArea(mMenuRectangle);

		// 非表示にする
		hideSelectMenu();
	}
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

	private boolean isPlayerTouch;
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			
			if (!isPlayerTouch) {
				// タッチイベント振り分け処理を呼ぶ
				gameManager.onTouchMapItemEvent(x, y);
			}
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
