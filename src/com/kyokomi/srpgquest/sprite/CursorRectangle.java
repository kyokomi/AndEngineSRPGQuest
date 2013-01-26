package com.kyokomi.srpgquest.sprite;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class CursorRectangle extends Rectangle {

	private final int mMapPointX;
	private final int mMapPointY;
	
	public CursorRectangle(final int pMapPointX, final int pMapPointY, float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mMapPointX = pMapPointX;
		mMapPointY = pMapPointY;
	}

	/**
	 * @return the mMapPointX
	 */
	public int getmMapPointX() {
		return mMapPointX;
	}

	/**
	 * @return the mMapPointY
	 */
	public int getmMapPointY() {
		return mMapPointY;
	}
}
