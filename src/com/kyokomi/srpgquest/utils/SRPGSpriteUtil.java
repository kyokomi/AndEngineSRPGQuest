package com.kyokomi.srpgquest.utils;

import org.andengine.entity.sprite.TiledSprite;

import com.kyokomi.core.scene.KeyListenScene;

public class SRPGSpriteUtil {

	public static TiledSprite getResourceFaceSprite(KeyListenScene pBaseScene, int playerId, int imageResId) {
		return getResourceFaceSprite(pBaseScene, playerId, ActorSpriteUtil.getFaceFileName(imageResId));
	}
	public static TiledSprite getResourceFaceSprite(KeyListenScene pBaseScene, int tag, String faceFileName) {
		TiledSprite tiledSprite = pBaseScene.getResourceTiledSprite(faceFileName, 4, 2);
		tiledSprite.setTag(tag);
		return tiledSprite;
	}
	
	/**
	 * IconSetからSpriteを取得.
	 * @return TiledSprite
	 */
	public static TiledSprite getIconSetTiledSprite(KeyListenScene keyListenScene) {
		return keyListenScene.getBaseActivity().getResourceUtil().getTiledSprite("icon_set.png", 16, 48);
	}
}
