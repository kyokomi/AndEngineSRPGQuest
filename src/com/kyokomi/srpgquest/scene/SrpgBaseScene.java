package com.kyokomi.srpgquest.scene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;

public abstract class SrpgBaseScene extends KeyListenScene {
	
	/**
	 * 共通タッチイベント用
	 */
	private Sprite mTouchSprite;
	public void touchSprite(float x, float y) {
		if (mTouchSprite == null) {
			mTouchSprite = getResourceSprite("touch.png");
			mTouchSprite.setVisible(false);
			mTouchSprite.setZIndex(999);
			attachChild(mTouchSprite);
		}
		mTouchSprite.setPosition(x - mTouchSprite.getWidth() / 2, y - mTouchSprite.getHeight() / 2);
		mTouchSprite.registerEntityModifier(new ParallelEntityModifier(
				new ScaleModifier(0.2f, 1.0f, 1.5f, new IEntityModifier.IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						mTouchSprite.setVisible(true);
					}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					}
				}),
				new AlphaModifier(0.2f, 1.0f, 0.0f, new IEntityModifier.IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						mTouchSprite.setVisible(false);
					}
				})
			));
	}
	
//	// ----- SE, BGM -----
//	private Sound mBtnPressedSound;
//	public Sound getBtnPressedSound() {
//		return mBtnPressedSound;
//	}
	
	/** サウンドの準備. */
	@Override
	public void prepareSoundAndMusic() {
//		try {
//			mBtnPressedSound = createSoundFromFileName("btn_se1.wav");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		initSoundAndMusic();
	}
	public abstract void initSoundAndMusic();
	public abstract void onResume();
	public abstract void onPause();
	
	public SrpgBaseScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
	}
	
	/**
	 * 次シナリオへ
	 */
	public void nextScenario() {
		// FPS表示以外を開放
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildByIndex(i).getTag() == FPS_TAG) {
				continue;
			}
			// ボタンはタッチの検知も無効にする
			if (getChildByIndex(i) instanceof ButtonSprite) {
				unregisterTouchArea((ButtonSprite) getChildByIndex(i));
			}
			detachEntity(getChildByIndex(i));
		}
		System.gc();
		
		// セーブAnd次シナリオへ進行
		getBaseActivity().getGameController().nextScenarioAndSave(this);
		init();
	}
	
	public float getDispStartX() {
		return 0;
	}
	
	public float getDispStartY() {
		return 0;
	}
}
