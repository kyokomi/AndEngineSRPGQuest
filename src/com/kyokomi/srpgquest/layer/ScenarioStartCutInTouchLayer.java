package com.kyokomi.srpgquest.layer;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;
import com.kyokomi.srpgquest.constant.LayerZIndexType;

public class ScenarioStartCutInTouchLayer extends ACutInTouchLayer {
	
	private Rectangle mLayer;
	
	public ScenarioStartCutInTouchLayer(SrpgBaseScene pBaseScene) {
		super(pBaseScene);
	}

	@Override
	protected void initLayer(SrpgBaseScene pBaseScene) {
		// レイヤー作成
		mLayer = new Rectangle(0, 0, pBaseScene.getWindowWidth(), pBaseScene.getWindowHeight(), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mLayer.setColor(Color.BLACK);
		mLayer.setAlpha(0.7f);
		mLayer.setVisible(false);
		mLayer.setZIndex(LayerZIndexType.CUTIN_LAYER.getValue());
		// 現在のシナリオを取得
		MScenarioEntity mScenarioEntity = pBaseScene.getScenarioEntity();
		if (mScenarioEntity == null) {
			return; // シナリオが無いときはレイヤーだけ作って終わり
		}
		// フォント
		Font defaultFont = pBaseScene.createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font titleFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.WHITE);
		// 表示テキスト TODO: 文字ベタ書き
		Text winConditionTitle = new Text(16, 16, titleFont, 
				"- 第" + mScenarioEntity.getScenarioNo() + "章 -", 
				new TextOptions(HorizontalAlign.CENTER), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenter(winConditionTitle);
		winConditionTitle.setY(winConditionTitle.getY() - 40);
		mLayer.attachChild(winConditionTitle);
				
		Text gameOverConditionDetial = new Text(16, 16, defaultFont, 
				mScenarioEntity.getScenarioTitle(),
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionDetial, winConditionTitle.getY() + winConditionTitle.getHeight() + 20);
		mLayer.attachChild(gameOverConditionDetial);
		
		mLayer.setTag(2);
	}

	@Override
	public IAreaShape getTouchLayer() {
		return mLayer;
	}
}
