package com.kyokomi.srpgquest.logic;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class TextLogic {

	public Rectangle createTextRectangle(String titleStr, Font titleFont, String detatilStr, Font detailFont,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		
		Text titleText = new Text(0, 0, titleFont, titleStr, 
				pVertexBufferObjectManager);
		Text detatilText = new Text(0, 0, detailFont, detatilStr, 
				pVertexBufferObjectManager);
		titleText.setPosition(0, 0);
		detatilText.setPosition(titleText.getX() + titleText.getWidth(), titleText.getY());
		
		float textWidth = titleText.getWidth() + detatilText.getWidth();
		float textHeight = titleText.getHeight();
		Rectangle resultRectangle = new Rectangle(0, 0, textWidth, textHeight, 
				pVertexBufferObjectManager);
		
		resultRectangle.setColor(Color.TRANSPARENT);
		resultRectangle.setAlpha(0.0f);
		resultRectangle.attachChild(titleText);
		resultRectangle.attachChild(detatilText);
		return resultRectangle;
	}
}
