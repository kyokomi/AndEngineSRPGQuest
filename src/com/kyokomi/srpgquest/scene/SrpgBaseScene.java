package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.sprite.TiledSprite;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;

public abstract class SrpgBaseScene extends KeyListenScene {
	
	// ----- SE, BGM -----
	private Sound mBtnPressedSound;
	public Sound getBtnPressedSound() {
		return mBtnPressedSound;
	}
	
	/** サウンドの準備. */
	@Override
	public void prepareSoundAndMusic() {
		try {
			mBtnPressedSound = createSoundFromFileName("btn_se1.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
		initSoundAndMusic();
	}
	public abstract void initSoundAndMusic();
	
	public SrpgBaseScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
	}
	
	public String createFaceFileName(int imageResId) {
		String baseFileName = "actor" + imageResId;
		return baseFileName + "_f.png";
	}
	public TiledSprite getResourceFaceSprite(int playerId, int imageResId) {
		return getResourceFaceSprite(playerId, createFaceFileName(imageResId));
	}
	public TiledSprite getResourceFaceSprite(int tag, String faceFileName) {
		TiledSprite tiledSprite = getResourceTiledSprite(faceFileName, 4, 2);
		tiledSprite.setTag(tag);
		return tiledSprite;
	}
	
	/**
	 * IconSetからSpriteを取得.
	 * @return TiledSprite
	 */
	public TiledSprite getIconSetTiledSprite() {
		return getBaseActivity().getResourceUtil().getTiledSprite("icon_set.png", 16, 48);
	}
}
