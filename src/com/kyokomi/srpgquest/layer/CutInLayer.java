package com.kyokomi.srpgquest.layer;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.constant.MapBattleCutInLayerType;

/**
 * 自動的に消えるカットイン.
 * @author kyokomi
 *
 */
public class CutInLayer extends Rectangle {
	
	private final MapBattleCutInLayerType mapBattleCutInLayerType;
	
	/**
	 * カットインのコールバック.
	 * @author kyokomi
	 *
	 */
	public interface ICutInCallback {
		public void doAction();
	}

	/**
	 * コンストラクタ
	 * @param pBaseScene
	 */
	public CutInLayer(float pX, float pY, float pWidth, float pHeight, KeyListenScene pBaseScene, MapBattleCutInLayerType mapBattleCutInLayerType) {
		super(pX, pY, pWidth, pHeight, pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		this.mapBattleCutInLayerType = mapBattleCutInLayerType;
		setTag(mapBattleCutInLayerType.getValue());
		setAlpha(0.0f);
		setColor(Color.TRANSPARENT);
		setVisible(false);
		setZIndex(LayerZIndexType.CUTIN_LAYER.getValue());
		attachWithCreateMapBattleCutIn(pBaseScene, mapBattleCutInLayerType);
	}
	
	public MapBattleCutInLayerType getMapBattleCutInLayerType() {
		return mapBattleCutInLayerType;
	}
	
	private final static int SPRITE_TAG = 1;

	private void attachWithCreateMapBattleCutIn(KeyListenScene pBaseScene, MapBattleCutInLayerType mapBattleCutInLayerType) {
		Sprite sprite = pBaseScene.getResourceSprite(getMapBattleCutInLayerType().getFileName());
		if (mapBattleCutInLayerType.isWindowSize()) {
			sprite.setSize(pBaseScene.getWindowWidth(), pBaseScene.getWindowHeight());
		}
		pBaseScene.placeToCenter(sprite);
		sprite.setTag(SPRITE_TAG);
		attachChild(sprite);
	}
	
	public void showCutInSprite(final float pDuration, final ICutInCallback cutInCallback) {
		final Sprite sprite = (Sprite) getChildByTag(SPRITE_TAG);
		sprite.registerEntityModifier(new FadeInModifier(pDuration, new IEntityModifier.IEntityModifierListener() {
			@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				setVisible(true);
			}
			@Override public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				cutInCallback.doAction(); // コールバック呼び出し
				sprite.setAlpha(0.0f);
				setVisible(false);
			}
		}));
	}
}
