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
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.logic.TalkLogic;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.srpgquest.constant.LayerZIndexType;
import com.kyokomi.srpgquest.layer.ScenarioStartCutInTouchLayer;

public class MainScene extends AbstractGameScene implements IOnSceneTouchListener {
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
		;
		private Integer value;
		private GamePartType(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
	}
	
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
	private GamePartType mGamePartType;
	
	/**
	 * ゲーム起動時初期処理
	 */
	@Override
	public void init() {
		// セーブを読み込み
		SaveDataDto saveDataDto = getBaseActivity().getGameController().
				createSaveDataDto(this);
		switch (saveDataDto.getSceneType()) {
		case SCENE_TYPE_NOVEL:
			mGamePartType = GamePartType.NOVEL_PART;
			initNovel(saveDataDto);
			break;
		case SCENE_TYPE_MAP:
			mGamePartType = GamePartType.SRPG_PART;
			initMap(saveDataDto);
			break;
		case SCENE_TYPE_RESULT:
			mGamePartType = GamePartType.RESULT_PART;
			initResult(saveDataDto);
			break;
		default:
			throw new RuntimeException("sceneTypeが不正です ["
					+ saveDataDto.getSceneType() + "]");
		}
	}
	
	/**
	 * 次シナリオへ
	 */
	private void nextScenario() {
		// セーブAnd次シナリオへ進行
		getBaseActivity().getGameController().nextScenarioAndSave(this);
		init();
	}
	
	/**
	 * 画面タッチイベント
	 * プレイ中のパートに振り分ける
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO: 共通タッチイベント
//		touchSprite(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		switch (mGamePartType) {
		case NOVEL_PART:
			novelTouchEvent(pScene, pSceneTouchEvent);
			break;
		default:
			break;
		}
		return false;
	}
	
	// ------------------------------------------------------------------
	// ノベルパート関連
	// ------------------------------------------------------------------
	
	/**
	 * ノベルパートの初期化処理
	 */
	private void initNovel(SaveDataDto saveDataDto) {
		// 会話内容取得
		TalkLogic talkLogic = new TalkLogic();
		List<PlayerTalkDto> talks = talkLogic.getTalkDtoList(this,
				saveDataDto.getScenarioNo(), 
				saveDataDto.getSeqNo());
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = talkLogic.getTalkFaceSparse(this, talks);
		// 会話レイヤー作成
		TalkLayer talkLayer = new TalkLayer(this);
		talkLayer.initTalk(actorFaces, talks);
		talkLayer.hide();
		talkLayer.setZIndex(LayerZIndexType.TALK_LAYER.getValue());
		talkLayer.setTag(999);// TODO: TAG管理しないと・・・
		attachChild(talkLayer);
		
		// まずは章開始カットイン
		if (saveDataDto.getSeqNo() == 1) {
			ScenarioStartCutInTouchLayer scenarioStartCutInTouchLayer = 
					new ScenarioStartCutInTouchLayer(this);
			scenarioStartCutInTouchLayer.initLayer(this, saveDataDto);
			scenarioStartCutInTouchLayer.showTouchLayer(this);
		} else {
			talkLayer.nextTalk();
		}
		
		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);
	}
	
	/**
	 * ノベルパートのタッチイベント
	 */
	private void novelTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			ScenarioStartCutInTouchLayer startTouchLayer = (ScenarioStartCutInTouchLayer) getChildByTag(
					ScenarioStartCutInTouchLayer.TAG);
			TalkLayer talkLayer = (TalkLayer) getChildByTag(999); // TODO: TAG管理しないと・・・
			if (startTouchLayer != null && startTouchLayer.isTouchLayer(x, y)) {
				// タップで消える
				startTouchLayer.hideTouchLayer((KeyListenScene) pScene);
				// 会話を開始
				if (talkLayer != null) {
					talkLayer.nextTalk();
				}
				detachEntity(startTouchLayer);
				
			} else if (talkLayer != null && talkLayer.contains(x, y)) {
				// TODO: SE再生
				
				if (talkLayer.isNextTalk()) {
					talkLayer.nextTalk();
					
				} else {
					talkLayer.hide();
					// 次の会話がなくなれば、会話レイヤーを開放
					detachEntity(talkLayer);
					
					// ノベルパート終了
					endNovel();
				}
			}
		}
	}
	
	private void endNovel() {
		// 次のシナリオへ
		nextScenario();
	}
	
	/**
	 * SRPGマップバトルパートの初期化処理
	 */
	private void initMap(SaveDataDto saveDataDto) {
		
	}
	
	/**
	 * リザルト画面パートの初期化処理
	 */
	private void initResult(SaveDataDto saveDataDto) {
		
	}

}
