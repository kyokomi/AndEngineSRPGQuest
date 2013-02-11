package com.kyokomi.srpgquest.layer;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.srpgquest.scene.MapBattleScene.LayerZIndex;

public class MapBattleTouchLayer {
	public enum MapBattleTouchLayerType {
		CLEAR_CONDITION_TOUCH(1),
		;
		private Integer value;
		private MapBattleTouchLayerType(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
		public static MapBattleTouchLayerType get(Integer value) {
			MapBattleTouchLayerType[] values = values();
			for (MapBattleTouchLayerType type : values) {
				if (type.getValue() == value) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type.");
		}
	}
	/** 勝利条件タッチ画面. */
	private Rectangle mClearConditionCutInRect;
	
	public MapBattleTouchLayer(SrpgBaseScene pBaseScene) {
		initClearConditionCutIn(pBaseScene);
	}
	private void initClearConditionCutIn(SrpgBaseScene pBaseScene) {
		Font defaultFont = pBaseScene.createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font blueFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.BLUE);
		Font redFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.RED);
		mClearConditionCutInRect = new Rectangle(0, 0, pBaseScene.getWindowWidth(), pBaseScene.getWindowHeight(), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mClearConditionCutInRect.setColor(Color.BLACK);
		mClearConditionCutInRect.setAlpha(0.7f);
		mClearConditionCutInRect.setVisible(false);
		mClearConditionCutInRect.setZIndex(LayerZIndex.CUTIN_LAYER.getValue());
		pBaseScene.attachChild(mClearConditionCutInRect);
		
		Text winConditionTitle = new Text(16, 16, blueFont, "- 勝利条件 -", 
				new TextOptions(HorizontalAlign.CENTER), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(winConditionTitle, 20);
		mClearConditionCutInRect.attachChild(winConditionTitle);
		
		Text winConditionDetial = new Text(16, 16, defaultFont, "全ての敵を倒せ",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(winConditionDetial, winConditionTitle.getY() + winConditionTitle.getHeight() + 20);
		mClearConditionCutInRect.attachChild(winConditionDetial);
		
		Text gameOverConditionTitle = new Text(16, 16, redFont, "- 敗北条件 -",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionTitle, winConditionDetial.getY() + winConditionDetial.getHeight() + 50);
		mClearConditionCutInRect.attachChild(gameOverConditionTitle);
		
		Text gameOverConditionDetial = new Text(16, 16, defaultFont, "プレイヤーキャラの全滅",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionDetial, gameOverConditionTitle.getY() + gameOverConditionTitle.getHeight() + 20);
		mClearConditionCutInRect.attachChild(gameOverConditionDetial);
		
		mClearConditionCutInRect.setTag(MapBattleTouchLayerType.CLEAR_CONDITION_TOUCH.getValue());
		// タッチイベントを拾わせるためにここでattachしない
	}
	public IAreaShape showTouchLayer(MapBattleTouchLayerType pMapBattleTouchLayerType) {
		return setVisibleTouchLayer(pMapBattleTouchLayerType, true);
	}
	public IAreaShape hideTouchLayer(MapBattleTouchLayerType pMapBattleTouchLayerType) {
		return setVisibleTouchLayer(pMapBattleTouchLayerType, false);
	}
	public IAreaShape setVisibleTouchLayer(MapBattleTouchLayerType pMapBattleTouchLayerType, boolean isVisible) {
		IAreaShape areaShape = getTouchLayer(pMapBattleTouchLayerType);
		if (areaShape != null) {
			areaShape.setVisible(isVisible);
		}
		return areaShape;
	}
	public IAreaShape getTouchLayer(MapBattleTouchLayerType pMapBattleTouchLayerType) {
		switch(pMapBattleTouchLayerType) {
		case CLEAR_CONDITION_TOUCH:
			return mClearConditionCutInRect;
		default:
			return null;
		}
	}
	public boolean isTouchClerCondition(MapBattleTouchLayerType pMapBattleTouchLayerType, float x, float y) {
		IAreaShape areaShape = getTouchLayer(pMapBattleTouchLayerType);
		if (areaShape != null) {
			if (areaShape.isVisible() && areaShape.contains(x, y)) {
				return true;
			}
		}
		return false;
	}
}
