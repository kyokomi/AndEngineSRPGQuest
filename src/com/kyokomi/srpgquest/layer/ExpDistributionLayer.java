package com.kyokomi.srpgquest.layer;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.logic.ActorPlayerLogic;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.CommonWindowRectangle;
import com.kyokomi.srpgquest.logic.TextLogic;
import com.kyokomi.srpgquest.sprite.ActorSprite;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle.PlayerStatusRectangleType;

public class ExpDistributionLayer extends Rectangle {

	public ExpDistributionLayer(float pX, float pY, float pWidth,
			float pHeight, KeyListenScene pBaseScene) {
		
		super(pX, pY, pWidth, pHeight, pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		
		init(pBaseScene);
	}
	
	private void init(KeyListenScene pBaseScene) {
		// 共通ウィンドウを作成
		CommonWindowRectangle comonWindowRectangle = new CommonWindowRectangle(
				0,
				0,
				pBaseScene.getWindowWidth(), 
				pBaseScene.getWindowHeight(),
				Color.BLACK, 0.8f,
				pBaseScene);
		attachChild(comonWindowRectangle);
		// タイトル
		float titleY = 12;
		Font titleFont = pBaseScene.createFont(Typeface.DEFAULT_BOLD, 30, Color.WHITE);
		Text titleText = new Text(10, 10, titleFont, "〜 経験値振り分け 〜", 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		pBaseScene.placeToCenterX(titleText, titleY);
		attachChild(titleText);
		
		float titleLineY = titleText.getY() + titleText.getHeight() + 4;
		
		Line line = new Line(10, titleLineY, pBaseScene.getWindowWidth() - 16, titleLineY, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		line.setLineWidth(1);
		line.setColor(Color.WHITE);
		line.setAlpha(1.0f);
		attachChild(line);
		
		TextLogic textLogic = new TextLogic();
		Font defaultFont = pBaseScene.createFont(Typeface.SANS_SERIF, 16, Color.WHITE);
		Font paramFont = pBaseScene.createFont(Typeface.DEFAULT, 16, Color.YELLOW);
		Font upParamFont = pBaseScene.createFont(Typeface.DEFAULT, 16, Color.BLUE);
		// Expの表示
		float expY = titleLineY + 4;
		Rectangle totalExpTextRect = textLogic.createTextRectangle("所持経験値:", defaultFont, "100 Exp", paramFont, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChild(totalExpTextRect);
		
		Rectangle afterExpTextRect = textLogic.createTextRectangle("振り分け後経験値:", defaultFont, "0 Exp", paramFont, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChild(afterExpTextRect);
		
		totalExpTextRect.setPosition(pBaseScene.getWindowWidth() / 2 -
				(totalExpTextRect.getX() + totalExpTextRect.getWidth() + 50 + afterExpTextRect.getWidth()) / 2, expY);

		afterExpTextRect.setPosition(
				totalExpTextRect.getX() + totalExpTextRect.getWidth() + 50,
				totalExpTextRect.getY());
		
		float expLineY = totalExpTextRect.getY() + totalExpTextRect.getHeight() + 4;
		Line expLine = new Line(10, expLineY, pBaseScene.getWindowWidth() - 16, expLineY, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		expLine.setLineWidth(1);
		expLine.setColor(Color.WHITE);
		expLine.setAlpha(1.0f);
		attachChild(expLine);
		
		// プレイヤー情報
		float playerX = 12;
		float playerY = expLineY + 12;
		
		ActorPlayerLogic actorPlayerLogic = new ActorPlayerLogic();
		ActorPlayerDto actorPlayerDto = actorPlayerLogic.createActorPlayerDto(pBaseScene, 1);
		
		PlayerStatusRectangle playerStatusRectangle = new PlayerStatusRectangle(pBaseScene, defaultFont, 
				actorPlayerDto, ActorSprite.getFaceFileName(actorPlayerDto.getImageResId()), playerX, playerY);
		playerStatusRectangle.show(PlayerStatusRectangleType.MINI_STATUS);
		attachChild(playerStatusRectangle);
	}
}
