package com.kyokomi.srpgquest.utils;

import android.graphics.Point;
import android.graphics.PointF;

public class MapGridUtil {

	public static final int GRID_X = 128;
	public static final int GRID_Y = 64;
	public static final int BASE_Y = 7;
	
	public static PointF indexToDisp(Point pMapIndex) {
		return indexToDisp(pMapIndex.x, pMapIndex.y);
	}
	public static PointF indexToDisp(int x, int y) {
		PointF dispPointF = new PointF();
		dispPointF.set(
				(y + x) * (GRID_X / 2), 
				((BASE_Y - 1) - x + y) * (GRID_Y / 2));
		return dispPointF;
	}
	
	public static Point dispToIndex(PointF pDispPointF) {
		return dispToIndex(pDispPointF.x, pDispPointF.y);
	}
	public static Point dispToIndex(float x, float y) {
		int view_y = (int)((y - GRID_Y * BASE_Y / 2) * 2);
		return new Point(
				(int)((x - view_y + GRID_X * 10) / GRID_X - 10),
				(int)((x + view_y + GRID_X * 10) / GRID_X - 10));
	}
}
