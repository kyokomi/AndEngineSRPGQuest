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

/**
 * TODO: ACutInTouchLayerを継承する
 * @author kyokomi
 *
 */
public class MapBattleClearConditionTouchLayer extends ACutInTouchLayer {
	/** 勝利条件タッチ画面. */
	private Rectangle mTouchLayer;
	
	public MapBattleClearConditionTouchLayer(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}
	
	@Override
	protected void initLayer(SrpgBaseScene pBaseScene) {
		Font defaultFont = pBaseScene.createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font blueFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.BLUE);
		Font redFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.RED);
		mTouchLayer = new Rectangle(0, 0, pBaseScene.getWindowWidth(), pBaseScene.getWindowHeight(), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mTouchLayer.setColor(Color.BLACK);
		mTouchLayer.setAlpha(0.7f);
		mTouchLayer.setVisible(false);
		mTouchLayer.setZIndex(LayerZIndex.CUTIN_LAYER.getValue());
		
		// TODO: 文字ベタ書き
		Text winConditionTitle = new Text(16, 16, blueFont, "- 勝利条件 -", 
				new TextOptions(HorizontalAlign.CENTER), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(winConditionTitle, 20);
		mTouchLayer.attachChild(winConditionTitle);
		
		Text winConditionDetial = new Text(16, 16, defaultFont, "全ての敵を倒せ",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(winConditionDetial, winConditionTitle.getY() + winConditionTitle.getHeight() + 20);
		mTouchLayer.attachChild(winConditionDetial);
		
		Text gameOverConditionTitle = new Text(16, 16, redFont, "- 敗北条件 -",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionTitle, winConditionDetial.getY() + winConditionDetial.getHeight() + 50);
		mTouchLayer.attachChild(gameOverConditionTitle);
		
		Text gameOverConditionDetial = new Text(16, 16, defaultFont, "プレイヤーキャラの全滅",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionDetial, gameOverConditionTitle.getY() + gameOverConditionTitle.getHeight() + 20);
		mTouchLayer.attachChild(gameOverConditionDetial);
		mTouchLayer.setTag(1);
	}
	@Override
	public IAreaShape getTouchLayer() {
		return mTouchLayer;
	}
	
	public void removeAll() {
		mTouchLayer.detachSelf();
	}
}
