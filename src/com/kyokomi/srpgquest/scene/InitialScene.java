package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.ease.EaseBackInOut;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.utils.ResourceUtil;

import android.view.KeyEvent;

public class InitialScene extends KeyListenScene implements ButtonSprite.OnClickListener{

	// TODO: enumでもよい？
	private static final int INITIAL_START = 1;
	private static final int INITIAL_RANKING = 2;
	private static final int INITIAL_FEEDBACK = 3;
	
	/** ボタンが押された時のサウンド. */
	private Sound btnPressedSound;
	
	public InitialScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
	}

	@Override
	public void init() {
//		Sprite bg = getBaseActivity().getResourceUtil().getSprite(
//				"initial_bg.png");
//		bg.setPosition(0, 0);
//		attachChild(bg);
		
		Sprite titleSprite = getBaseActivity().getResourceUtil().getSprite(
				"initial_title.png");
		placeToCenterX(titleSprite, 40);
		titleSprite.setY(titleSprite.getY() - 200);
		attachChild(titleSprite);
		
		titleSprite.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(0.5f), 
				new MoveModifier(1.0f, 
						titleSprite.getX(), 
						titleSprite.getX(), 
						titleSprite.getY(),
						titleSprite.getY() + 200,
						EaseBackInOut.getInstance())));
		
		// ボタンの追加
		ButtonSprite btnStart = getBaseActivity().getResourceUtil().getButtonSprite(
				"initial_btn_01.png", 
				"initial_btn_01_p.png");
		placeToCenterX(btnStart, 240);
		btnStart.setY(btnStart.getY() + 400);
		btnStart.setTag(INITIAL_START);
		btnStart.setOnClickListener(this);
		attachChild(btnStart);
		// ボタンをタップ可能にする
		registerTouchArea(btnStart);
		
		btnStart.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.0f), new MoveModifier(1.0f, 
						btnStart.getX(), 
						btnStart.getX(), 
						btnStart.getY(),
						btnStart.getY() - 400,
						EaseBackInOut.getInstance())));
		
		ButtonSprite btnRanking = getBaseActivity().getResourceUtil().getButtonSprite(
				"initial_btn_02.png", 
				"initial_btn_02_p.png");
		placeToCenterX(btnRanking, 310);
		btnRanking.setY(btnRanking.getY() + 400);
		btnRanking.setTag(INITIAL_RANKING);
		btnRanking.setOnClickListener(this);
		attachChild(btnRanking);
		registerTouchArea(btnRanking);
		
		btnRanking.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.2f), new MoveModifier(1.0f, 
						btnRanking.getX(), 
						btnRanking.getX(), 
						btnRanking.getY(),
						btnRanking.getY() - 400,
						EaseBackInOut.getInstance())));
		
		ButtonSprite btnRecommend = getBaseActivity().getResourceUtil().getButtonSprite(
				"initial_btn_03.png", 
				"initial_btn_03_p.png");
		placeToCenterX(btnRecommend, 380);
		btnRecommend.setY(btnRecommend.getY() + 400);
		btnRecommend.setTag(INITIAL_FEEDBACK);
		btnRecommend.setOnClickListener(this);
		attachChild(btnRecommend);
		registerTouchArea(btnRecommend);
		
		btnRecommend.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.4f), new MoveModifier(1.0f, 
						btnRecommend.getX(), 
						btnRecommend.getX(), 
						btnRecommend.getY(),
						btnRecommend.getY() - 400,
						EaseBackInOut.getInstance())));
	}

	@Override
	public void prepareSoundAndMusic() {
		// 効果音をロード
		try {
			btnPressedSound = createSoundFromFileName("clock00.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}

	
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// 効果音を再生
		btnPressedSound.play();
		
		switch (pButtonSprite.getTag()) {
		case INITIAL_START:
			showScene(new MainScene(getBaseActivity()));
			break;
		case INITIAL_RANKING:
			showScene(new SandboxScene(getBaseActivity()));
			break;
		case INITIAL_FEEDBACK:
			showScene(new MapBattleScene(getBaseActivity()));
			break;
		}
	}

	private void showScene(KeyListenScene scene) {
		ResourceUtil.getInstance(getBaseActivity()).resetAllTexture();
		getBaseActivity().getEngine().setScene(scene);
		getBaseActivity().appendScene(scene);
	}
}
