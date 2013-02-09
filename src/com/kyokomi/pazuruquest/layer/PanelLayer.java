package com.kyokomi.pazuruquest.layer;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.Point;

public class PanelLayer extends Rectangle {

	private Sprite image;
	private String name;
	private Point panelPoint;
	
	public PanelLayer(int pPanelPointX, int pPanelPointY, float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		this.panelPoint = new Point(pPanelPointX, pPanelPointY);
	}

	public Sprite getImage() {
		return image;
	}
	public void setImage(Sprite image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Point getPanelPoint() {
		return panelPoint;
	}
	
	public void setPanelPoint(Point panelPoint) {
		this.panelPoint = panelPoint;
	}
}
