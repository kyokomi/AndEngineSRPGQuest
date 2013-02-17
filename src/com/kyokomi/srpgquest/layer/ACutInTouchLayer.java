package com.kyokomi.srpgquest.layer;

import org.andengine.entity.shape.IAreaShape;

import com.kyokomi.srpgquest.scene.SrpgBaseScene;

public abstract class ACutInTouchLayer {
	
	public ACutInTouchLayer(SrpgBaseScene pBaseScene) {
		initLayer(pBaseScene);
	}
	protected abstract void initLayer(SrpgBaseScene pBaseScene);
	
	public void showTouchLayer(SrpgBaseScene pBaseScene) {
		pBaseScene.attachChild(getTouchLayer());
		setVisibleTouchLayer(true);
	}
	public void hideTouchLayer(SrpgBaseScene pBaseScene) {
		pBaseScene.detachChild(getTouchLayer());
		setVisibleTouchLayer(false);
	}
	public void setVisibleTouchLayer(boolean isVisible) {
		IAreaShape areaShape = getTouchLayer();
		if (areaShape != null) {
			areaShape.setVisible(isVisible);
		}
	}
	
	public abstract IAreaShape getTouchLayer();
	
	public boolean isTouchLayer(float x, float y) {
		IAreaShape areaShape = getTouchLayer();
		if (areaShape != null) {
			if (areaShape.isVisible() && areaShape.contains(x, y)) {
				return true;
			}
		}
		return false;
	}
}
