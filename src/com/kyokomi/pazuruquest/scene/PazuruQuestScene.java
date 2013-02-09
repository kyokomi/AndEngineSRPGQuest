package com.kyokomi.pazuruquest.scene;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ParallelModifier;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.pazuruquest.layer.PanelLayer;

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
//		int startX = windowX + PANEL_SIZE / 5;
//		int endX = windowX + PANEL_SIZE - (PANEL_SIZE / 5) ;
//		
//		Log.d(TAG, "x = " + x + " start = " + startX + " end = " + endX);
//		if (x < startX || x > endX) { 
//			return true;
//		} else {
//			return false;
//		}
	}
	private boolean isPanelToMoveWindowY(int y) {
		return true;
//		int panelY = getPanelToWindowY(y);
//		int windowY = getWindowToPanelY(panelY);
//		int startY = windowY + PANEL_SIZE / 5;
//		int endY = windowY + PANEL_SIZE - (PANEL_SIZE / 5);
//
//		Log.d(TAG, "y = "+ y + " start = " + startY + " end = " + endY);
//		if (y < startY || y > endY) { 
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
	
	private PanelLayer[][] panelMap;
	
    // 乱数シードの初期設定処理(現在時刻に応じた乱数シードを設定することで毎回実行するたびに異なる乱数が生成される）
	private final Random rand = new Random(new Date().getTime());
	
	// ビューが表示される直前の初期化処理
	private void viewWillAppear() {
		
		panelMap = new PanelLayer[PANEL_COUNT_X][PANEL_COUNT_Y];
		
		initPanelBase(0, 0);
		
		mBackgroundLayer = new Rectangle(0, 0, 
				PANEL_COUNT_X * (PANEL_SIZE), 
				PANEL_COUNT_Y * (PANEL_SIZE), 
				getBaseActivity().getVertexBufferObjectManager());
		mBackgroundLayer.setColor(Color.TRANSPARENT);
		mBackgroundLayer.setPosition(PANEL_BASE_X, PANEL_BASE_Y);
		mBackgroundLayer.setZIndex(0);
		attachChild(mBackgroundLayer);
		
	    // ゲーム管理用の初期化
		chegeState(PlayState.PlayStateChoose);
	    
	    isFinished = false;
	    score = 0;
	    timeCount = 60;
	    // TODO: 再プレイボタン
//	    titleButton.hidden = true;
	    
	    // Y方向とX方向6マスのパネル画像を設定
	    for (int y = 0; y < PANEL_COUNT_Y; y++) {
	        for (int x = 0; x < PANEL_COUNT_X; x++) {
	            
	            // backViewのlayerに新しく作ったレイヤーを追加する
	        	PanelLayer layer = createPanelLayer(x, y, 
	            		getWindowToPanelX(x), getWindowToPanelY(y));
	            mBackgroundLayer.attachChild(layer);
	            panelMap[x][y] = layer;
	        }
	    }
	    sortChildren();
	    
	    // TODO: プレイ時間
//	    // タイムカウント
//	    [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(timerProc:) userInfo:nil repeats:YES];
	}
	
	private PanelLayer createPanelLayer(int panelX, int panelY, float x, float y) {
        // 新たなレイヤーを生成
    	PanelLayer layer = new PanelLayer(panelX, panelY, 0, 0, PANEL_SIZE, PANEL_SIZE, 
    			getBaseActivity().getVertexBufferObjectManager());
    	layer.setColor(Color.WHITE);
    	layer.setAlpha(0.5f);
        // レイヤの中心点の位置を設定
        layer.setPosition(x, y);
        
        // 表示するパネル画像をランダム値(0〜4)を取得
        int dice = rand.nextInt(5);
        // ランダム値をもとにパネル画像を読み込む
        String layerName = String.format("pazuru_%d", dice); // TODO: あとでローカライズ
        String imageName = String.format("%s.png", layerName);  // TODO: あとでローカライズ
        Log.d(TAG, imageName);
        Sprite imageSprite = getResourceSprite(imageName);
        imageSprite.setSize(PANEL_SIZE, PANEL_SIZE);
        layer.attachChild(imageSprite);
        layer.setName(layerName);
        layer.setImage(imageSprite);
        
        layer.setPanelPoint(new Point(panelX, panelY));
        
        return layer;
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
	    	mMovingLayer1.setPosition(x - mMovingLayer1.getWidth() / 2, y - mMovingLayer1.getHeight() /2);
	    	// 移動前のパネル座標
	    	int beforePanelX = mMovingLayer1.getPanelPoint().x;
	    	int beforePanelY = mMovingLayer1.getPanelPoint().y;
	    	// 移動前の画面座標
			float beforeX = getWindowToPanelX(beforePanelX);
			float beforeY = getWindowToPanelX(beforePanelY);
			// 移動後のパネル座標（予定）
	    	int panelX = getPanelToWindowX((int)x);
			int panelY = getPanelToWindowX((int)y);

			// 動いていない
			if (Math.abs(beforePanelX - panelX) == 0 && Math.abs(beforePanelY - panelY) == 0) {
				return;
			}
			
//	    	// 斜め移動とかぶっ飛んだ移動を補正する
//	    	if ((Math.abs(beforePanelX - panelX) == 1 && Math.abs(beforePanelY - panelY) == 0) == false ||
//					(Math.abs(beforePanelX - panelX) == 0 &&  Math.abs(beforePanelY - panelY) == 1 ) == false) {
//	    		
//	    		int overX = beforePanelX - panelX;
//	    		int overY = beforePanelY - panelY;
//	    		// 移動幅が大きい方を候補にする
//	    		if (Math.abs(overX) > Math.abs(overY)) {
//	    			if (overX > 0) {
//	    				panelX = beforePanelX + 1;
//	    			} else if (overX < 0) {
//	    				panelX = beforePanelX - 1;
//	    			}
//	    			panelY = beforePanelY;
//	    		} else {
//	    			if (overY > 0) {
//	    				panelY = beforePanelY + 1;
//	    			} else if (overY < 0){
//	    				panelY = beforePanelY - 1;
//	    			}
//	    			panelX = beforePanelX;
//	    		}
//	    		Log.d(TAG, "補正 panelXY[" + panelX + ", " + panelY + "]");
//	    	}
			
			// TODO: 移動判定補正（未使用）
    		if (isPanelToMoveWindowX((int)x) || isPanelToMoveWindowY((int)y)) {
    			
    			// 移動したとき
    			if (beforePanelX != panelX || beforePanelY != panelY) {
    				// 斜め移動は禁止
    				if ((Math.abs(beforePanelX - panelX) == 1 &&  Math.abs(beforePanelY - panelY) == 0) ||
    						(Math.abs(beforePanelX - panelX) == 0 &&  Math.abs(beforePanelY - panelY) == 1 )) {
    					
        				// 移動先にパネルが存在する場合
        				PanelLayer temp = panelMap[panelX][panelY];
        				if (temp != null) {
    	    				mMovingLayer1.setPanelPoint(new Point(panelX, panelY));
    	    				panelMap[panelX][panelY] = mMovingLayer1;
    	    				temp.setPanelPoint(new Point(beforePanelX, beforePanelY));
    	    				panelMap[beforePanelX][beforePanelY] = temp;
    	    				
    	    				animationMovePanel(temp, beforeX, beforeY, new IEntityModifierListener() {
    							@Override
    							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
    							}
    							@Override
    							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
    							}
    						});
        				}    					
    				}
    			}
    		}
	    }
	}

	private void touchesEnded(Scene pScene, TouchEvent pSceneTouchEvent) {
		Log.d(TAG,"touchesEnded");
	    if (state != PlayState.PlayStateChange) {
	        Log.d(TAG, "たっちあうと！ " + state);
	        
	        registerUpdateHandler(new TimerHandler(0.01f, new ITimerCallback() {
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					if(mMovingLayer1 != null) {
						
						chegeState(PlayState.PlayStateChange);
						
						int beforePanelX = mMovingLayer1.getPanelPoint().x;
				    	int beforePanelY = mMovingLayer1.getPanelPoint().y;
						float beforeX = getWindowToPanelX(beforePanelX);
						float beforeY = getWindowToPanelX(beforePanelY);
						mMovingLayer1.setPosition(beforeX, beforeY);
						mMovingLayer1 = null;
						finishChange();
					}
				}
			}));
	        // TODO: 交換終了時の処理
	        //[NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(finishChange:) userInfo:nil repeats:NO];
	    }
	}
	
	private void animationMovePanel(PanelLayer pLayer, float moveX, float moveY, 
			IEntityModifierListener pIEntityModifierListener) {
		pLayer.registerEntityModifier(new MoveModifier(0.1f, 
				pLayer.getX(), moveX, 
				pLayer.getY(), moveY, 
				pIEntityModifierListener));
	}

	// パネルを消せるか判定
	private void finishChange() {
		Log.d(TAG, "------ finishChange -----");
		
	    // 連鎖カウントを初期化
	    chainCount = 0;
	    
	    // パネル消去の判定
	    if (!checkExplosion()) {
	        
	        // ステータスを選択可能に戻す
	    	chegeState(PlayState.PlayStateChoose);
	    }
	}
	
	private List<PanelLayer> mDeleteLayers;
	
	private boolean checkExplosion() {
		Log.d(TAG, "checkExplosion");
		
	    if (mMovingLayer1 != null) {
	        // 2回目以降の連鎖時にこれらのレイヤが使われないようにnilをセットする
	    	mMovingLayer1 = null;
	    }
	    
	    /*
	     * 上下左右に3個以上連なっている同じ種類のパネルに対して、それらのパネルをdeletingLayers変数に格納していく。
	     *
	     * tempListに左右に同じ種類のパネルが連なっている間、tempListにそれらのレイヤを追加していく。
	     * 最終的にtempListに3個以上の要素が格納されていればdeletingLayersに格納し直す。
	     *
	     */
	    mDeleteLayers = new ArrayList<PanelLayer>();
	    List<PanelLayer> tempList = new ArrayList<PanelLayer>();
	    for (int y = 0; y < PANEL_COUNT_Y; y++) {
	        String currentName = "";
	        for (int x = 0; x < PANEL_COUNT_X; x++) {
	            PanelLayer layer = panelMap[x][y];
	            if (layer == null) {
	            	continue;
	            }
	            String layerName = layer.getName();
	            if (layerName.equals(currentName)) {
	            	tempList.add(layer);
	            } else {
	                currentName = layerName;
	                // 消滅判定
	                if (tempList.size() >= 3) {
	                	mDeleteLayers.addAll(tempList);
	                }
	                tempList = new ArrayList<PanelLayer>();
	                tempList.add(layer);
	            }
	        }
	        // 消滅判定
            if (tempList.size() >= 3) {
            	mDeleteLayers.addAll(tempList);
            }
            tempList = new ArrayList<PanelLayer>();
	    }
	    
	    for (int x = 0; x < PANEL_COUNT_X; x++) {
	    	String currentName = "";
	        for (int y = 0; y < PANEL_COUNT_Y; y++) {
	            PanelLayer layer = panelMap[x][y];
	            if (layer == null) {
	            	continue;
	            }
	            String layerName = layer.getName();
	            if (layerName.equals(currentName)) {
	            	tempList.add(layer);
	            } else {
	                currentName = layerName;
	                // 消滅判定
	                if (tempList.size() >= 3) {
	                	mDeleteLayers.addAll(tempList);
	                }
	                tempList = new ArrayList<PanelLayer>();
	                tempList.add(layer);
	            }
	        }
	        // 消滅判定
            if (tempList.size() >= 3) {
            	mDeleteLayers.addAll(tempList);
            }
            tempList = new ArrayList<PanelLayer>();
	    }
	    // 拡大と透明化のアニメーション
	    for (PanelLayer layer : mDeleteLayers) {
	    	layer.registerEntityModifier(new ParallelEntityModifier(
	    			new ScaleModifier(0.3f, 1.0f, 1.5f),
	    			new AlphaModifier(0.3f, 1.0f, 0.0f, new IEntityModifier.IEntityModifierListener() {
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				}
			})));
	    	layer.getImage().registerEntityModifier(new AlphaModifier(0.3f, 1.0f, 0.0f));
	    }
	    
	    // 消滅対象あり
	    if (mDeleteLayers.size() > 0) {
	    	// TODO: スコア
	        // スコア設定
//	        self.score += ([self.deletingLayers count] * 10 * (self.chainCount + 1));
//	        self.scoreLabel.text = [NSString stringWithFormat:@"%05d", self.score];
	        
	    	// TODO: 連鎖
	        // 連鎖カウントアップ
//	        self.chainCount++;
	        // アニメーション後(0.3秒後)にcheckChainを呼び出す
//	        [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(checkChain:) userInfo:nil repeats:NO];
	    	
	    	registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					// 消滅した分を詰めるためパネルを落下させる
					finishExplosion();
				}
			}));
	    	
	    	return true;
	        
        // 消滅対象なし
	    } else {
	    	// 新しいパネルを降らせる
	        return addNewLayers();
	    }
	}
	
	/**
	 * 消滅完了時の処理.
	 */
	private void finishExplosion() {
	    Log.d(TAG, "finishExplosion");

	    for (PanelLayer layer : mDeleteLayers) {
	    	panelMap[layer.getPanelPoint().x][layer.getPanelPoint().y] = null;
	    }
	    
	    // 消滅対象のレイヤーを削除(削除は別スレッドで)
		getBaseActivity().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				for (PanelLayer layer : mDeleteLayers) {
					layer.detachChildren();
			        layer.detachSelf();
			    }
			}
		});
	    
	    /*
	     * 消滅したパネルの上のパネルを下に落とす
	     */
	    boolean hasDropped = false;
	    
	    for (int x = 0; x < PANEL_COUNT_X; x++) {
	        int count = 0;
	        for (int y = 0; y < PANEL_COUNT_Y; y++) {
	        	PanelLayer layer = panelMap[x][y];
	        	if (layer != null) {
	        		count++;
	        	}
	        }
	        
	        final int START_Y = PANEL_COUNT_Y - 1;
	        int y = START_Y;
	        for (int i = 0; i < count; i++) {
	            while (y >= 0) {
	            	PanelLayer layer = panelMap[x][y];
	                y--;
	                if (layer != null) {
	                	// panelYが異なる場合
	                    if (layer.getPanelPoint().y != (START_Y -i)) {
	                        // 落下アニメーション(0.25秒)
	                    	int panelX = layer.getPanelPoint().x;
	                    	int panelY = layer.getPanelPoint().y;
	                    	int movePanelY = (START_Y -i);
	                    	int startX = getWindowToPanelX(panelX);
	                    	int startY = getWindowToPanelY(panelY);
	                    	int endY = getWindowToPanelY(movePanelY);
	                    	layer.setPanelPoint(new Point(panelX, movePanelY));
	                    	
	                    	panelMap[panelX][movePanelY] = layer;
	                    	panelMap[panelX][panelY] = null;
	                    	
	                        layer.registerEntityModifier(new MoveModifier(0.25f, startX, startX, startY, endY));
	                        hasDropped = true;
	                    }
	                    break;
	                }
	            }
	        }
	    }
	    
	    // パネルを落とした場合、連鎖して消滅することがあるためpositon設定による暗黙的アニメーション後に再度checkExplosionを呼び出す
	    if (hasDropped) {
	    	registerUpdateHandler(new TimerHandler(0.25f, new ITimerCallback() {
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					if (!checkExplosion()) {
						chegeState(PlayState.PlayStateChoose);
			    	}
				}
			}));
	    } else {
	        if (!addNewLayers()) {
	        	chegeState(PlayState.PlayStateChoose);
	        }
	    }
	}

	private boolean addNewLayers() {
		Log.d(TAG, "addNewLayers");
		
	    boolean hasAdded = false;
	    final int START_Y = PANEL_COUNT_Y - 1;
	    for (int x = 0; x < PANEL_COUNT_X; x++) {
	    	int y = START_Y;
	        for (; y >= 0; y--) {
	            PanelLayer layer = panelMap[x][y];
	            if (layer == null) {
	                break;
	            }
	        }
	        y += 1;
	        if (y > 0) {
	            hasAdded = true;
	        }
	        for (int i = 0; i < y; i++) {
	        	// 上の方で生成する
	        	final PanelLayer layer = createPanelLayer(x, y, 
	        			getWindowToPanelX(x), getWindowToPanelY(i - y));
	            
	            // backViewのlayerに新しく作ったレイヤーを追加する
	            mBackgroundLayer.attachChild(layer);

	            int spaceCount = getPanelYCount(x);
    	        if (spaceCount >= 0) {
    	        	final int panelX = x;
    	        	final int panelY = (START_Y - spaceCount);
                	int startX = getWindowToPanelX(panelX);
                	int startY = getWindowToPanelY(i - y);
                	int endY = getWindowToPanelY(panelY);
                	
                	panelMap[panelX][panelY] = layer;
                	
                	// 落下アニメーション(0.25秒)                	
    	            layer.registerEntityModifier(new MoveModifier(0.5f, startX, startX, startY, endY, 
    	            		new IEntityModifier.IEntityModifierListener() {
    					@Override
    					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
    					}
    					
    					@Override
    					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
    						layer.setPanelPoint(new Point(panelX, panelY));
    					}
    				}));
    	        }
	        }
	    }
	    
	    if (hasAdded) {
	    	registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
			    	if (!checkExplosion()) {
			    		chegeState(PlayState.PlayStateChoose);
			    	}
				}
			}));
	    	return true;
	    } else {
	        return false;
	    }
	}
	
	private int getPanelYCount(int x) {
		int count = 0;
        for (int y = 0; y < PANEL_COUNT_Y; y++) {
        	PanelLayer layer = panelMap[x][y];
        	if (layer != null) {
        		count++;
        	}
        }
        return count;
	}
	
	private void chegeState(PlayState pPlayState) {
		Log.d(TAG, "ChangeState " + state + " >> " + pPlayState);
		state = pPlayState;
	}
}
