package com.kyokomi.srpgquest.scene;

import org.andengine.entity.sprite.TiledSprite;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;

public abstract class SrpgBaseScene extends KeyListenScene {

	public SrpgBaseScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
	}
	
	/**
	 * IconSetからSpriteを取得.
	 * @return TiledSprite
	 */
	public TiledSprite getIconSetTiledSprite() {
		return getBaseActivity().getResourceUtil().getTiledSprite("icon_set.png", 16, 48);
	}
}
