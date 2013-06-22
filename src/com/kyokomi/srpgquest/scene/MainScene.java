package com.kyokomi.srpgquest.scene;


import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.srpgquest.scene.part.NovelPart;
import com.kyokomi.srpgquest.scene.part.ResultPart;
import com.kyokomi.srpgquest.scene.part.SRPGPart;

public class MainScene extends SrpgBaseScene implements IOnSceneTouchListener {
	
	public MainScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
		
		// FPS表示
		initFps(getWindowWidth() - 100, getWindowHeight() - 20, getFont());
	}
	
	/**
	 * サウンド周りの準備
	 */
	@Override
	public void prepareSoundAndMusic() {
		
	}

	@Override
	public void initSoundAndMusic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * キーイベント制御
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
			return true;
		}
		return false;
	}
	
	// ------------------------------------------------------------------
	// ゲーム進行関連
	// ------------------------------------------------------------------
	/**
	 * ゲームパート
	 * @author kyokomi
	 */
	public enum GamePartType {
		/** ノベルパート */
		NOVEL_PART(1),
		/** SRPGパート */
		SRPG_PART(2),
		/** リザルトパート */
		RESULT_PART(3),
		/** バトルパート */
		BATTLE_PART(4),
		;
		private Integer value;
		private GamePartType(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
	}
	private GamePartType mGamePartType;
	private NovelPart mNovelPart;
	private ResultPart mResultPart;
	private SRPGPart mSRPGPart;
	
	/**
	 * ゲーム起動時初期処理
	 */
	@Override
	public void init() {
		SaveDataDto saveDataDto = getBaseActivity().getGameController().
				createSaveDataDto(this);
		init(saveDataDto);
	}
	public void init(SaveDataDto saveDataDto) {
		// タッチイベントを初期化
		setOnSceneTouchListener(null);
		
		// セーブを読み込み
		if (saveDataDto == null) {
			getBaseActivity().backToInitial();
			return;
		}
		switch (saveDataDto.getSceneType()) {
		case SCENE_TYPE_NOVEL:
			mGamePartType = GamePartType.NOVEL_PART;
			if (mNovelPart == null) {
				mNovelPart = new NovelPart(this);
			}
			mNovelPart.init(saveDataDto);
			break;
		case SCENE_TYPE_MAP:
			mGamePartType = GamePartType.SRPG_PART;
			if (mSRPGPart == null) {
				mSRPGPart = new SRPGPart(this);
			}
			mSRPGPart.init(saveDataDto);
			break;
		case SCENE_TYPE_RESULT:
			mGamePartType = GamePartType.RESULT_PART;
			if (mResultPart == null) {
				mResultPart = new ResultPart(this);
			}
			mResultPart.init(saveDataDto);
			break;
		default:
			getBaseActivity().backToInitial();
			return;
		}
		// タッチイベント登録
		setOnSceneTouchListener(this);
	}
	
	/**
	 * 画面タッチイベント
	 * プレイ中のパートに振り分ける
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// 共通タッチイベント
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		switch (mGamePartType) {
		case NOVEL_PART:
			mNovelPart.touchEvent(pScene, pSceneTouchEvent);
			break;
		case SRPG_PART:
			mSRPGPart.touchEvent(pScene, pSceneTouchEvent);
			break;
		case RESULT_PART:
			mResultPart.touchEvent(pScene, pSceneTouchEvent);
			break;
		default:
			break;
		}
		return false;
	}
}
