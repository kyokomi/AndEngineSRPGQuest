package com.kyokomi.pazuruquest.scene;

import java.util.Date;
import java.util.Random;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.utils.CollidesUtil;
import com.kyokomi.pazuruquest.scene.layer.PanelLayer;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.KeyEvent;

public class PazuruQuestScene extends KeyListenScene 
	implements IOnSceneTouchListener{
	private static final String TAG = "PazuruQuestScene";
	
	public PazuruQuestScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		
		init();

		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}
	
	@Override
	public void init() {
		
		viewWillAppear();
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
	}
	
	@Override
	public void prepareSoundAndMusic() {
			
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		boolean isEvent = false;
		
		switch (pSceneTouchEvent.getAction()) {
			case TouchEvent.ACTION_DOWN:
				touchesBegan(pScene, pSceneTouchEvent);
				isEvent = true;
				break;
			case TouchEvent.ACTION_MOVE:
				touchesMoved(pScene, pSceneTouchEvent);
				isEvent = true;
				break;
			case TouchEvent.ACTION_UP:
				touchesEnded(pScene, pSceneTouchEvent);
				isEvent = true;
				break;
			default:
				Log.d(TAG, "onSceneTouchEvent No Action " + pSceneTouchEvent.getAction());
				break;
		}
		return isEvent;
	}
	
	private Rectangle mBackgroundLayer;
//	private Rectangle mTouchLayer;
	// ----------------------------------------------------------------------------
	private static final int PANEL_SIZE = 78;
	private static final int PANEL_BASE_X = 10;
	private static final int PANEL_BASE_Y = 5;
	private static final int PANEL_COUNT_X = 6;
	private static final int PANEL_COUNT_Y = 6;
	
	private int mPanelBaseX;
	private int mPanelBaseY;
	
	private void initPanelBase(int pX, int pY) {
		this.mPanelBaseX = pX;
		this.mPanelBaseY = pY;
	}
	
	private int getWindowToPanelX(int panelX) {
		return mPanelBaseX + (PANEL_SIZE + 1) * (panelX);
	}
	private int getWindowToPanelY(int panelY) {
		return mPanelBaseX + (PANEL_SIZE + 1) * (panelY);		
	}
	private int getPanelToWindowX(int x) {
		int panelX = (x - mPanelBaseX) / (PANEL_SIZE + 1);
		if (panelX >= PANEL_COUNT_X) {
			panelX = PANEL_COUNT_X - 1;
		} else if (panelX < 0) {
			panelX = 0;
		}
		return panelX;
	}
	private int getPanelToWindowY(int y) {
		int panelY = (y - mPanelBaseY) / (PANEL_SIZE + 1);
		if (panelY >= PANEL_COUNT_Y) {
			panelY = PANEL_COUNT_Y - 1;
		} else if (panelY < 0) {
			panelY = 0;
		}
		return panelY;		
	}
	
	private boolean isPanelToMoveWindowX(int x) {
		return true;
//		int panelX = getPanelToWindowX(x);
//		int windowX = getWindowToPanelX(panelX);
//		int startX = windowX + PANEL_SIZE / 10;
//		int endX = startX + (PANEL_SIZE - PANEL_SIZE / 5) ;
//		
//		if (x > startX && x < endX) { 
//			return true;
//		} else {
//			return false;
//		}
	}
	private boolean isPanelToMoveWindowY(int y) {
		
		return true;
//		int panelY = getPanelToWindowY(y);
//		int windowY = getWindowToPanelY(panelY);
//		int startY = windowY + PANEL_SIZE / 10;
//		int endY = startY + (PANEL_SIZE - PANEL_SIZE / 5);
//		
//		if (y > startY && y < endY) { 
//			return true;
//		} else {
//			return false;
//		}
	}

	// ステート管理定数
	enum PlayState {
	    PlayStateChoose, // パネル選択
	    PlayStateChange, // パネル交換
	};
	
	// ------------------------------------------
	// マネージャー
	// ------------------------------------------
	// 操作可能な状態を表すステータス
	private PlayState state;
	// ゲーム実行完了判定フラグ
	private boolean isFinished;

	// ------------------------------------------
	// レイヤ関連
	// ------------------------------------------
	// パネル交換用レイヤ
//	private CALayer *movingLayer1;
//	private CALayer *movingLayer2;
//	// 削除対象レイヤ
//	private NSMutableSet *deletingLayers;
//	// 追加レイヤ
//	private NSMutableArray *panelLayers;

	// ------------------------------------------
	// ゲーム関連
	// ------------------------------------------
	// 連鎖カウント
	private int chainCount;
	// スコア
	private int score;
	// タイムカウント
	private int timeCount;

	// ビューが表示される直前の初期化処理
	private void viewWillAppear() {
		
		initPanelBase(0, 0);
		
		mBackgroundLayer = new Rectangle(0, 0, 
				PANEL_COUNT_X * (PANEL_SIZE), 
				PANEL_COUNT_Y * (PANEL_SIZE), 
				getBaseActivity().getVertexBufferObjectManager());
		mBackgroundLayer.setColor(Color.TRANSPARENT);
		mBackgroundLayer.setPosition(PANEL_BASE_X, PANEL_BASE_Y);
		mBackgroundLayer.setZIndex(0);
		attachChild(mBackgroundLayer);
//		mTouchLayer = new Rectangle(0, 0, 
//				PANEL_COUNT_X * (PANEL_SIZE + 1), 
//				PANEL_COUNT_Y * (PANEL_SIZE + 1), 
//				getBaseActivity().getVertexBufferObjectManager());
//		mTouchLayer.setColor(Color.TRANSPARENT);
//		mTouchLayer.setPosition(PANEL_BASE_X, PANEL_BASE_Y);
//		mTouchLayer.setZIndex(1);
//		attachChild(mTouchLayer);
		
	    // 乱数シードの初期設定処理(現在時刻に応じた乱数シードを設定することで毎回実行するたびに異なる乱数が生成される）
//	    srand((unsigned)time(NULL));
		Random rand = new Random(new Date().getTime());
	    
	    // ゲーム管理用の初期化
	    state = PlayState.PlayStateChoose;
	    isFinished = false;
//	    deletingLayers = [[NSMutableSet alloc] init];
//	    panelLayers = [[NSMutableArray alloc] init];
	    score = 0;
	    timeCount = 60;
//	    titleButton.hidden = true;
	    
	    // Y方向とX方向6マスのパネル画像を設定
	    for (int y = 0; y < PANEL_COUNT_Y; y++) {
	        for (int x = 0; x < PANEL_COUNT_X; x++) {
	            // 新たなレイヤーを生成
	        	PanelLayer layer = new PanelLayer(x, y, 0, 0, PANEL_SIZE, PANEL_SIZE, 
	        			getBaseActivity().getVertexBufferObjectManager());
	        	layer.setColor(Color.WHITE);
	        	layer.setAlpha(0.5f);
	            // レイヤの中心点の位置を設定
	            layer.setPosition(getWindowToPanelX(x), getWindowToPanelY(y));
	            
	            // 表示するパネル画像をランダム値(0〜4)を取得
	            int dice = rand.nextInt(5);
	            // ランダム値をもとにパネル画像を読み込む
	            String layerName = String.format("pazuru_%d", dice); // TODO: あとでローカライズ
	            String imageName = String.format("%s.png", layerName);  // TODO: あとでローカライズ
	            Log.d(TAG, imageName);
	            Sprite imageSprite = getResourceSprite(imageName);
	            imageSprite.setSize(PANEL_SIZE, PANEL_SIZE);
	            layer.attachChild(imageSprite);
	            layer.setTag(dice);
	            
	            // backViewのlayerに新しく作ったレイヤーを追加する
	            mBackgroundLayer.attachChild(layer);
	        }
	    }
	    sortChildren();
//	    // タイムカウント
//	    [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(timerProc:) userInfo:nil repeats:YES];
	}
	
	private boolean checkTouchedSprite(Sprite sprite, TouchEvent pSceneTouchEvent) {
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (sprite.contains(x, y)){
//		if ((x > sprite.getX() && x < sprite.getX() + sprite.getWidth()) && 
//				(y > sprite.getY() && x < sprite.getY() + sprite.getHeight()) ) {
			return true;
		}
		return false;
	}
	private PanelLayer panelHitTest(float pX, float pY) {
		if (mBackgroundLayer.contains(pX, pY)) {
			int count = mBackgroundLayer.getChildCount();
			for (int i = 0; i < count; i++) {
				if (mBackgroundLayer.getChildByIndex(i) instanceof Rectangle) {
					PanelLayer sprite = (PanelLayer) mBackgroundLayer.getChildByIndex(i);
					if (sprite.contains(pX, pY)) {
						return sprite;
					}
				}
			}
		}
		return null;
	}
	private PanelLayer mMovingLayer1;
	private PanelLayer mMovingLayer2;
	/**
	 * 画面タッチ時のイベント.
	 */
	private void touchesBegan(Scene pScene, TouchEvent pSceneTouchEvent) {
	    // ゲームが完了している場合 または パネル交換中の場合はイベントを無効
	    if (isFinished || state != PlayState.PlayStateChoose) {
	        return ;
	    }
	    // タッチの座標を取得
 		float x = pSceneTouchEvent.getX();
 		float y = pSceneTouchEvent.getY();
	 		
	    /*
	     * タッチイベントでタッチされている画面の位置情報を元にパネルのレイヤーを取得
	     */
	    
	    // タッチされた位置のレイヤをhitTestで取得
 		PanelLayer layer = panelHitTest(x, y);
	    // パネル以外の場合と１回目と同じパネルをを選択した場合は無効
	    if (layer == null || (mMovingLayer1 != null && layer.equals(mMovingLayer1))) {
	        return ;
	    }
	    
	    // 最初のパネルの選択
	    if (mMovingLayer1 == null) {
	    	mMovingLayer1 = layer;
	    }
	}
	
	/**
	 * ドラッグ操作時に呼び出される.
	 * touchesBeganと違って1枚目の選択処理が無い以外はほとんど同じ処理
	 */
	private void  touchesMoved(Scene pScene, TouchEvent pSceneTouchEvent) {
	    if (isFinished || state != PlayState.PlayStateChoose) {
	        return ;
	    }
	    
	    if (mMovingLayer1 == null) {
	        return;
	    }
	    // タッチの座標を取得
 		float x = pSceneTouchEvent.getX();
 		float y = pSceneTouchEvent.getY();
 		
 		if (!mBackgroundLayer.contains(x, y)) {
 			return;
 		}
 		
 		// 移動する
	    if (mMovingLayer1 != null) {
    		if (isPanelToMoveWindowX((int)x) && isPanelToMoveWindowY((int)y)) {
    	    	mMovingLayer1.setPosition(
    	    			getWindowToPanelX(getPanelToWindowX((int)x)),
    	    			getWindowToPanelY(getPanelToWindowY((int)y)));    			
    		}
	    }
	}

	private void touchesEnded(Scene pScene, TouchEvent pSceneTouchEvent) {
		Log.d(TAG,"touchesEnded");
	    if (state != PlayState.PlayStateChange) {
	        Log.d(TAG, "たっちあうと！ " + state);
	        state = PlayState.PlayStateChange;
	        
	        registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					mMovingLayer1 = null;
					finishChange();
				}
			}));
	        // TODO: 交換終了時の処理
	        //[NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(finishChange:) userInfo:nil repeats:NO];
	    }
	}
	
	private PointF getLayerPoint(PanelLayer layer) {
		return new PointF(layer.getX(), layer.getY());
//		return layer.getMoveingAfterPointF();
	}
	// レイヤ交換
	private boolean swapLayers() {
//		final PointF panelPos1 = getLayerPoint(mMovingLayer1);
//		final PointF panelPos2 = getLayerPoint(mMovingLayer2);
//		
//	    // 画面左上を(0,0)その右を(1,0)とするように変換
//	    int px1 = (int)(panelPos1.x - mPanelBaseX) / (PANEL_SIZE + 1);
//	    int py1 = (int)(panelPos1.y - mPanelBaseY) / (PANEL_SIZE + 1);
//	    int px2 = (int)(panelPos2.x - mPanelBaseX) / (PANEL_SIZE + 1);
//	    int py2 = (int)(panelPos2.y - mPanelBaseY) / (PANEL_SIZE + 1);
		final Point panelPos1 = mMovingLayer1.getPanelPoint();
		final Point panelPos2 = mMovingLayer2.getPanelPoint();
		final int px1 = panelPos1.x;
		final int py1 = panelPos1.y;
		final int px2 = panelPos2.x;
		final int py2 = panelPos2.y;
		
	    // 差分を計算することで1枚目と2枚目のパネルが上下か左右に隣り合っていることを確認
	    int dx = px2 - px1;
	    int dy = py2 - py1;
	    
	    if ((dx ==0 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && dy ==0)) {
	    	
	        // 0.4秒かけて１枚目のパネル位置と2枚目のパネル位置を交換
//	    	animationChangePanel(mMovingLayer1, mMovingLayer2, new IEntityModifierListener() {
//				@Override
//				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
////					mMovingLayer1.setPanelPoint(new Point(px2, px2));
////					mMovingLayer2.setPanelPoint(new Point(px1, py1));
//				}
//				@Override
//				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
////					mMovingLayer1.setPanelPoint(new Point(px2, px2));
////					mMovingLayer2.setPanelPoint(new Point(px1, py1));
//				}
//			});
//	        CABasicAnimation *anime1 = [CABasicAnimation animationWithKeyPath:@"positon"];
//	        anime1.duration = 0.4;
//	        anime1.fromValue = [NSNumber valueWithCGPoint:panelPos1];
//	        anime1.toValue = [NSNumber valueWithCGPoint:panelPos2];
//	        // アニメーションが提供するのはあくまでも見た目の変更だけの機能であり、positionは明示的に指定する必要がある
//	        self.movingLayer1.position = panelPos2;
//	        [self.movingLayer1 addAnimation:anime1 forKey:nil];
//	        
//	        // 0.4秒かけて2枚目のパネル位置と1枚目のパネル位置を交換
//	        CABasicAnimation *anime2 = [CABasicAnimation animationWithKeyPath:@"positon"];
//	        anime2.duration = 0.4;
//	        anime2.fromValue = [NSNumber valueWithCGPoint:panelPos2];
//	        anime2.toValue = [NSNumber valueWithCGPoint:panelPos1];
//	        self.movingLayer2.position = panelPos1;
//	        [self.movingLayer2 addAnimation:anime2 forKey:nil];
	        
	        return true;
	    } else {
	    	Log.d(TAG, "dx = " + dx + " dy =" + dy);
	    }
	    
	    return false;
	}
	
//	private void animationMovePanel(Rectangle pRectangle, PointF pMovePointF, 
//			IEntityModifierListener pIEntityModifierListener) {
//		pRectangle.registerEntityModifier(new MoveModifier(0.4f, 
//				pRectangle.getX(), pMovePointF.x, 
//				pRectangle.getY(), pMovePointF.y, 
//				pIEntityModifierListener));
//	}
	private void animationChangePanel(PanelLayer pPanelLayer1, PanelLayer pPanelLayer2, 
			IEntityModifierListener pIEntityModifierListener) {
//		PointF panelPos1 = getLayerPoint(pPanelLayer1);
//		PointF panelPos2 = getLayerPoint(pPanelLayer2);
		
//		int x1 = getPanelX(pPanelLayer1.getPanelPoint().x);
//		int y1 = getPanelY(pPanelLayer1.getPanelPoint().y);
//		int x2 = getPanelX(pPanelLayer2.getPanelPoint().x);
//		int y2 = getPanelY(pPanelLayer2.getPanelPoint().y);
		
		float x1 = pPanelLayer1.getX();
		float y1 = pPanelLayer1.getY();
		float x2 = pPanelLayer2.getX();
		float y2 = pPanelLayer2.getY();
		
		pPanelLayer2.registerEntityModifier(new MoveModifier(0.2f, 
				x2, x1, 
				y2, y1, 
				pIEntityModifierListener));
		pPanelLayer1.registerEntityModifier(new MoveModifier(0.2f, 
				x1, x2, 
				y1, y2));
	}

	// パネルを消せるか判定
	private void finishChange() {
	    
	    // 連鎖カウントを初期化
	    chainCount = 0;
	    
	    // パネル消去の判定
	    if (!checkExplosion()) {
	        
	        // ステータスを選択可能に戻す
	        state = PlayState.PlayStateChoose;
	    }
	}
	
	private boolean checkExplosion() {
		return false;
	}
}
