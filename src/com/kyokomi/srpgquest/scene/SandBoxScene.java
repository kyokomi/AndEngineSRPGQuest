package com.kyokomi.srpgquest.scene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import com.kyokomi.core.activity.MultiSceneActivity;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.KeyEvent;

public class SandBoxScene extends SrpgBaseScene 
	implements ButtonSprite.OnClickListener, IOnSceneTouchListener {
	
	private static final int GRID_X = 128;
	private static final int GRID_Y = 64;
	private static final int SPRITE_SIZE = 64;
	private static final int BASE_Y = 7;
	
	/**
	 * 
		マップ座標[0.0]
		Y座標がグリッドで6個ずれているため、
		
		画面座標x=0
		画面座標y=12*(32/2)
		となる。
		
		マップ座標xが1増えると画面座標yは、1減る。
		
		画面座標x=map_x *(64/2)
		画面座標y=(12-map_x)*(32/2)
		
		マップ座標yが1増えると画面座標xとyが1増える。
		
		画面座標x=(map_y + map_x) *(64/2)
		画面座標y=(12-map_x + map_y)*(32/2)
	 *
	 * @param pMapPointF
	 * @return
	 */
	public PointF getMapPointToDispPoint(Point pMapPoint) {
		PointF dispPointF = new PointF();
		dispPointF.set(
				(pMapPoint.y + pMapPoint.x) * (GRID_X / 2), 
				((BASE_Y - 1) - pMapPoint.x + pMapPoint.y) * (GRID_Y / 2));
		return dispPointF;
	}
	
	public Point pointToIndex(PointF pDispPointF) {
		return pointToIndex(pDispPointF.x, pDispPointF.y);
	}
	public Point pointToIndex(float x, float y) {
		int view_y = (int)((y - GRID_Y * BASE_Y / 2) * 2);
		return new Point(
				(int)((x - view_y + GRID_X * 10) / GRID_X - 10),
				(int)((x + view_y + GRID_X * 10) / GRID_X - 10));
	}

	public SandBoxScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
		
		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}

	@Override
	public void init() {
		// 背景
		Sprite bg = getBaseActivity().getResourceUtil().getSprite(
				"bk/bg_jan.jpg");
		bg.setPosition(0, 0);
		bg.setSize(getWindowWidth(), getWindowHeight());
		attachChild(bg);
		
		showGrid();
		
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {
				PointF pointF = getMapPointToDispPoint(new Point(x, y));
				
				// グリッド画像
				Sprite grid = getResourceSprite("grid0.png");
				grid.setPosition(pointF.x, pointF.y);
				grid.setSize(GRID_X, GRID_Y);
				grid.setZIndex(0);
				// 点滅表示設定
				grid.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
						new AlphaModifier(0.5f, 0.2f, 0.6f),
						new AlphaModifier(0.5f, 0.6f, 0.2f)
						)));
				String fileName = "actor/actor110_3_s.png";
				long[] pFrameDurations = new long[]{100, 100, 100, 100, 100};
				int[] pFrames = new int[]{1, 2, 3, 4, 5};
				int index = x * y % 6;
				switch(index) {
				case 0:
					grid.setColor(Color.RED);
					fileName = "actor/actor110_1_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 1:
					grid.setColor(Color.GREEN);
					fileName = "actor/actor110_2_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 2:
					grid.setColor(Color.BLUE);
					fileName = "actor/actor110_2_s2.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 3:
					grid.setColor(Color.YELLOW);
					fileName = "actor/actor110_3_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 4:
					grid.setColor(Color.PINK);
					fileName = "actor/actor110_3_s2.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 5:
					grid.setColor(Color.CYAN);
					fileName = "actor/actor110_5_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				default:
					grid.setColor(Color.BLACK);
					break;
				}
				AnimatedSprite sprite = getResourceAnimatedSprite(fileName, 3, 4);
				sprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
				sprite.setCurrentTileIndex(x * y % 12);
				sprite.setPosition(
						pointF.x + (GRID_X / 2) - (sprite.getWidth() / 2) - (sprite.getWidth() / 8), 
						pointF.y + (GRID_Y / 2) - sprite.getHeight() + (sprite.getHeight() / 8));
				sprite.setZIndex(2);
				sprite.animate(pFrameDurations, pFrames, true);
				
				grid.setTag(x * 10 + y + 1000);
				sprite.setTag(x * 10 + y + 100);
				attachChild(grid);
				attachChild(sprite);
			}
		}
		sortChildren();
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
	}

	@Override
	public void initSoundAndMusic() {
		// 効果音をロード
	}

	/**
	 * 再開時
	 */
	@Override
	public void onResume() {
		
	}
	/**
	 * バックグラウンド時
	 */
	@Override
	public void onPause() {
		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
			return true;
		}
		return false;
	}

	
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		Point mapPoint = pointToIndex(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		Log.d("", " x = " + mapPoint.x + " y = " + mapPoint.y);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			IEntity entity = getChildByIndex(i);
			if (entity instanceof AnimatedSprite) {
				if (entity.getTag() == (mapPoint.x * 10 + mapPoint.y + 100)) {
					((AnimatedSprite) entity).stopAnimation();
				}
			} else if (entity instanceof Sprite) {
				if (entity.getTag() == (mapPoint.x * 10 + mapPoint.y + 1000)) {
					((Sprite) entity).setColor(Color.BLACK);
					((Sprite) entity).clearEntityModifiers();
				}
			}
		}
		return false;
	}
	
	/**
	 * グリッド表示
	 */
	private void showGrid() {
		
		// 擬似3D縦線
//		{
//			PointF pointStart = getMapPointToDispPoint(new Point(0, 0));
//			final Line line = new Line(pointStart.x + 1, pointStart.y + GRID_Y / 2, pointStart.x + 1, getWindowHeight(), 
//					getBaseActivity().getVertexBufferObjectManager());
//			line.setLineWidth(1);
//			line.setColor(Color.WHITE);
//			line.setAlpha(1.0f);
//			attachChild(line);
//		}
//		{
//			PointF pointStart = getMapPointToDispPoint(new Point(0, 5));
//			final Line line = new Line(pointStart.x + GRID_X / 2, pointStart.y + GRID_Y, pointStart.x + GRID_X / 2, getWindowHeight(), 
//					getBaseActivity().getVertexBufferObjectManager());
//			line.setLineWidth(1);
//			line.setColor(Color.WHITE);
//			line.setAlpha(1.0f);
//			attachChild(line);
//		}
//		{
//			PointF pointStart = getMapPointToDispPoint(new Point(5, 5));
//			final Line line = new Line(pointStart.x + GRID_X, pointStart.y + GRID_Y / 2, pointStart.x + GRID_X, getWindowHeight(), 
//					getBaseActivity().getVertexBufferObjectManager());
//			line.setLineWidth(1);
//			line.setColor(Color.WHITE);
//			line.setAlpha(1.0f);
//			attachChild(line);
//		}
		
		for (int x = 0; x < 7; x++) {
			PointF pointStart = getMapPointToDispPoint(new Point(x, 0));
			PointF pointEnd = getMapPointToDispPoint(new Point(x, 6));
			pointStart.y = pointStart.y + GRID_Y / 2;
			pointEnd.y = pointEnd.y + GRID_Y / 2;
			final Line line = new Line(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			line.setZIndex(1);
			attachChild(line);
		}
		for (int y = 0; y < 7; y++) {
			PointF pointStart = getMapPointToDispPoint(new Point(0, y));
			PointF pointEnd = getMapPointToDispPoint(new Point(6, y));
			pointStart.y = pointStart.y + GRID_Y / 2;
			pointEnd.y = pointEnd.y + GRID_Y / 2;
			final Line line = new Line(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			line.setZIndex(1);
			attachChild(line);
		}
	}
}
