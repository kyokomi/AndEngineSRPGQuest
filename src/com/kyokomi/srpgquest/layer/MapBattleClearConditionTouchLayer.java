package com.kyokomi.srpgquest.layer;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.constant.LayerZIndexType;

/**
 * @author kyokomi
 *
 */
public class MapBattleClearConditionTouchLayer extends ACutInTouchLayer {
	public static final int TAG = 4000;
	public MapBattleClearConditionTouchLayer(KeyListenScene pBaseScene) {
		super(pBaseScene);
		initLayer(pBaseScene);
	}
	
	private void initLayer(KeyListenScene pBaseScene) {
		Font defaultFont = pBaseScene.createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font blueFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.BLUE);
		Font redFont = pBaseScene.createFont(Typeface.SANS_SERIF, 36, Color.RED);
		setColor(Color.BLACK);
		setAlpha(0.7f);
		setVisible(false);
		setZIndex(LayerZIndexType.CUTIN_LAYER.getValue());
		
		// TODO: 文字ベタ書き
		Text winConditionTitle = new Text(16, 16, blueFont, "- 勝利条件 -", 
				new TextOptions(HorizontalAlign.CENTER), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(winConditionTitle, 20);
		attachChild(winConditionTitle);
		
		Text winConditionDetial = new Text(16, 16, defaultFont, "全ての敵を倒せ",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(winConditionDetial, winConditionTitle.getY() + winConditionTitle.getHeight() + 20);
		attachChild(winConditionDetial);
		
		Text gameOverConditionTitle = new Text(16, 16, redFont, "- 敗北条件 -",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionTitle, winConditionDetial.getY() + winConditionDetial.getHeight() + 50);
		attachChild(gameOverConditionTitle);
		
		Text gameOverConditionDetial = new Text(16, 16, defaultFont, "プレイヤーキャラの全滅",
				new TextOptions(HorizontalAlign.CENTER),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(gameOverConditionDetial, gameOverConditionTitle.getY() + gameOverConditionTitle.getHeight() + 20);
		attachChild(gameOverConditionDetial);
		
		// TODO: あとでTAG管理
		setTag(TAG);
	}
}
