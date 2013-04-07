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
import com.kyokomi.srpgquest.layer.ACutInTouchLayer;
import com.kyokomi.srpgquest.layer.ScenarioStartCutInTouchLayer;
import com.kyokomi.srpgquest.scene.MapBattleScene.LayerZIndex;

public class NovelScene extends SrpgBaseScene implements IOnSceneTouchListener {
//	private final static String TAG = "NovelScene";
	
	/** 会話レイヤー. */
	private TalkLayer mTalkLayer;
	/** 章カットイン. */
	private ACutInTouchLayer mScenarioStartCutInTouchLayer;

	private MScenarioEntity mScenarioEntity;
	
	@Override
	public MScenarioEntity getScenarioEntity() {
		return this.mScenarioEntity;
	}
	public NovelScene(MultiSceneActivity baseActivity, MScenarioEntity pMScenario) {
		super(baseActivity);
		this.mScenarioEntity = pMScenario;
		init();
	}
	
	@Override
	public void init() {
		// 会話内容取得
		List<PlayerTalkDto> talks = getTalkDtoList(
				getScenarioEntity().getScenarioNo(), 
				getScenarioEntity().getSeqNo());
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = getTalkFaceSparse(talks);
		// 会話レイヤー作成
		mTalkLayer = new TalkLayer(this);
		mTalkLayer.initTalk(actorFaces, talks);
		mTalkLayer.hide();
		mTalkLayer.setZIndex(LayerZIndex.TALK_LAYER.getValue());
		mTalkLayer.setTag(999);
		attachChild(mTalkLayer);
		
		// まずは章開始カットイン
		if (getScenarioEntity().getSeqNo() == 1) {
			mScenarioStartCutInTouchLayer = new ScenarioStartCutInTouchLayer(this);
			mScenarioStartCutInTouchLayer.showTouchLayer(this);
		} else {
			mTalkLayer.nextTalk();
		}
		
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
		getMediaManager().playPauseingMusic();
	}
	/**
	 * バックグラウンド時
	 */
	@Override
	public void onPause() {
		getMediaManager().pausePlayingMusic();
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
			// シナリオタイトル表示が先
			if (mScenarioStartCutInTouchLayer != null && 
					mScenarioStartCutInTouchLayer.isTouchLayer(x, y)) {
				// タップで消える
				mScenarioStartCutInTouchLayer.hideTouchLayer(this);
				// 会話を表示開始
				mTalkLayer.nextTalk();
				
			} else if (mTalkLayer != null && mTalkLayer.contains(x, y)) {
				
				getBtnPressedSound().play();
				
				if (mTalkLayer.isNextTalk()) {
					mTalkLayer.nextTalk();
					
				} else {
					mTalkLayer.hide();
					// 次の会話がなくなれば、会話レイヤーを開放
					detachEntity(mTalkLayer);
					mTalkLayer = null;
					
					// 次のシナリオへ
					nextScenario(getScenarioEntity());
				}
			}
		}
		return false;
	}
	@Override
	public void destory() {
		if (mScenarioStartCutInTouchLayer != null) {
			detachEntity(mScenarioStartCutInTouchLayer.getTouchLayer());
			mScenarioStartCutInTouchLayer = null;
		}
		
		if (mTalkLayer != null) {
			detachEntity(mTalkLayer);
			mTalkLayer = null;
		}
	}
}
