package com.kyokomi.pazuruquest.layer;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.Point;
import android.graphics.PointF;

public class PanelLayer extends Rectangle {

//	private PointF moveingBeforePointF;
	private Point panelPoint;
	
	public PanelLayer(int pPanelPointX, int pPanelPointY, float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		this.panelPoint = new Point(pPanelPointX, pPanelPointY);
	}

	public Point getPanelPoint() {
		return panelPoint;
	}
	
	public void setPanelPoint(Point panelPoint) {
		this.panelPoint = panelPoint;
	}
//	public void setMoveingBeforePointF(PointF moveingBeforePointF) {
//		this.moveingBeforePointF = moveingBeforePointF;
//	}
//	public PointF getMoveingBeforePointF() {
//		if(moveingBeforePointF == null) {
//			PointF pointF = new PointF(getX(), getY());
//			moveingBeforePointF = pointF;
//		}
//		return moveingBeforePointF;
//	}
//	
//	public void setMoveingAfterPointF(PointF moveingAfterPointF) {
//		this.moveingAfterPointF = moveingAfterPointF;
//	}
//	public PointF getMoveingAfterPointF() {
////		if(moveingAfterPointF == null) {
//			PointF pointF = new PointF(getX(), getY());
//			moveingAfterPointF = pointF;
////		}
//		return moveingAfterPointF;
//	}
}
