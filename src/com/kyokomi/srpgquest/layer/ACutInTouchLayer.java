package com.kyokomi.srpgquest.layer;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.primitive.vbo.IRectangleVertexBufferObject;
import org.andengine.entity.shape.IAreaShape;

import com.kyokomi.core.scene.KeyListenScene;

/**
 * タッチしたら消えるカットインの基底クラス.
 * @author kyokomi
 *
 */
public abstract class ACutInTouchLayer extends Rectangle {
	
	public ACutInTouchLayer(KeyListenScene pBaseScene) {
		super(0, 0, pBaseScene.getWindowWidth(), pBaseScene.getWindowHeight(), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		
	}
	public ACutInTouchLayer(float pX, float pY, float pWidth, float pHeight,
			IRectangleVertexBufferObject pRectangleVertexBufferObject) {
		super(pX, pY, pWidth, pHeight, pRectangleVertexBufferObject);
	}
	
	public void showTouchLayer() {
		setVisibleTouchLayer(true);
	}
	public void hideTouchLayer() {
		setVisibleTouchLayer(false);
	}
	public void setVisibleTouchLayer(boolean isVisible) {
		IAreaShape areaShape = getTouchLayer();
		if (areaShape != null) {
			areaShape.setVisible(isVisible);
		}
	}
	
	public IAreaShape getTouchLayer() {
		return this;
	}
	
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
