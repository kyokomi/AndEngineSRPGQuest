package com.kyokomi.srpgquest.scene.part;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.sprite.TextButton;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.srpgquest.sprite.ActorSprite;

public class BattlePart extends AbstractGamePart {

	private Rectangle mBaseLayer;
	
	public BattlePart(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}

	/**
	 * @deprecated init(ActorPlayerDto player, ActorPlayerDto enemy)使って下さい
	 * @param saveDataDto
	 */
	@Override
	public void init(SaveDataDto saveDataDto) {
		
	}
	public void init(ActorPlayerDto player, ActorPlayerDto enemy) {
			
		// 上に重ねる用にBaseを用意
		mBaseLayer = new Rectangle(0, 0, 
				getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight(), 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		mBaseLayer.setColor(Color.TRANSPARENT);
		
		// 背景表示
		initBackground();
		// 背景画像の都合で表示位置が決まる
		float acotrBaseY = getBaseScene().getWindowHeight() / 2;
		
		// キャラ表示
		AnimatedSprite playerSprite = getBaseScene().getResourceAnimatedSprite(
				ActorSprite.getMoveFileName(player.getImageResId()), 3, 4);
		playerSprite.setSize(64, 64);
		// 右上から表示
		playerSprite.setPosition(getBaseScene().getWindowWidth() - 
				(getBaseScene().getWindowWidth() / 8) -
				playerSprite.getWidth(), 
				acotrBaseY);
		mBaseLayer.attachChild(playerSprite);
		
		// キャラ表示
		AnimatedSprite enemySprite = getBaseScene().getResourceAnimatedSprite(
				ActorSprite.getMoveFileName(enemy.getImageResId()), 3, 4);
		enemySprite.setSize(64, 64);
		// 左上から表示
		enemySprite.setPosition(getBaseScene().getWindowWidth() / 8, 
				acotrBaseY);
		mBaseLayer.attachChild(enemySprite);
		
		// ベースレイヤをattach
		getBaseScene().attachChild(mBaseLayer);
	}
	
	/**
	 * 背景表示.
	 */
	private void initBackground() {
		Sprite backgroundSprite = getBaseScene().getResourceSprite("bk/main_bg.jpg");
		backgroundSprite.setSize(getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight());
		backgroundSprite.setZIndex(-1);
		mBaseLayer.attachChild(backgroundSprite);
	}
	
	@Override
	public void touchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.isActionUp()) {
			Log.d("touchEvent", "onClick");
			if (showBattleMenuLayer(x, y) == false) {
				Log.d("showBattleMenuLayer", "false");
				mBaseLayer.getChildByTag(10000).setVisible(false);
				mBaseLayer.getChildByTag(10000).setPosition(-1000, -1000);
			} else {
				Log.d("showBattleMenuLayer", "true");
			}
		}
		//end();
	}
	
	public boolean showBattleMenuLayer(float x, float y) {
		if (mBaseLayer.getChildByTag(10000) != null) {
			if (mBaseLayer.getChildByTag(10000).isVisible()) {
				return false;	
			} else {
				mBaseLayer.getChildByTag(10000).setVisible(true);
				float menuWidth = ((Rectangle) mBaseLayer.getChildByTag(10000)).getWidth();
				float menuHeight = ((Rectangle) mBaseLayer.getChildByTag(10000)).getHeight();
				mBaseLayer.getChildByTag(10000).setPosition(x - menuWidth / 2, y - menuHeight / 2);
				return true;
			}
		}
		
		Rectangle battleMenuLayer = new Rectangle(getBaseScene().getWindowWidth()/ 2, getBaseScene().getWindowHeight() / 2, 
				getBaseScene().getWindowWidth() / 4, 
				getBaseScene().getWindowHeight() / 2, 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		battleMenuLayer.setColor(Color.TRANSPARENT);
		
		List<String> menuList = new ArrayList<String>();
		menuList.add("攻撃");
		menuList.add("防御");
		menuList.add("道具");
		menuList.add("特技");
		
		Font menuFont = getBaseScene().createFont(Typeface.DEFAULT_BOLD, 20, Color.WHITE);
		List<TextButton> textButtonList = new ArrayList<TextButton>();
		float sizeX = 0;
		float sizeY = 0;
		for (String menuStr : menuList) {
			Text text = new Text(0, 0, menuFont, "********", 
					getBaseScene().getBaseActivity().getVertexBufferObjectManager());
			text.setText(menuStr);
			if (sizeX == 0 && sizeY == 0) {
				sizeX = text.getWidth();
				sizeY = text.getHeight();
			} else {
				text.setSize(sizeX, sizeY);
			}
			TextButton textButton = new TextButton(text, 0, 0, 80, 30, 
					getBaseScene().getBaseActivity().getVertexBufferObjectManager(), 
					onClickListener);
			textButtonList.add(textButton);
		}
		float startX = 0;
		float startY = 0;
		float addY = 0;
		float addX = 0;
		int index = 0;
		for (TextButton textButton : textButtonList) {
			getBaseScene().registerTouchArea(textButton);
			battleMenuLayer.attachChild(textButton);
			if (index == 0) {
				textButton.setX(startX);
				textButton.setY(startY);
				addX = textButton.getWidth();
				addY = textButton.getHeight();
			} else if (index == 1) {
				textButton.setX(startX + addX);
				textButton.setY(startY);
			} else if (index == 2) {
				textButton.setX(startX);
				textButton.setY(startY + addY);
			} else if (index == 3) {
				textButton.setX(startX + addX);
				textButton.setY(startY + addY);
			}
			index++;
		}
		battleMenuLayer.setSize(addX * 2, addY * 2);
		battleMenuLayer.setTag(10000); // TODO: とりあえず
		battleMenuLayer.setPosition(x - battleMenuLayer.getWidth() / 2, y - battleMenuLayer.getHeight() / 2);
		mBaseLayer.attachChild(battleMenuLayer);
		
		return true;
	}
	
	private TextButton.OnClickListener onClickListener = new TextButton.OnClickListener() {
		
		@Override
		public void onClick(TextButton pTextButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			// TODO Auto-generated method stub
			Log.d("showBattleMenuLayer", "onClick");
		}
	};

	@Override
	public void end() {
		if (mBaseLayer != null) {
			mBaseLayer.detachChildren();
			mBaseLayer.detachSelf();
			mBaseLayer = null;
		}
	}

}
