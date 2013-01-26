package com.kyokomi.srpgquest.scene;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
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
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException;
import org.andengine.util.modifier.IModifier.IModifierListener;

import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.MenuRectangle;
import com.kyokomi.core.sprite.PlayerSprite;
import com.kyokomi.srpgquest.GameManager;
import com.kyokomi.srpgquest.map.common.MapPoint;
import com.kyokomi.srpgquest.sprite.CursorRectangle;

public class MapBattleScene extends KeyListenScene 
	implements IOnSceneTouchListener{
	
	private GameManager gameManager;
	
	private SparseArray<PlayerSprite> players;
	private SparseArray<PlayerSprite> enemys;
	private List<CursorRectangle> cursorList;
	
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
		player.setZIndex(20);
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
		enemy.setZIndex(20);
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
		obstacle.setZIndex(20);
		attachChild(obstacle);
	}
	
	/**
	 * 移動カーソル描画.
	 * @param mapPoint
	 */
	public void createMoveCursorSprite(MapPoint mapPoint) {
		CursorRectangle cursorRectangle = createCursorSprite(mapPoint, Color.GREEN);
		cursorRectangle.setZIndex(10);
	}
	/**
	 * 攻撃カーソル描画.
	 * @param mapPoint
	 */
	public void createAttackCursorSprite(MapPoint mapPoint) {
		CursorRectangle cursorRectangle = createCursorSprite(mapPoint, Color.YELLOW);
		cursorRectangle.setZIndex(30);
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
	 * プレイヤー移動アニメーション.
	 * @param playerId
	 * @param moveMapPointList
	 */
	public void movePlayerAnimation(int playerId, List<MapPoint> moveMapPointList) {
		PlayerSprite playerSprite = players.get(playerId);
		playerSprite.move(1.0f, moveMapPointList, new IEntityModifier.IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				// カーソルを消す
				hideCursorSprite();				
			}
		});

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
		mMenuRectangle.setZIndex(40);
		
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
