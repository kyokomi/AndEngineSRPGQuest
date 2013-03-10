package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.manager.MediaManager.MusicType;
import com.kyokomi.core.manager.MediaManager.SoundType;

public class ResultScene extends SrpgBaseScene {
	private static final String TAG = "ResultScene";
	
	private final MScenarioEntity mScenarioEntity;
	
	public ResultScene(MultiSceneActivity baseActivity, MScenarioEntity pScenarioEntity) {
		super(baseActivity);
		this.mScenarioEntity = pScenarioEntity;
		init();
		
		getMediaManager().play(MusicType.CLEAR_BGM);
	}

	@Override
	public MScenarioEntity getScenarioEntity() {
		return mScenarioEntity;
	}

	@Override
	public void initSoundAndMusic() {
		// 効果音をロード
		try {
			getMediaManager().resetAllMedia();
			getMediaManager().createMedia(SoundType.BTN_PRESSED_SE);
			getMediaManager().createMedia(MusicType.CLEAR_BGM);
		} catch (IOException e) {
			Log.e(TAG, "sound file io error");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		getMediaManager().playPauseingMusic();
	}

	@Override
	public void onPause() {
		getMediaManager().pausePlayingMusic();
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

		Text titleText = createWithAttachText(largeFont, "- Result -");
		placeToCenterX(titleText, 20);
		
		Text expText = createWithAttachText(defaultFont, "獲得経験値:" + 10000);
		placeToCenterX(expText, titleText.getY() + titleText.getHeight() + 20);

		Text goldText = createWithAttachText(defaultFont, "獲得ゴールド:" + 10000);
		placeToCenterX(goldText, expText.getY() + expText.getHeight() + 20);
		
		Text itemTitleText = createWithAttachText(defaultFont, "獲得アイテム:");
		placeToCenterX(itemTitleText, goldText.getY() + goldText.getHeight() + 20);
		Text notGetItemText = createWithAttachText(defaultFont, "なし");
		placeToCenterX(notGetItemText, itemTitleText.getY() + itemTitleText.getHeight() + 20);
		
		ButtonSprite nextSceneButtonSprite = getResourceButtonSprite("btn/next_btn.png", "btn/next_btn_p.png");
		placeToCenterX(nextSceneButtonSprite, getWindowHeight() - nextSceneButtonSprite.getHeight() - 20);
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
		Text text = new Text(16, 16, font, textStr, 
				new TextOptions(HorizontalAlign.CENTER), 
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(text);
		return text;
	}
}
