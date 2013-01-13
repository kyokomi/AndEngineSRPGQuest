package com.kyokomi.srpgquest.scene;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.PlayerTalkDto.TalkDirection;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.PlayerSprite;
import com.kyokomi.core.sprite.TalkLayer;

import android.util.SparseArray;
import android.view.KeyEvent;

public class SandboxScene extends KeyListenScene 
	implements IOnSceneTouchListener{
	
	private PlayerSprite player;
	private PlayerSprite enemy;
	private TalkLayer talkLayer;
	
	public SandboxScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}
	
	@Override
	public void init() {
		testShowGrid();
		testBtnCreate();
		
		// プレイヤー配置
		player = new PlayerSprite(this, 
				0, 0, getWindowWidth(), getWindowHeight(), 
				110, 1, 2.0f,
				getBaseActivity().getVertexBufferObjectManager());
		player.setPlayerToAttackPosition();
		player.setPlayerFlippedHorizontal(true);
		player.setPlayerPosition(130, 150);
		attachChild(player);
		
		enemy = new PlayerSprite(this, 
				0, 0, getWindowWidth(), getWindowHeight(), 
				34, 2, 2.0f,
				getBaseActivity().getVertexBufferObjectManager());
		enemy.setPlayerToAttackPosition();
		enemy.setPlayerPosition(getWindowWidth() - 100, 150);
		attachChild(enemy);

		// 会話プレイヤーリストを作成
		SparseArray<TiledSprite> playerSprite = new SparseArray<TiledSprite>();
		playerSprite.put(player.getPlayerId(), player.getPlayerTalk());
		playerSprite.put(enemy.getPlayerId(), enemy.getPlayerTalk());
		
		// 会話内容を作成
		List<PlayerTalkDto> talks = new ArrayList<PlayerTalkDto>();
		talks.add(new PlayerTalkDto(player.getPlayerId(), 0, TalkDirection.TALK_DIRECT_LEFT,
				"これは、ゲームであっても、遊びではない。"));
		talks.add(new PlayerTalkDto(enemy.getPlayerId(), 0, TalkDirection.TALK_DIRECT_RIGHT,
				"こんにちわ。"));
		talks.add(new PlayerTalkDto(player.getPlayerId(), 3, TalkDirection.TALK_DIRECT_LEFT,
				"言っとくが俺はソロだ。\n１日２日オレンジになるくらいどおって事ないぞ。"));
		talks.add(new PlayerTalkDto(player.getPlayerId(), 2, TalkDirection.TALK_DIRECT_LEFT,
				"レベルなんてタダの数字だよ。\nこの世界での強さは、単なる幻想に過ぎない。\nそんなものよりもっと大事なものがある。"));
		talks.add(new PlayerTalkDto(enemy.getPlayerId(), 1, TalkDirection.TALK_DIRECT_RIGHT,
				"なんでや！！\n何でディアベルハンを見殺しにしたんや！"));
		
		// 会話レイヤーを生成
		talkLayer = new TalkLayer(this);
		talkLayer.initTalk(playerSprite, talks);
		attachChild(talkLayer);
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
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
			if (talkLayer.contains(x, y)) {
				
				if (talkLayer.nextTalk()) {
					return true;
				}
				talkLayer.hide();
			}
			
//			if (talkTextLayer.contains(x, y)) {
//				// 次のテキストを表示して最後までいけば閉じる
//				talkClose();
//				return true;
//			}
		}
		return false;
	}

	// ---- グリッド表示 ----
	
	private void testShowGrid() {
		int base = 42;
		int baseX = (int)(base * 1.5);
		int baseGrid = 400;
		
		for (int x = -10 ; x < 20; x++) {
			final Line line = new Line(baseX * x, 0, (x * baseX) + baseGrid, getWindowHeight(), getBaseActivity().getVertexBufferObjectManager());
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
						player.attack2();
						break;
					case 5:
//						player.setPlayerToAttackPosition();
						talkLayer.resetTalk();
						talkLayer.nextTalk();
						break;
					case 6:
						enemy.attack2();
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
