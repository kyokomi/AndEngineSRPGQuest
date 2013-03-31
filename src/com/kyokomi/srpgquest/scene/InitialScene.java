package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseBackInOut;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.pazuruquest.scene.MjPazuruQuestScene;
import com.kyokomi.core.manager.MediaManager.MusicType;
import com.kyokomi.core.manager.MediaManager.SoundType;

import android.view.KeyEvent;

public class InitialScene extends SrpgBaseScene 
	implements ButtonSprite.OnClickListener, IOnSceneTouchListener {

	// TODO: enumでもよい？
	private static final int INITIAL_START = 1;
	private static final int INITIAL_RANKING = 2;
	private static final int INITIAL_FEEDBACK = 3;
	
	public InitialScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		
		init();
		
		getMediaManager().playStart(MusicType.TITLE_BGM);
	}

	@Override
	public void init() {
		Sprite bg = getBaseActivity().getResourceUtil().getSprite(
				"bk/bg_jan.jpg");
		bg.setPosition(0, 0);
		bg.setSize(getWindowWidth(), getWindowHeight());
		attachChild(bg);
		
//		ButtonSprite btnStart = getBaseActivity().getResourceUtil().getButtonSprite(
//				"initial_btn_01.png", 
//				"initial_btn_01_p.png");
//		placeToCenterX(btnStart, 40);
//		btnStart.setY(btnStart.getY() + 400);
//		btnStart.setTag(INITIAL_START);
//		btnStart.setOnClickListener(this);
//		attachChild(btnStart);
//		// ボタンをタップ可能にする
//		registerTouchArea(btnStart);
//				
//		btnStart.registerEntityModifier(new SequenceEntityModifier(
//				new DelayModifier(1.0f), new MoveModifier(1.0f, 
//						btnStart.getX(), 
//						btnStart.getX(), 
//						btnStart.getY(),
//						btnStart.getY() - 400,
//						EaseBackInOut.getInstance())));
		
		ButtonSprite btnSandBox = getBaseActivity().getResourceUtil().getButtonSprite(
				"menu/initial_btn_01.png", 
				"menu/initial_btn_01_p.png");
		placeToCenterX(btnSandBox, 110);
		btnSandBox.setY(btnSandBox.getY() + 400);
		btnSandBox.setTag(INITIAL_RANKING);
		btnSandBox.setOnClickListener(this);
		attachChild(btnSandBox);
		registerTouchArea(btnSandBox);
		
		btnSandBox.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.2f), new MoveModifier(1.0f, 
						btnSandBox.getX(), 
						btnSandBox.getX(), 
						btnSandBox.getY(),
						btnSandBox.getY() - 400,
						EaseBackInOut.getInstance())));
		
		ButtonSprite btnPazuru = getBaseActivity().getResourceUtil().getButtonSprite(
				"menu/initial_btn_01.png", 
				"menu/initial_btn_01_p.png");
		placeToCenterX(btnPazuru, 180);
		btnPazuru.setY(btnPazuru.getY() + 400);
		btnPazuru.setTag(INITIAL_FEEDBACK);
		btnPazuru.setOnClickListener(this);
		attachChild(btnPazuru);
		registerTouchArea(btnPazuru);
		
		btnPazuru.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.4f), new MoveModifier(1.0f, 
						btnPazuru.getX(), 
						btnPazuru.getX(), 
						btnPazuru.getY(),
						btnPazuru.getY() - 400,
						EaseBackInOut.getInstance())));

		ButtonSprite btnNovel = getBaseActivity().getResourceUtil().getButtonSprite(
				"menu/initial_btn_01.png", 
				"menu/initial_btn_01_p.png");
		placeToCenterX(btnNovel, 250);
		btnNovel.setTag(4);
		btnNovel.setOnClickListener(this);
		attachChild(btnNovel);
		registerTouchArea(btnNovel);

		ButtonSprite btnNewGame = getBaseActivity().getResourceUtil().getButtonSprite(
				"menu/instruction_btn.png", 
				"menu/instruction_btn_p.png");
		placeToCenterX(btnNewGame, 320);
		btnNewGame.setTag(5);
		btnNewGame.setOnClickListener(this);
		attachChild(btnNewGame);
		registerTouchArea(btnNewGame);
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
	}

	@Override
	public void initSoundAndMusic() {
		// 効果音をロード
		try {
			getMediaManager().resetAllMedia();
			getMediaManager().createMedia(SoundType.BTN_PRESSED_SE);
			getMediaManager().createMedia(MusicType.TITLE_BGM);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 再開時
	 */
	@Override
	public void onResume() {
		getBaseActivity().getMediaManager().play(MusicType.TITLE_BGM);
	}
	/**
	 * バックグラウンド時
	 */
	@Override
	public void onPause() {
		getBaseActivity().getMediaManager().pause(MusicType.TITLE_BGM);
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
		// 効果音を再生
		getMediaManager().play(SoundType.BTN_PRESSED_SE);
		
		switch (pButtonSprite.getTag()) {
		case INITIAL_START:
//			showScene(new MapBattleScene(getBaseActivity()));
			break;
		case INITIAL_RANKING:
			showScene(new SandboxScene(getBaseActivity()));
			break;
		case INITIAL_FEEDBACK:
			showScene(new MjPazuruQuestScene(getBaseActivity()));
			break;
		case 4: // シナリオデータ読み込み
			loadScenario();
			break;
		case 5: // NewGame
			getBaseActivity().getGameController().start(getBaseActivity());
			loadScenario();
			break;
		}	
	}

	@Override
	public void showScene(KeyListenScene scene) {
		super.showScene(scene);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		return false;
	}

	@Override
	public MScenarioEntity getScenarioEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		
	}
}
