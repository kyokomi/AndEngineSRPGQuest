package com.kyokomi.srpgquest.scene;

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

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.PlayerSprite;
import com.kyokomi.srpgquest.GameManager;

import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.KeyEvent;

public class MapBattleScene extends KeyListenScene 
	implements IOnSceneTouchListener{
	
	private GameManager gameManager;
	
	private PlayerSprite player;
	
	public MapBattleScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}
	
	@Override
	public void init() {
//		testShowGrid();
//		testBtnCreate();
//		// プレイヤー配置
//		player = new PlayerSprite(this, 1, 0, 0);
//		player.setPlayerToAttackPosition();
//		attachChild(player.getLayer());
//		talkTextInit();
		
		// ゲーム開始
		gameManager = new GameManager(this);
		gameManager.mapInit(10, 10, 1f);
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
	}
	
	public void createPlayerSprite(int playerId) {
		// キャラ
	}
	public void createEnemySprite(int enemyId) {
		// 敵
	}
	public void createObstacleSprite() {
		// 障害物
	}
	public void createCursorSprite() {
		// 移動または攻撃可能範囲のカーソル
	}
	public void createSelectMenuSprite() {
		// キャラ選択時の行動選択メニュー
	}

	@Override
	public void prepareSoundAndMusic() {
			
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			if (talkTextLayer.contains(x, y)) {
				// 次のテキストを表示して最後までいけば閉じる
				talkClose();
				return true;
			}
		}
		return false;
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
		int base = 42;
		int baseGrid = 400;
		
		for (int x = -10 ; x < 20; x++) {
			final Line line = new Line(base * x, 0, (x * base) + baseGrid, getWindowHeight(), getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(255, 255, 255);
			attachChild(line);
		}
		
		for (int y = -10 ; y < 20; y++) {
			final Line line = new Line(0, (base * y), getWindowWidth(), (y * base) - (baseGrid / 2), getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(255, 255, 255);
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
						player.setPlayerToAttackPosition();
						break;
					case 2:
						player.setPlayerToJumpPositon();
						break;
					case 3:
						player.setPlayerToSlidePositon();
						break;
					case 4:
						player.attack();
						break;
					case 5:
						talk(player.getPlayerTalk(), "テストです");
						break;
					case 6:
						talk(player.getPlayerTalk(), "これ以上の会話はできません");
						break;
					case 7:
						player.setPlayerToAttackPosition();
						break;
					case 8:
						player.setPlayerToAttackPosition();
						break;
					case 9:
						player.setPlayerToAttackPosition();
						break;
					default:
						break;
					}
				}
			}
		});
	}
}
