package com.kyokomi.srpgquest.scene;

import java.util.List;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;

import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.srpgquest.scene.MapBattleScene.LayerZIndex;

public class NovelScene extends SrpgBaseScene implements IOnSceneTouchListener {
//	private final static String TAG = "NovelScene";
	
	/** 会話レイヤー. */
	private TalkLayer mTalkLayer;
	private int scenarioNo;
	private int seqNo;
	
	public NovelScene(MultiSceneActivity baseActivity, MScenarioEntity pMScenario) {
		super(baseActivity);
		this.scenarioNo = pMScenario.getScenarioNo();
		this.seqNo = pMScenario.getSeqNo();
		init();
	}
	
	@Override
	public void init() {
		// 会話内容取得
		List<PlayerTalkDto> talks = getTalkDtoList(scenarioNo, seqNo);
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = getTalkFaceSparse(talks);
		// 会話レイヤー作成
		mTalkLayer = new TalkLayer(this);
		mTalkLayer.initTalk(actorFaces, talks);
		mTalkLayer.hide();
		mTalkLayer.setZIndex(LayerZIndex.TALK_LAYER.getValue());
		mTalkLayer.setTag(999);
		attachChild(mTalkLayer);
		
		mTalkLayer.nextTalk();
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}

	@Override
	public void initSoundAndMusic() {
		
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

	/**
	 * 画面タッチイベント.
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		// タッチの座標を取得
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			
			if (mTalkLayer != null && mTalkLayer.contains(x, y)) {
				
				getBtnPressedSound().play();
				
				if (mTalkLayer.isNextTalk()) {
					mTalkLayer.nextTalk();
					
				} else {
					mTalkLayer.hide();
					// 次の会話がなくなれば、会話レイヤーを開放
					detachEntity(mTalkLayer);
					mTalkLayer = null;
					
					// 次のシナリオへ
					nextScenario(scenarioNo, seqNo);
				}
			}
		}
		return false;
	}
}
