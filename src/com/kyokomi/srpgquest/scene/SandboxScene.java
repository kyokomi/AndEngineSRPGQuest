package com.kyokomi.srpgquest.scene;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.PlayerSprite;
import com.kyokomi.core.utils.SPUtil;

import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.KeyEvent;

public class SandboxScene extends KeyListenScene implements ButtonSprite.OnClickListener{

	private SparseArray<Sprite> btnSpriteMap;
	
	private PlayerSprite player;
	
	public SandboxScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}
	
	@Override
	public void init() {
		
		btnSpriteMap = new SparseArray<Sprite>();
		
		ButtonSprite btn1 = getResourceButtonSprite("p_ms1_0.gif", "p_ms1_1.gif");
		addMenu(btn1, 1, 200);
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
		
		// プレイヤー配置
		player = new PlayerSprite(this, 0, 0);
		player.setPlayerToAttackPosition();
		attachChild(player.getLayer());
		
	}
	
	private float getLeftWidthToX(Sprite sprite) {
		return (200 - 2 - sprite.getX() - (sprite.getWidthScaled() / 2));
	}
	private void addMenu(ButtonSprite menuSprite, int tag, float x) {
		menuSprite.setTag(tag);
		placeToCenterX(menuSprite, 600);
		menuSprite.setX(menuSprite.getX() - x);
		attachChild(menuSprite);
		btnSpriteMap.put(tag, menuSprite);
		registerTouchArea(menuSprite);
		menuSprite.setOnClickListener(this);
	}

	@Override
	public void prepareSoundAndMusic() {
			
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}

	
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
				player.talk(500, "日本語でOK?");
				break;
			case 6:
				player.talk(500, "タッチできません");
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
}
