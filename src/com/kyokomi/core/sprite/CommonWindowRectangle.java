package com.kyokomi.core.sprite;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.util.color.Color;

import com.kyokomi.core.scene.KeyListenScene;

public class CommonWindowRectangle extends Rectangle {
 
	public CommonWindowRectangle(float pX, float pY, float pWidth, 
			float pHeight, KeyListenScene pBaseScene) {
		super(pX, pY, pWidth, pHeight, pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		init(pBaseScene, Color.BLACK, 0.5f);
	}
	
	public CommonWindowRectangle(float pX, float pY, float pWidth, 
			float pHeight, Color pColor, float pAlpha, KeyListenScene pBaseScene) {
		super(pX, pY, pWidth, pHeight, pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		init(pBaseScene, pColor, pAlpha);
	}
	
	private void init(KeyListenScene pBaseScene, Color pColor, float pAlpha) {
		
		setColor(pColor);
		setAlpha(pAlpha);
		
		TiledSprite board1 = pBaseScene.getResourceTiledSprite("window_c_4_4.png", 4, 4);
		board1.setCurrentTileIndex(2);
		board1.setPosition(0, 0);
		attachChild(board1);
		TiledSprite board2 = pBaseScene.getResourceTiledSprite("window_c_4_4.png", 4, 4);
		board2.setCurrentTileIndex(3);
		board2.setPosition(getWidth() - board2.getWidth(), 0);
		attachChild(board2);
		TiledSprite board3 = pBaseScene.getResourceTiledSprite("window_c_4_4.png", 4, 4);
		board3.setCurrentTileIndex(6);
		board3.setPosition(0, getHeight() - board3.getHeight());
		attachChild(board3);
		TiledSprite board4 = pBaseScene.getResourceTiledSprite("window_c_4_4.png", 4, 4);
		board4.setCurrentTileIndex(7);
		board4.setPosition(
				getWidth() - board4.getWidth(), 
				getHeight() - board4.getHeight());
		attachChild(board4);
		
		TiledSprite board5 = pBaseScene.getResourceTiledSprite("window_c_6_4.png", 6, 4);
		board5.setCurrentTileIndex(4);
		board5.setPosition(board1.getX() + board1.getWidth(), board1.getY());
		board5.setWidth(getWidth() - board1.getWidth() - board2.getWidth());
		attachChild(board5);
		
		TiledSprite board6 = pBaseScene.getResourceTiledSprite("window_c_4_6.png", 4, 6);
		board6.setCurrentTileIndex(6);
		board6.setPosition(board1.getX(), board1.getY() + board1.getHeight());
		board6.setHeight(getHeight() - board1.getHeight() - board3.getHeight());
		attachChild(board6);
		
		TiledSprite board7 = pBaseScene.getResourceTiledSprite("window_c_4_6.png", 4, 6);
		board7.setCurrentTileIndex(7);
		board7.setPosition(board2.getX(), board2.getY() + board2.getHeight());
		board7.setHeight(getHeight() - board2.getHeight() - board4.getHeight());
		attachChild(board7);
		
		TiledSprite board8 = pBaseScene.getResourceTiledSprite("window_c_6_4.png", 6, 4);
		board8.setCurrentTileIndex(10);
		board8.setPosition(board3.getX() + board3.getWidth(), board3.getY());
		board8.setWidth(getWidth() - board3.getWidth() - board4.getWidth());
		attachChild(board8);
	}
}
