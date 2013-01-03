package com.kyokomi.srpgquest.scene;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
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
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
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
	public void init() {
		// 初期化
		players = new SparseArray<PlayerSprite>();
		enemys = new SparseArray<PlayerSprite>();
		cursorList = new ArrayList<Rectangle>();
		
		// グリッド線表示
		testShowGrid();
		
		// ゲーム開始
		gameManager = new GameManager(this);
		gameManager.mapInit(10, 10, 1f);
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
		
		// メニューを作っておく
		createSelectMenuSprite();
	}
	
	/**
	 * プレイヤーキャラ描画.
	 * @param playerId
	 * @param imageId
	 * @param mapPoint
	 */
	public void createPlayerSprite(int playerId, int imageId, MapPoint mapPoint) {
		PlayerSprite player = new PlayerSprite(this, imageId, playerId);
		player.setPlayerToDefaultPosition();
		player.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		player.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		attachChild(player.getLayer());
		players.put(playerId, player);
	}
	/**
	 * 敵キャラ描画.
	 * @param enemyId
	 * @param imageId
	 * @param mapPoint
	 */
	public void createEnemySprite(int enemyId, int imageId, MapPoint mapPoint) {
		PlayerSprite enemy = new PlayerSprite(this, imageId, enemyId);
		enemy.setPlayerToDefaultPosition();
		enemy.setPlayerPosition(mapPoint.getX(), mapPoint.getY());
		enemy.setPlayerSize(mapPoint.getGridSize(), mapPoint.getGridSize());
		attachChild(enemy.getLayer());
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
	 * カーソル描画.
	 * @param mapPoint
	 */
	public void createCursorSprite(MapPoint mapPoint) {
		// 移動または攻撃可能範囲のカーソル
		Rectangle cursor = new Rectangle(
				mapPoint.getX(), mapPoint.getY(),
				mapPoint.getGridSize(), 
				mapPoint.getGridSize(), 
				getBaseActivity().getVertexBufferObjectManager());
		cursor.setColor(Color.GREEN);
		cursor.setAlpha(0.4f);
		attachChild(cursor);
		cursorList.add(cursor);
	}
	
	public void hideCursorSprite() {
		for (Rectangle cursor : cursorList) {
			cursor.detachChildren();
			cursor.detachSelf();
		}
	}
	
	private Rectangle selectMenuBackground;
	
	public void showSelectMenu() {
		selectMenuBackground.setY(0);
	}
	
	public void createSelectMenuSprite() {
		// キャラ選択時の行動選択メニュー
		selectMenuBackground = new Rectangle(
				0, 0,
				getWindowWidth(), getWindowHeight(), 
				getBaseActivity().getVertexBufferObjectManager());
		selectMenuBackground.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		selectMenuBackground.setColor(0, 0, 0);
		selectMenuBackground.setAlpha(0.7f);
		selectMenuBackground.setZIndex(10);
		attachChild(selectMenuBackground);
		selectMenuBackground.setY(getWindowHeight() * 2);
		
		sortChildren();
		
		// 各ボタン配置
		ButtonSprite btnRanking = getResourceButtonSprite("attack_btn.gif", "attack_btn_p.gif");
		attachButtonSprite(selectMenuBackground, 1, btnRanking, 100, selectMenuOnClickListener);
		
		ButtonSprite btnRetry = getResourceButtonSprite("move_btn.gif", "move_btn_p.gif");
		attachButtonSprite(selectMenuBackground, 2, btnRetry, 150, selectMenuOnClickListener);
		
		ButtonSprite btnTweet = getResourceButtonSprite("wait_btn.gif", "wait_btn_p.gif");
		attachButtonSprite(selectMenuBackground, 3, btnTweet, 200, selectMenuOnClickListener);
		
		ButtonSprite btnCancel = getResourceButtonSprite("cancel_btn.gif", "cancel_btn_p.gif");
		attachButtonSprite(selectMenuBackground, 4, btnCancel, 250, selectMenuOnClickListener);
	}
	/**
	 * キャラ選択画面のボタン押下時.
	 */
	private ButtonSprite.OnClickListener selectMenuOnClickListener = new ButtonSprite.OnClickListener() {
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			switch (pButtonSprite.getTag()) {
			case 1:
				players.get(selectPlayerId).attack2(null);
				break;
			case 2:
				gameManager.showMoveDistCursor(selectMapPoint.getX(), selectMapPoint.getY());
				break;
			case 3:
				hideCursorSprite();
				break;
			case 4:
				break;
			default:
				break;
			}
			
			// Hide
			selectMenuBackground.setY(getWindowHeight() * 2);
		}
	};
	
	@Override
	public void prepareSoundAndMusic() {
			
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}

	private boolean isPlayerTouch;
	private int selectPlayerId;
	private MapPoint selectMapPoint;
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			if (!isPlayerTouch) {
				int playerId = gameManager.getTouchPositionToPlayerId(x, y);
				if (playerId != 0 && players.indexOfKey(playerId) >= 0) {
					selectPlayerId = playerId;
					selectMapPoint = gameManager.getTouchPositionToMapPoint(x, y);
					
					// TODO: 行動可能なプレイヤーなら行動メニューを表示
					showSelectMenu();
					
//					// 移動範囲表示
//					gameManager.showMoveDistCursor(x, y);
//					// プレイヤータッチ判定
//					touchPlayer(playerId);
				}
			}
			
			if (talkTextLayer != null && talkTextLayer.contains(x, y)) {
				// 次のテキストを表示して最後までいけば閉じる
				talkClose();
				return true;
			}
		}
		return false;
	}
	
	private void touchPlayer(int playerId) {
		
		isPlayerTouch = true;

		PlayerSprite playerSprite = players.get(playerId);
		playerSprite.attack2(new IEntityModifier.IEntityModifierListener() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				isPlayerTouch = false;
			}
		});
	}

	// ----- 会話用 -------
	private Rectangle talkTextLayer;
	private Text talkText;
	private SparseArray<TiledSprite> talkFaceList;
	
	private void talkTextInit() {
		
		// 会話ウィンドウ
		talkTextLayer = new Rectangle(
				0, 0,
				getWindowWidth(), 
				96, 
				getBaseActivity().getVertexBufferObjectManager());
		talkTextLayer.setColor(Color.TRANSPARENT);
		attachChild(talkTextLayer);
		
		Texture texture = new BitmapTextureAtlas(
				getBaseActivity().getTextureManager(), 512, 512, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		Font font = new Font(getBaseActivity().getFontManager(), 
				texture, Typeface.DEFAULT_BOLD, 22, true, Color.WHITE);
		
		// EngineのTextureManagerにフォントTextureを読み込み
		getBaseActivity().getTextureManager().loadTexture(texture);
		getBaseActivity().getFontManager().loadFont(font);
		
		talkText = new Text(20, 20, font, 
				"******************************************************************", 
				new TextOptions(HorizontalAlign.LEFT), 
				getBaseActivity().getVertexBufferObjectManager());
		talkText.setColor(Color.TRANSPARENT);
		talkTextLayer.attachChild(talkText);
		talkTextLayer.setAlpha(0.0f);
		
		talkFaceList = new SparseArray<TiledSprite>();
	}
	private void talk(TiledSprite faceSprite, String text) {
		if (talkFaceList.indexOfKey(faceSprite.getTag())< 0) {
			talkTextLayer.attachChild(faceSprite);
			talkFaceList.put(faceSprite.getTag(), faceSprite);
		}
		faceSprite.setCurrentTileIndex(0);
		faceSprite.setAlpha(1.0f);
		faceSprite.setPosition(0, 0);
		
		talkTextLayer.setAlpha(1.0f);
		talkTextLayer.setPosition(0, getWindowHeight() - talkTextLayer.getHeight());
		
		talkText.setColor(Color.WHITE);
		talkText.setPosition(faceSprite.getWidth(), 0);
		talkText.setText(text);
	}
	private void talkClose() {
		talkTextLayer.setY(getWindowHeight());
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
	
	// ---- test用ボタン ----
	
	private SparseArray<Sprite> btnSpriteMap;
	
	private void testBtnCreate() {
		btnSpriteMap = new SparseArray<Sprite>();
		ButtonSprite btn1 = getResourceButtonSprite("p_ms1_0.gif", "p_ms1_1.gif");
		addMenu(btn1, 1, 0);
		ButtonSprite btn2 = getResourceButtonSprite("p_ms2_0.gif", "p_ms2_1.gif");
		addMenu(btn2, 2, getLeftWidthToX(btn1));
		ButtonSprite btn3 = getResourceButtonSprite("p_ms3_0.gif", "p_ms3_1.gif");
		addMenu(btn3, 3, getLeftWidthToX(btn2));
		ButtonSprite btn4 = getResourceButtonSprite("p_ms4_0.gif", "p_ms4_1.gif");
		addMenu(btn4, 4, getLeftWidthToX(btn3));
		ButtonSprite btn5 = getResourceButtonSprite("p_ms5_0.gif", "p_ms5_1.gif");
		addMenu(btn5, 5, getLeftWidthToX(btn4));
		ButtonSprite btn6 = getResourceButtonSprite("p_ms6_0.gif", "p_ms6_1.gif");
		addMenu(btn6, 6, getLeftWidthToX(btn5));
		ButtonSprite btn7 = getResourceButtonSprite("p_ms7_0.gif", "p_ms7_1.gif");
		addMenu(btn7, 7, getLeftWidthToX(btn6));
		ButtonSprite btn8 = getResourceButtonSprite("p_ms8_0.gif", "p_ms8_1.gif");
		addMenu(btn8, 8, getLeftWidthToX(btn7));
		ButtonSprite btn9 = getResourceButtonSprite("p_ms9_0.gif", "p_ms9_1.gif");
		addMenu(btn9, 9, getLeftWidthToX(btn8));
	}
	private float getLeftWidthToX(Sprite sprite) {
		return (0 + 2 + sprite.getX() + (sprite.getWidth()));
	}
	private void addMenu(ButtonSprite menuSprite, int tag, float x) {
		menuSprite.setTag(tag);
		menuSprite.setPosition(x, 0);
		attachChild(menuSprite);
		btnSpriteMap.put(tag, menuSprite);
		registerTouchArea(menuSprite);
		menuSprite.setOnClickListener(new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (btnSpriteMap.indexOfKey(pButtonSprite.getTag()) >= 0) {
					final Sprite sprite = btnSpriteMap.get(pButtonSprite.getTag());
					
					switch (sprite.getTag()) {
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					case 8:
						break;
					case 9:
						break;
					default:
						break;
					}
				}
			}
		});
	}
}
