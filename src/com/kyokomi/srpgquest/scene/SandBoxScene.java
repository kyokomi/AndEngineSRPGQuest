package com.kyokomi.srpgquest.scene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.utils.CollidesUtil;
import com.kyokomi.core.utils.CollidesUtil.TouchEventFlick;

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
	
	private static final int GRID_SIZE_X = 8;
	private static final int GRID_SIZE_Y = 8;
	
	/** ドラッグ判定用 */
	private float[] touchStartPoint;
	
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
	public PointF indexToDisp(Point pMapIndex) {
		return indexToDisp(pMapIndex.x, pMapIndex.y);
	}
	public PointF indexToDisp(int x, int y) {
		PointF dispPointF = new PointF();
		dispPointF.set(
				(y + x) * (GRID_X / 2), 
				((BASE_Y - 1) - x + y) * (GRID_Y / 2));
		return dispPointF;
	}
	
	public Point dispToIndex(PointF pDispPointF) {
		return dispToIndex(pDispPointF.x, pDispPointF.y);
	}
	public Point dispToIndex(float x, float y) {
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
		touchStartPoint = new float[2];
		
		// 背景
		Sprite bg = getBaseActivity().getResourceUtil().getSprite(
				"bk/bg_jan.jpg");
		bg.setPosition(0, 0);
		bg.setSize(getWindowWidth(), getWindowHeight());
		attachChild(bg);

		// ベースマップ生成
		Rectangle mapBaseRect = new Rectangle(0, 0, 
				getWindowWidth(), getWindowHeight(), 
				getBaseActivity().getVertexBufferObjectManager());
		mapBaseRect.setTag(9999999); // TODO:どうにかして
		mapBaseRect.setColor(Color.TRANSPARENT);
		
		// 選択カーソル生成
		Sprite cursorSprite = getResourceSprite("grid128.png");
		cursorSprite.setColor(Color.CYAN);
		cursorSprite.setVisible(false);
		cursorSprite.setTag(99); // TODO: どうにかして
		cursorSprite.setZIndex(2);
		cursorSprite.setSize(GRID_X, GRID_Y);
		cursorSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new AlphaModifier(0.5f, 0.2f, 0.6f),
				new AlphaModifier(0.5f, 0.6f, 0.2f)
				)));
		mapBaseRect.attachChild(cursorSprite);
		
		// グリッドライン表示
		showGrid(mapBaseRect);
		
		for (int x = 0; x < GRID_SIZE_X; x++) {
			for (int y = 0; y < GRID_SIZE_Y; y++) {
				PointF pointF = indexToDisp(new Point(x, y));
				
				// グリッド画像
				Sprite grid = getResourceSprite("grid128.png");
				grid.setPosition(pointF.x, pointF.y);
				grid.setSize(GRID_X, GRID_Y);
				grid.setColor(Color.TRANSPARENT);
				grid.setZIndex(0);
				String fileName = "actor/actor110_3_s.png";
				long[] pFrameDurations = new long[]{100, 100, 100, 100, 100};
				int[] pFrames = new int[]{1, 2, 3, 4, 5};
				int index = x * y % 6;
				switch(index) {
				case 0:
					fileName = "actor/actor110_1_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 1:
					fileName = "actor/actor110_2_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 2:
					fileName = "actor/actor110_2_s2.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 3:
					fileName = "actor/actor110_3_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 4:
					fileName = "actor/actor110_3_s2.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				case 5:
					fileName = "actor/actor110_5_s.png";
					pFrames = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					pFrameDurations = new long[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
					break;
				default:
					break;
				}
				AnimatedSprite sprite = getResourceAnimatedSprite(fileName, 3, 4);
				sprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
				sprite.setCurrentTileIndex(x * y % 12);
				sprite.setPosition(
						pointF.x + (GRID_X / 2) - (sprite.getWidth() / 2) - (sprite.getWidth() / 8), 
						pointF.y + (GRID_Y / 2) - sprite.getHeight() + (sprite.getHeight() / 8));
				sprite.setZIndex(3);
				sprite.animate(pFrameDurations, pFrames, true);
				
				grid.setTag(x * 10 + y + 1000);
				sprite.setTag(x * 10 + y + 100);
				mapBaseRect.attachChild(grid);
				mapBaseRect.attachChild(sprite);
			}
		}
		mapBaseRect.sortChildren();
		
		attachChild(mapBaseRect);
		
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
	
	// TODO: グリッドのはみ出した数から計算できるはず。。。
	private static final int OVER_START_DISP_X = -220;
	private static final int OVER_END_DISP_X   = 0;
	private static final int OVER_START_DISP_Y = 0;
	private static final int OVER_END_DISP_Y   = 100;
	private float getStartDispX() {
		return 0 + OVER_START_DISP_X;
	}
	private float getStartDispY() {
		return 0 + OVER_START_DISP_Y;
	}
	private float getEndDispX(IAreaShape entity) {
		return entity.getWidth() + OVER_END_DISP_X;
	}
	private float getEndDispY(IAreaShape entity) {
		return entity.getHeight() + OVER_END_DISP_Y;
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		// タッチした位置のアニメーションの停止とマスの色を黒にして点滅を停止
		Rectangle mapBaseRect = getBaseMap();
		if (mapBaseRect == null) {
			return false;
		}
		// タッチ位置をスクロールを考慮したマップ座標に変換
		Point mapPoint = dispToIndex(
				pSceneTouchEvent.getX() - mapBaseRect.getX(), 
				pSceneTouchEvent.getY() - mapBaseRect.getY());
		Log.d("", " x = " + mapPoint.x + " y = " + mapPoint.y);

		// スクロールチェック
		TouchEventFlick touchEventFlick = TouchEventFlick.UN_FLICK;
		float xDistance = 0;
		float yDistance = 0;
		if (pSceneTouchEvent.isActionDown()) {
			// 開始点を登録
			touchStartPoint[0] = pSceneTouchEvent.getX();
			touchStartPoint[1] = pSceneTouchEvent.getY();
		} else if (pSceneTouchEvent.isActionUp() || pSceneTouchEvent.isActionCancel()) {
			float[] touchEndPoint = new float[2];
			touchEndPoint[0] = pSceneTouchEvent.getX();
			touchEndPoint[1] = pSceneTouchEvent.getY();
			// フリックチェック
			touchEventFlick = CollidesUtil.checkToushFlick(touchStartPoint, touchEndPoint);
			if (touchEventFlick != TouchEventFlick.UN_FLICK) {
				xDistance = touchEndPoint[0] -touchStartPoint[0];
				yDistance = touchEndPoint[1] -touchStartPoint[1];
			}
		}
		// スクロール時
		if (touchEventFlick != TouchEventFlick.UN_FLICK) {
			// マップをスクロール
			float moveToX = mapBaseRect.getX() + xDistance;
			float moveToY = mapBaseRect.getY() + yDistance;
			// 表示可能領域で補正
			if (getStartDispX() > moveToX) {
				moveToX = getStartDispX();
			}
			if (getEndDispX(mapBaseRect) < (moveToX + mapBaseRect.getWidth())) {
				moveToX = getEndDispX(mapBaseRect) - mapBaseRect.getWidth();
			}
			if (getStartDispY() > moveToY) {
				moveToY = getStartDispY();
			}
			if (getEndDispY(mapBaseRect) < (moveToY + mapBaseRect.getHeight())) {
				moveToY = getEndDispY(mapBaseRect) - mapBaseRect.getHeight();
			}
			
			mapBaseRect.registerEntityModifier(new MoveModifier(0.2f, 
					mapBaseRect.getX(), moveToX,
					mapBaseRect.getY(), moveToY));
			
		// スクロール以外のとき
		} else {
			// 押し上げ時
			if (pSceneTouchEvent.isActionUp()) {
				int count = mapBaseRect.getChildCount();
				for (int i = 0; i < count; i++) {
					IEntity entity = mapBaseRect.getChildByIndex(i);
					if (entity instanceof AnimatedSprite) {
						if (entity.getTag() == (mapPoint.x * 10 + mapPoint.y + 100)) {
							((AnimatedSprite) entity).stopAnimation();
							((AnimatedSprite) entity).setColor(new Color(0.4f, 0.4f, 0.4f));
						}
					} else if (entity instanceof Sprite) {
						if (entity.getTag() == 99) {
							PointF touchPoint = indexToDisp(mapPoint);
							entity.setPosition(touchPoint.x, touchPoint.y);
							entity.setVisible(true);
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private Rectangle getBaseMap() {
		Rectangle mapBaseRect = null;
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			IEntity entity = getChildByIndex(i);
			if (entity instanceof Rectangle && entity.getTag() == 9999999) {
				mapBaseRect = (Rectangle) entity;
			}
		}
		return mapBaseRect;
	}
	
	/**
	 * グリッド表示
	 */
	private void showGrid(IEntity entity) {
		
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
		
		for (int x = 0; x <= GRID_SIZE_X; x++) {
			PointF pointStart = indexToDisp(new Point(x, 0));
			PointF pointEnd = indexToDisp(new Point(x, GRID_SIZE_X));
			pointStart.y = pointStart.y + GRID_Y / 2;
			pointEnd.y = pointEnd.y + GRID_Y / 2;
			final Line line = new Line(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			line.setZIndex(1);
			entity.attachChild(line);
		}
		for (int y = 0; y <= GRID_SIZE_Y; y++) {
			PointF pointStart = indexToDisp(new Point(0, y));
			PointF pointEnd = indexToDisp(new Point(GRID_SIZE_Y, y));
			pointStart.y = pointStart.y + GRID_Y / 2;
			pointEnd.y = pointEnd.y + GRID_Y / 2;
			final Line line = new Line(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, 
					getBaseActivity().getVertexBufferObjectManager());
			line.setLineWidth(1);
			line.setColor(Color.WHITE);
			line.setAlpha(0.5f);
			line.setZIndex(1);
			entity.attachChild(line);
		}
	}
}
