package com.kyokomi.srpgquest.layer;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.constant.LayerZIndexType;

public class TextCutInTouchLayer extends ACutInTouchLayer {

	public TextCutInTouchLayer(KeyListenScene pBaseScene, String pText) {
		super(pBaseScene);
	
		initLayer(pBaseScene, pText);
	}
	private void initLayer(KeyListenScene pBaseScene, String pText) {
		// レイヤー作成
		setColor(Color.BLACK);
		setAlpha(0.7f);
		setVisible(false);
		setZIndex(LayerZIndexType.CUTIN_LAYER.getValue());
		
		// フォント
		Font titleFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.WHITE);
		// 表示テキスト
		Text winConditionTitle = new Text(16, 16, titleFont, 
				pText, 
				new TextOptions(HorizontalAlign.CENTER), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenter(winConditionTitle);
		winConditionTitle.setY(winConditionTitle.getY() - 40);
		attachChild(winConditionTitle);
	}
}
