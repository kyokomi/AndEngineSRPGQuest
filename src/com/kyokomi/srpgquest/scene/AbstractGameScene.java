package com.kyokomi.srpgquest.scene;

import org.andengine.entity.sprite.TiledSprite;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;

public abstract class AbstractGameScene extends KeyListenScene {

	public AbstractGameScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
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
