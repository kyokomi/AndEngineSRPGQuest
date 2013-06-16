package com.kyokomi.srpgquest.utils;

import org.andengine.entity.sprite.AnimatedSprite;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.constant.MoveDirectionType;

/**
 * アクター
 * @author kyokomi
 *
 */
public class ActorSpriteUtil {
	
	private ActorSpriteUtil() {
		
	}

	// ----------------------------------------------
	// 汎用ポジション設定
	// ----------------------------------------------
	public static void setPlayerDirection(AnimatedSprite pSprite, MoveDirectionType moveDirectionType) {
		pSprite.setCurrentTileIndex(moveDirectionType.getDirection());
		setPlayerToDefaultPosition(pSprite, moveDirectionType.getDirection());
	}
	/**
	 * プレイヤーデフォルトポジション設定.
	 */
	public static void setPlayerToDefaultPosition(AnimatedSprite pSprite, int lastMoveCurrentIndex) {
		if (pSprite != null) {
			pSprite.animate(
					new long[]{100, 100, 100}, 
					new int[]{lastMoveCurrentIndex, lastMoveCurrentIndex+1, lastMoveCurrentIndex+2}, 
					true);
		}
	}
	
	// ----------------------------------------------
	// staticメソッド
	// ----------------------------------------------
	public static String getFaceFileName(int pImageResId) {
		return getBaseFileName(pImageResId) + "_f.png";
	}
	public static String getMoveFileName(int pImageResId) {
		return getBaseFileName(pImageResId) + "_5_s.png";
	}
	private static String getBaseFileName(int pImageResId) {
		return "actor/actor" + pImageResId;
	}
	public static AnimatedSprite getMoveAnimatedSprite(KeyListenScene pBaseScene, int pImageResId) {
		return pBaseScene.getResourceAnimatedSprite(getMoveFileName(pImageResId), 3, 4);
	}
}