package com.kyokomi.srpgquest.scene.part;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.entity.MItemEntity;
import com.kyokomi.core.logic.MapBattleRewardLogic;
import com.kyokomi.core.sprite.CommonWindowRectangle;
import com.kyokomi.srpgquest.layer.ExpDistributionLayer;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.srpgquest.utils.SRPGSpriteUtil;

public class ResultPart extends AbstractGamePart {

	public ResultPart(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}

	@Override
	public void init(SaveDataDto saveDataDto) {
		// フォントの作成
		Font defaultFont = getBaseScene().createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font largeFont = getBaseScene().createFont(Typeface.SANS_SERIF, 36, Color.WHITE);
		
		MapBattleRewardLogic mapBattleRewardLogic = new MapBattleRewardLogic();
		MapBattleRewardDto mapBattleRewardDto = mapBattleRewardLogic.createMapBattleRewardDto(
				getBaseScene(), saveDataDto.getSceneId());

		// 背景
		Sprite backImage = getBaseScene().getResourceSprite(saveDataDto.getBackImgFilePath());
		backImage.setSize(getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight());
		getBaseScene().attachChild(backImage);

		// 共通ウィンドウを作成
		CommonWindowRectangle comonWindowRectangle = new CommonWindowRectangle(
				getBaseScene().getWindowWidth() / 4, 5,
				getBaseScene().getWindowWidth() / 2, 
				getBaseScene().getWindowHeight() / 2 + getBaseScene().getWindowHeight() / 4,
				getBaseScene());
		getBaseScene().attachChild(comonWindowRectangle);
		
		// ---------------------------------------------------------------
		// 獲得物表示
		// ---------------------------------------------------------------
		Text titleText = createWithAttachText(largeFont, "- Result -");
		getBaseScene().placeToCenterX(titleText, 20);
		
		float titleBaseX = (getBaseScene().getWindowWidth() / 2) - (getBaseScene().getWindowWidth() / 6);
		Text expTitleText = createWithAttachText(defaultFont, "獲得経験値:");
		expTitleText.setPosition(titleBaseX, titleText.getY() + titleText.getHeight() + 20);
		
		Text expText = createWithAttachText(defaultFont, mapBattleRewardDto.getTotalExp() + " Exp");
		expText.setPosition(getBaseScene().getWindowWidth() / 2, titleText.getY() + titleText.getHeight() + 20);

		Text goldTitleText = createWithAttachText(defaultFont, "獲得ゴールド:");
		goldTitleText.setPosition(titleBaseX, expText.getY() + expText.getHeight() + 20);
		
		Text goldText = createWithAttachText(defaultFont, mapBattleRewardDto.getTotalGold() + " Gold");
		goldText.setPosition(getBaseScene().getWindowWidth() / 2, expText.getY() + expText.getHeight() + 20);
		
		Text itemTitleText = createWithAttachText(defaultFont, "獲得アイテム:");
		itemTitleText.setPosition(titleBaseX, goldText.getY() + goldText.getHeight() + 20);
		if (mapBattleRewardDto.getItemList().isEmpty()) {
			Text notGetItemText = createWithAttachText(defaultFont, "なし");
			getBaseScene().placeToCenterX(notGetItemText, itemTitleText.getY() + itemTitleText.getHeight() + 20);
		} else {
			Rectangle itemIconRectangle = new Rectangle(0, 
					itemTitleText.getY() + itemTitleText.getHeight() + 20, 
					getBaseScene().getWindowWidth() / 4, getBaseScene().getWindowHeight() / 4, 
					getBaseScene().getBaseActivity().getVertexBufferObjectManager());
			itemIconRectangle.setColor(Color.TRANSPARENT);
			getBaseScene().attachChild(itemIconRectangle);
			
			float baseX = 0;
			float baseY = 0;
			for (MItemEntity itemEntity : mapBattleRewardDto.getItemList()) {
				// アイコン
				TiledSprite itemIconTiled = SRPGSpriteUtil.getIconSetTiledSprite(getBaseScene());
				itemIconTiled.setCurrentTileIndex(itemEntity.getItemImageId());
				itemIconTiled.setPosition(baseX, baseY);
				itemIconRectangle.attachChild(itemIconTiled);
				baseX += itemIconTiled.getWidth() + 5;
				
				// テキスト
				Text itemText = createText(defaultFont, itemEntity.getItemName());
				itemText.setPosition(baseX, baseY);
				itemIconRectangle.attachChild(itemText);
				baseX += itemText.getWidth() + 5;
				
				baseY += itemIconTiled.getHeight() + 5;
				baseX = 0;
			}
			getBaseScene().placeToCenterX(itemIconRectangle, itemIconRectangle.getY());
		}
		// 経験値振り分けウィンドウ表示
		final ExpDistributionLayer expDistributionLayer = new ExpDistributionLayer(0, 0, 
				getBaseScene().getWindowWidth(), getBaseScene().getWindowHeight(), getBaseScene());
		expDistributionLayer.setVisible(false);
		getBaseScene().attachChild(expDistributionLayer);
		// 次へボタン生成
		final ButtonSprite nextSceneButtonSprite = getBaseScene().getResourceButtonSprite("btn/next_btn.png", "btn/next_btn_p.png");
		getBaseScene().placeToCenterX(nextSceneButtonSprite, getBaseScene().getWindowHeight() - nextSceneButtonSprite.getHeight() - 40);
		getBaseScene().registerTouchArea(nextSceneButtonSprite);
		nextSceneButtonSprite.setVisible(false);
		nextSceneButtonSprite.setOnClickListener(new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				end();					
			}
		});
		getBaseScene().attachChild(nextSceneButtonSprite);
		
		// 次へボタン生成
		ButtonSprite showExpDistributionButton = getBaseScene().getResourceButtonSprite("btn/next_btn.png", "btn/next_btn_p.png");
		getBaseScene().placeToCenterX(showExpDistributionButton, 
				getBaseScene().getWindowHeight() - showExpDistributionButton.getHeight() - 40);
		getBaseScene().registerTouchArea(showExpDistributionButton);
		showExpDistributionButton.setOnClickListener(new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				expDistributionLayer.setVisible(true);
				nextSceneButtonSprite.setVisible(true);
			}
		});
		getBaseScene().attachChild(showExpDistributionButton);
	}

	@Override
	public void touchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void end() {
		// 次のシナリオへ
		getBaseScene().nextScenario();
	}

	private Text createWithAttachText(Font font, String textStr) {
		Text text = createText(font, textStr);
		getBaseScene().attachChild(text);
		return text;
	}
	private Text createText(Font font, String textStr) {
		Text text = new Text(16, 16, font, textStr, 
				new TextOptions(HorizontalAlign.CENTER), 
				getBaseScene().getBaseActivity().getVertexBufferObjectManager());
		return text;
	}
}
