package com.kyokomi.srpgquest.layer;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.constant.LayerZIndexType;

public class ScenarioStartCutInTouchLayer extends ACutInTouchLayer {
	public static final int TAG = 1000000;// TODO: TAG管理しないと・・・
	
	public ScenarioStartCutInTouchLayer(KeyListenScene pBaseScene) {
		super(pBaseScene);
	}

	public void initLayer(KeyListenScene pBaseScene, SaveDataDto saveDataDto) {
		// レイヤー作成
		setColor(Color.BLACK);
		setAlpha(0.7f);
		setVisible(false);
		setZIndex(LayerZIndexType.CUTIN_LAYER.getValue());
		
		// フォント
		Font defaultFont = pBaseScene.createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font titleFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.WHITE);
		// 表示テキスト TODO: 文字ベタ書き
		Text winConditionTitle = new Text(16, 16, titleFont, 
				"- 第" + saveDataDto.getScenarioNo() + "章 -", 
				new TextOptions(HorizontalAlign.CENTER), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenter(winConditionTitle);
		winConditionTitle.setY(winConditionTitle.getY() - 40);
		attachChild(winConditionTitle);
				
		Text gameOverConditionDetial = new Text(16, 16, defaultFont, 
				saveDataDto.getScenarioTitle(),
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionDetial, winConditionTitle.getY() + winConditionTitle.getHeight() + 20);
		attachChild(gameOverConditionDetial);
		
		setTag(ScenarioStartCutInTouchLayer.TAG);
	}
}
