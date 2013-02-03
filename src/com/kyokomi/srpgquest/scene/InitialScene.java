package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseBackInOut;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.TextButton;
import com.kyokomi.core.utils.ResourceUtil;
import com.kyokomi.pazuruquest.scene.PazuruQuestScene;

import android.view.KeyEvent;

public class InitialScene extends KeyListenScene implements ButtonSprite.OnClickListener{

	// TODO: enumでもよい？
	private static final int INITIAL_START = 1;
	private static final int INITIAL_RANKING = 2;
	private static final int INITIAL_FEEDBACK = 3;
	
	/** タイトル画面のBGM. */
	private Music titleBGM;
	/** ボタンが押された時のサウンド. */
	private Sound btnPressedSound;
	
	public InitialScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		init();
		
		titleBGM.setLooping(true);
		titleBGM.play();
	}

	@Override
	public void init() {
		Sprite bg = getBaseActivity().getResourceUtil().getSprite(
				"bg_jan.jpg");
		bg.setPosition(0, 0);
		bg.setSize(getWindowWidth(), getWindowHeight());
		attachChild(bg);
		
//		Sprite titleSprite = getBaseActivity().getResourceUtil().getSprite(
//				"initial_title.png");
//		placeToCenterX(titleSprite, 40);
//		titleSprite.setY(titleSprite.getY() - 200);
//		attachChild(titleSprite);
//		
//		titleSprite.registerEntityModifier(new SequenceEntityModifier(
//				new DelayModifier(0.5f), 
//				new MoveModifier(1.0f, 
//						titleSprite.getX(), 
//						titleSprite.getX(), 
//						titleSprite.getY(),
//						titleSprite.getY() + 200,
//						EaseBackInOut.getInstance())));
		
		// ボタンの追加
//		TextButton btnStart = new TextButton(text, 0f, 0f, 0f, 0f, 
//				getBaseActivity().getVertexBufferObjectManager(), this);
		ButtonSprite btnStart = getBaseActivity().getResourceUtil().getButtonSprite(
				"initial_btn_01.png", 
				"initial_btn_01_p.png");
		placeToCenterX(btnStart, 40);
		btnStart.setY(btnStart.getY() + 400);
		btnStart.setTag(INITIAL_START);
		btnStart.setOnClickListener(this);
		attachChild(btnStart);
		// ボタンをタップ可能にする
		registerTouchArea(btnStart);
		
//		Text text = new Text(16, 16, getFont(), 
//				"SRPG", 
//				new TextOptions(HorizontalAlign.CENTER), 
//				getBaseActivity().getVertexBufferObjectManager());
//		text.setColor(Color.BLACK);
//		text.setPosition(btnStart.getX() - text.getWidth() + 10, btnStart.getY());
//		attachChild(text);
		
		btnStart.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.0f), new MoveModifier(1.0f, 
						btnStart.getX(), 
						btnStart.getX(), 
						btnStart.getY(),
						btnStart.getY() - 400,
						EaseBackInOut.getInstance())));
		
		ButtonSprite btnSandBox = getBaseActivity().getResourceUtil().getButtonSprite(
				"initial_btn_01.png", 
				"initial_btn_01_p.png");
		placeToCenterX(btnSandBox, 110);
		btnSandBox.setY(btnSandBox.getY() + 400);
		btnSandBox.setTag(INITIAL_RANKING);
		btnSandBox.setOnClickListener(this);
		attachChild(btnSandBox);
		registerTouchArea(btnSandBox);
		
//		Text sandBoxText = new Text(16, 16, getFont(), "SandBox", 
//				new TextOptions(HorizontalAlign.CENTER),
//				getBaseActivity().getVertexBufferObjectManager());
//		sandBoxText.setColor(Color.BLACK);
//		sandBoxText.setPosition(btnSandBox.getX() - sandBoxText.getWidth() + 10, btnSandBox.getY());
//		attachChild(sandBoxText);
//		
		btnSandBox.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.2f), new MoveModifier(1.0f, 
						btnSandBox.getX(), 
						btnSandBox.getX(), 
						btnSandBox.getY(),
						btnSandBox.getY() - 400,
						EaseBackInOut.getInstance())));
		
		ButtonSprite btnPazuru = getBaseActivity().getResourceUtil().getButtonSprite(
				"initial_btn_01.png", 
				"initial_btn_01_p.png");
		placeToCenterX(btnPazuru, 180);
		btnPazuru.setY(btnPazuru.getY() + 400);
		btnPazuru.setTag(INITIAL_FEEDBACK);
		btnPazuru.setOnClickListener(this);
		attachChild(btnPazuru);
		registerTouchArea(btnPazuru);
		
//		Text pazuruBoxText = new Text(16, 16, getFont(), "パズル", 
//				new TextOptions(HorizontalAlign.CENTER),
//				getBaseActivity().getVertexBufferObjectManager());
//		pazuruBoxText.setColor(Color.BLACK);
//		pazuruBoxText.setPosition(btnPazuru.getX() - pazuruBoxText.getWidth() + 10, btnPazuru.getY());
//		attachChild(pazuruBoxText);
		
		btnPazuru.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.4f), new MoveModifier(1.0f, 
						btnPazuru.getX(), 
						btnPazuru.getX(), 
						btnPazuru.getY(),
						btnPazuru.getY() - 400,
						EaseBackInOut.getInstance())));
	}

	@Override
	public void prepareSoundAndMusic() {
		// 効果音をロード
		try {
			btnPressedSound = createSoundFromFileName("clock00.wav");
			titleBGM = createMusicFromFileName("title_bgm1.mp3");
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
			showScene(new MapBattleScene(getBaseActivity()));
			break;
		case INITIAL_RANKING:
			showScene(new SandboxScene(getBaseActivity()));
			break;
		case INITIAL_FEEDBACK:
			showScene(new PazuruQuestScene(getBaseActivity()));
			break;
		}	
	}

	private void showScene(KeyListenScene scene) {
		if(!titleBGM.isReleased()) {
			titleBGM.release();
		}
		
		ResourceUtil.getInstance(getBaseActivity()).resetAllTexture();
		getBaseActivity().getEngine().setScene(scene);
		getBaseActivity().appendScene(scene);
	}

}
