package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.ease.EaseBackInOut;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dto.MScenarioDto;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.pazuruquest.scene.PazuruQuestScene;

import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;

public class InitialScene extends SrpgBaseScene implements ButtonSprite.OnClickListener{

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
		
		btnPazuru.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.4f), new MoveModifier(1.0f, 
						btnPazuru.getX(), 
						btnPazuru.getX(), 
						btnPazuru.getY(),
						btnPazuru.getY() - 400,
						EaseBackInOut.getInstance())));

		ButtonSprite btnNovel = getBaseActivity().getResourceUtil().getButtonSprite(
				"initial_btn_01.png", 
				"initial_btn_01_p.png");
		placeToCenterX(btnNovel, 250);
		btnNovel.setTag(4);
		btnNovel.setOnClickListener(this);
		attachChild(btnNovel);
		registerTouchArea(btnNovel);
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
		case 4:
//			getBaseActivity().runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
					SQLiteDatabase database = getBaseActivity().getBaseDBOpenHelper().getWritableDatabase();
					MScenarioDao mScenarioDao = new MScenarioDao();
					// TODO: セーブデータからscenarioNoとseqNoをとってくる
					MScenarioDto scenarioDto = mScenarioDao.selectByScenarioNoAndSeqNo(database, 1, 1);
					switch (scenarioDto.getSceneType()) {
					case SCENE_TYPE_MAP:
						// TODO: map側が未対応
//						showScene(new MapBattleScene(getBaseActivity(), scenarioDto));
						showScene(new MapBattleScene(getBaseActivity()));
						break;
					case SCENE_TYPE_NOVEL:
						showScene(new NovelScene(getBaseActivity(), scenarioDto));
						break;
					default:
						break;
					}					
//				}
//			});
			break;
		}	
	}

	@Override
	public void showScene(KeyListenScene scene) {
		if(!titleBGM.isReleased()) {
			titleBGM.release();
		}
		super.showScene(scene);
	}

	@Override
	public void initSoundAndMusic() {
		
	}

}
