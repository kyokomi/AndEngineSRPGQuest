package com.kyokomi.srpgquest.scene;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.sprite.CommonWindowRectangle;
import com.kyokomi.core.logic.ActorPlayerLogic;
import com.kyokomi.srpgquest.logic.TextLogic;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle;
import com.kyokomi.srpgquest.sprite.PlayerStatusRectangle.PlayerStatusRectangleType;
import com.kyokomi.srpgquest.utils.ActorSpriteUtil;

import android.graphics.Typeface;
import android.view.KeyEvent;

public class InitialScene extends SrpgBaseScene 
	implements ButtonSprite.OnClickListener, IOnSceneTouchListener {

	// TODO: enumでもよい？
	private static final int SAVE_LOAD = 1;
	private static final int NEW_GAME = 2;
	
	public InitialScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		
		init();		
//		getMediaManager().playStart(MusicType.TITLE_BGM);
	}

	@Override
	public void init() {
		// 背景
		Sprite bg = getBaseActivity().getResourceUtil().getSprite(
				"bk/002-Woods01.jpg");
		bg.setPosition(0, 0);
		bg.setSize(getWindowWidth(), getWindowHeight());
		attachChild(bg);
		
		// セーブデータを取得
		if (getBaseActivity().getGameController().load(getBaseActivity()) == false) {
			// セーブデータが無いときは作ってあげる
			getBaseActivity().getGameController().start(getBaseActivity());
		}
		
		// タイトル
		CommonWindowRectangle titleWindowRectangle = new CommonWindowRectangle(
				0, 0, 
				getWindowWidth(), 
				80,
				Color.BLACK, 0.5f, this);
		Font titleFont = createFont(Typeface.DEFAULT_BOLD, 30, Color.WHITE);
		Text titleText = new Text(10, 10, titleFont, "〜 タイトル未定SRPG 〜", 
				getBaseActivity().getVertexBufferObjectManager());
		placeToCenterX(titleText, titleWindowRectangle.getY() 
				+ titleWindowRectangle.getHeight() / 2 
				- titleText.getHeight() / 2);
		titleWindowRectangle.attachChild(titleText);
		attachChild(titleWindowRectangle);
		
		// ユーザー情報
		float infoBaseX = 0;
		float infoBaseY = titleWindowRectangle.getY() + titleWindowRectangle.getHeight();
		ActorPlayerLogic actorPlayerLogic  = new ActorPlayerLogic();
		// TODO: とりあえず1キャラなので。。。
		ActorPlayerDto actorPlayerDto = actorPlayerLogic.createActorPlayerDto(this, 1);
		PlayerStatusRectangle statusRect = new PlayerStatusRectangle(
				this, getFont(), actorPlayerDto, 
				ActorSpriteUtil.getFaceFileName(actorPlayerDto.getImageResId()), 0, 0);
		statusRect.setColor(Color.BLACK);
		statusRect.setAlpha(0.5f);
		// 枠は別
		CommonWindowRectangle commonWindowRectangle = new CommonWindowRectangle(
				0, 0, 
				statusRect.getWidth(), 
				statusRect.getHeight() / 2,
				Color.TRANSPARENT, 0.0f, this);
		statusRect.attachChild(commonWindowRectangle);
		statusRect.show(PlayerStatusRectangleType.MINI_STATUS);
		statusRect.setPosition(infoBaseX, infoBaseY);
		attachChild(statusRect);
		
		// セーブデータの情報
		SaveDataDto saveDataDto = getBaseActivity().getGameController().createSaveDataDto(this);
		
		CommonWindowRectangle scenarioInfoRectangle = new CommonWindowRectangle(0, 0, 
				getWindowWidth() / 2, getWindowHeight() / 4, Color.BLACK, 0.5f, this);
		attachChild(scenarioInfoRectangle);
		scenarioInfoRectangle.setPosition(statusRect.getX() + statusRect.getWidth(), statusRect.getY());
		// 章情報
		String scenarioTitleStr = 
				"第" + saveDataDto.getScenarioNo() + "章" +
				" (" + saveDataDto.getScenarioNo() + "-" + saveDataDto.getSeqNo() + ")" + 
				" : 「" + saveDataDto.getScenarioTitle() + "」";
		Text scenarioTitle = new Text(10, 10, getFont(), 
				scenarioTitleStr, 
				getBaseActivity().getVertexBufferObjectManager());
		scenarioTitle.setPosition(12, 12);
		scenarioInfoRectangle.attachChild(scenarioTitle);
		// 所持ゴールド、所持経験値
		Font paramTitleFont = createFont(Typeface.DEFAULT, getDefualtFontSize(), Color.YELLOW);
		
		TextLogic textLogic = new TextLogic();
		
		Rectangle goldTextRectangle = textLogic.createTextRectangle(
				"所持ゴールド :", paramTitleFont, 
				saveDataDto.getGold() + " Gold", getFont(), 
				getBaseActivity().getVertexBufferObjectManager());
		goldTextRectangle.setPosition(scenarioTitle.getX(), 
				scenarioTitle.getY() + scenarioTitle.getHeight() + 4);
		scenarioInfoRectangle.attachChild(goldTextRectangle);
		Rectangle expTextRectangle = textLogic.createTextRectangle(
				"所持経験値 :", paramTitleFont, 
				saveDataDto.getExp() + " Exp", getFont(), 
				getBaseActivity().getVertexBufferObjectManager());
		expTextRectangle.setPosition(goldTextRectangle.getX(), 
				goldTextRectangle.getY() + goldTextRectangle.getHeight() + 4);
		scenarioInfoRectangle.attachChild(expTextRectangle);
		
		// 続きからボタン
		ButtonSprite btnNovel = getBaseActivity().getResourceUtil().getButtonSprite(
				"menu/initial_btn_01.png", 
				"menu/initial_btn_01_p.png");
		placeToCenterX(btnNovel, 260);
		btnNovel.setTag(SAVE_LOAD);
		btnNovel.setOnClickListener(this);
		attachChild(btnNovel);
		registerTouchArea(btnNovel);

		// ニューゲームボタン
		ButtonSprite btnNewGame = getBaseActivity().getResourceUtil().getButtonSprite(
				"menu/instruction_btn.png", 
				"menu/instruction_btn_p.png");
		placeToCenterX(btnNewGame, 400);
		btnNewGame.setTag(NEW_GAME);
		btnNewGame.setOnClickListener(this);
		attachChild(btnNewGame);
		registerTouchArea(btnNewGame);
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
	}

	@Override
	public void initSoundAndMusic() {
		// 効果音をロード
//		try {
//			getMediaManager().resetAllMedia();
//			getMediaManager().createMedia(SoundType.BTN_PRESSED_SE);
//			getMediaManager().createMedia(MusicType.TITLE_BGM);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 再開時
	 */
	@Override
	public void onResume() {
//		getMediaManager().playPauseingMusic();
	}
	/**
	 * バックグラウンド時
	 */
	@Override
	public void onPause() {
//		getMediaManager().pausePlayingMusic();
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
//		// 効果音を再生
//		getMediaManager().play(SoundType.BTN_PRESSED_SE);
//		// BGMなどをリセット
//		getMediaManager().resetAllMedia();
		
		switch (pButtonSprite.getTag()) {
		case SAVE_LOAD: // シナリオデータ読み込み
			showScene(new MainScene(getBaseActivity()));
			break;
		case NEW_GAME: // NewGame
			getBaseActivity().getGameController().start(getBaseActivity());
			showScene(new MainScene(getBaseActivity()));
			break;
		}	
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		return false;
	}
}
