package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.entity.MItemEntity;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.logic.MapBattleRewardLogic;
import com.kyokomi.core.manager.MediaManager.MusicType;
import com.kyokomi.core.manager.MediaManager.SoundType;
import com.kyokomi.core.sprite.CommonWindowRectangle;

public class ResultScene extends SrpgBaseScene {
	private static final String TAG = "ResultScene";
	
	private MScenarioEntity mScenarioEntity;
	
	public ResultScene(MultiSceneActivity baseActivity, MScenarioEntity pScenarioEntity) {
		super(baseActivity);
		this.mScenarioEntity = pScenarioEntity;
		init();
		
//		getMediaManager().play(MusicType.CLEAR_BGM);
	}

	@Override
	public MScenarioEntity getScenarioEntity() {
		return mScenarioEntity;
	}

	@Override
	public void initSoundAndMusic() {
		// 効果音をロード
//		try {
//			getMediaManager().resetAllMedia();
//			getMediaManager().createMedia(SoundType.BTN_PRESSED_SE);
//			getMediaManager().createMedia(MusicType.CLEAR_BGM);
//		} catch (IOException e) {
//			Log.e(TAG, "sound file io error");
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void onResume() {
//		getMediaManager().playPauseingMusic();
	}

	@Override
	public void onPause() {
//		getMediaManager().pausePlayingMusic();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
			return true;
		}
		return false;
	}
	// ------------------------------------------------------
	@Override
	public void init() {
		// フォントの作成
		Font defaultFont = createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font largeFont = createFont(Typeface.SANS_SERIF, 36, Color.WHITE);
		
//		Font blueLargeFont = createFont(Typeface.SANS_SERIF, 36, Color.BLUE);
//		Font greenLargeFont = createFont(Typeface.SANS_SERIF, 36, Color.GREEN);
//		Font redLargeFont = createFont(Typeface.SANS_SERIF, 36, Color.RED);

		MapBattleRewardLogic mapBattleRewardLogic = new MapBattleRewardLogic();
		MapBattleRewardDto mapBattleRewardDto = mapBattleRewardLogic.createMapBattleRewardDto(
				this, mScenarioEntity.getSceneId());

		// 背景
		Sprite backImage = getResourceSprite("bk/back_mori2.jpg");
		backImage.setSize(getWindowWidth(), getWindowHeight());
		attachChild(backImage);

		// 共通ウィンドウを作成
		CommonWindowRectangle comonWindowRectangle = new CommonWindowRectangle(
				getWindowWidth() / 4, 5,
				getWindowWidth() / 2, getWindowHeight() / 2 + getWindowHeight() / 4,
				this);
		attachChild(comonWindowRectangle);
		
		// ---------------------------------------------------------------
		// 獲得物表示
		// ---------------------------------------------------------------
		Text titleText = createWithAttachText(largeFont, "- Result -");
		placeToCenterX(titleText, 20);
		
		float titleBaseX = (getWindowWidth() / 2) - (getWindowWidth() / 6);
		Text expTitleText = createWithAttachText(defaultFont, "獲得経験値:");
		expTitleText.setPosition(titleBaseX, titleText.getY() + titleText.getHeight() + 20);
		
		Text expText = createWithAttachText(defaultFont, mapBattleRewardDto.getTotalExp() + " Exp");
		expText.setPosition(getWindowWidth() / 2, titleText.getY() + titleText.getHeight() + 20);

		Text goldTitleText = createWithAttachText(defaultFont, "獲得ゴールド:");
		goldTitleText.setPosition(titleBaseX, expText.getY() + expText.getHeight() + 20);
		
		Text goldText = createWithAttachText(defaultFont, mapBattleRewardDto.getTotalGold() + " Gold");
		goldText.setPosition(getWindowWidth() / 2, expText.getY() + expText.getHeight() + 20);
		
		Text itemTitleText = createWithAttachText(defaultFont, "獲得アイテム:");
		itemTitleText.setPosition(titleBaseX, goldText.getY() + goldText.getHeight() + 20);
		if (mapBattleRewardDto.getItemList().isEmpty()) {
			Text notGetItemText = createWithAttachText(defaultFont, "なし");
			placeToCenterX(notGetItemText, itemTitleText.getY() + itemTitleText.getHeight() + 20);
		} else {
			Rectangle itemIconRectangle = new Rectangle(0, 
					itemTitleText.getY() + itemTitleText.getHeight() + 20, 
					getWindowWidth() / 4, getWindowHeight() / 4, 
					getBaseActivity().getVertexBufferObjectManager());
			itemIconRectangle.setColor(Color.TRANSPARENT);
			attachChild(itemIconRectangle);
			
			float baseX = 0;
			float baseY = 0;
			for (MItemEntity itemEntity : mapBattleRewardDto.getItemList()) {
				// アイコン
				TiledSprite itemIconTiled = getIconSetTiledSprite();
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
			placeToCenterX(itemIconRectangle, itemIconRectangle.getY());
		}
		
		// ---------------------------------------------------------------
		// レベルアップ選択
		// ---------------------------------------------------------------
//		final Line line = new Line(20, getWindowHeight() / 2, getWindowWidth() - 20, getWindowHeight() / 2,
//				getBaseActivity().getVertexBufferObjectManager());
//		line.setLineWidth(1);
//		line.setColor(Color.WHITE);
//		attachChild(line);
//		
//		// プレイヤーパーティーを表示
//		ActorPlayerLogic actorPlayerLogic  = new ActorPlayerLogic();
//		// TODO: とりあえず１キャラなので...
//		ActorPlayerDto actorPlayerDto = actorPlayerLogic.createActorPlayerDto(this, 1);
//		PlayerStatusRectangle statusRect = new PlayerStatusRectangle(
//				this, getFont(), actorPlayerDto, 
//				ActorSprite.getFaceFileName(actorPlayerDto.getImageResId()), 0, 0);
//		statusRect.setPosition(20, getWindowHeight() / 2 + 20);
//		statusRect.show(PlayerStatusRectangleType.LVUP_STATUS);
//		attachChild(statusRect);
//		
//		Text levelUpText = new Text(0, 0, getFont(), "LEVEL UP", 
//				getBaseActivity().getVertexBufferObjectManager());
//		// レベルアップボタン表示
//		TextButton textButton = new TextButton(levelUpText, 0, 0, 8, 8, 
//				getBaseActivity().getVertexBufferObjectManager(), 
//				new TextButton.OnClickListener() {
//			@Override
//			public void onClick(TextButton pTextButtonSprite, float pTouchAreaLocalX,
//					float pTouchAreaLocalY) {
//				// 経験値減らす
//				
//				// レベルアップ呼び出し(DB更新）
//				
//				// プレイヤーウィンドウを再描画
//				
//			}
//		});
//		textButton.setPosition(
//				statusRect.getX() + statusRect.getWidth() / 2 + textButton.getWidth() / 4, 
//				statusRect.getY() + statusRect.getHeight() / 2 + textButton.getHeight() / 2);
//		registerTouchArea(textButton);
//		attachChild(textButton);
		
		// 次へボタン生成
		ButtonSprite nextSceneButtonSprite = getResourceButtonSprite("btn/next_btn.png", "btn/next_btn_p.png");
		placeToCenterX(nextSceneButtonSprite, getWindowHeight() - nextSceneButtonSprite.getHeight() - 40);
		registerTouchArea(nextSceneButtonSprite);
		nextSceneButtonSprite.setOnClickListener(new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				// 次のシナリオへ
				nextScenario(getScenarioEntity());
			}
		});
		attachChild(nextSceneButtonSprite);
	}

	private Text createWithAttachText(Font font, String textStr) {
		Text text = createText(font, textStr);
		attachChild(text);
		return text;
	}
	private Text createText(Font font, String textStr) {
		Text text = new Text(16, 16, font, textStr, 
				new TextOptions(HorizontalAlign.CENTER), 
				getBaseActivity().getVertexBufferObjectManager());
		return text;
	}

	@Override
	public void destory() {
		
	}
}
