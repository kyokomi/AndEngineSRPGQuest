package com.kyokomi.srpgquest.scene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dto.MScenarioEntity;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.PlayerTalkDto.TalkDirection;
import com.kyokomi.core.sprite.TalkLayer;
import com.kyokomi.srpgquest.scene.MapBattleScene.LayerZIndex;

public class NovelScene extends SrpgBaseScene implements IOnSceneTouchListener {
	private final static String TAG = "NovelScene";
	
	/** 会話レイヤー. */
	private TalkLayer mTalkLayer;
	private int scenarioNo;
	private int seqNo;
	
	/**
	 * @deprecated 廃止予定
	 * @param baseActivity
	 * @param scenarioNo
	 * @param talkNo
	 */
	public NovelScene(MultiSceneActivity baseActivity, int scenarioNo, int talkNo) {
		super(baseActivity);
		this.scenarioNo = scenarioNo;
		this.seqNo = talkNo;
		init();
	}
	public NovelScene(MultiSceneActivity baseActivity, MScenarioEntity pMScenario) {
		super(baseActivity);
		this.scenarioNo = pMScenario.getScenarioNo();
		this.seqNo = pMScenario.getSeqNo();
		init();
	}
	
	@Override
	public void init() {
		
		List<String> talkDataList = new ArrayList<String>();
		try {
			InputStream in = getBaseActivity().getAssets().open("scenario/" + scenarioNo + "_" + seqNo + ".txt");
			BufferedReader reader = 
		        new BufferedReader(new InputStreamReader(in));
		    String str;
		    while ((str = reader.readLine()) != null) {
		    	talkDataList.add(str);
		    	Log.d(TAG, "talks =" + str);
		    }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 会話内容作成
		List<PlayerTalkDto> talks = new ArrayList<PlayerTalkDto>();
		for (String data :talkDataList) {
			String[] tokens = data.split(",");
			if (tokens.length < 5) {
				continue;
			}
			int i = 0;
			int id = Integer.parseInt(tokens[i]);
			i++;
			String name = tokens[i];
			i++;
			int faceIndex = Integer.parseInt(tokens[i]);
			i++;
			TalkDirection talkDirection = TalkDirection.get(Integer.parseInt(tokens[i]));
			i++;
			String text = tokens[i];
			
			PlayerTalkDto talkDto = new PlayerTalkDto(id, name, faceIndex, talkDirection, text);
			talks.add(talkDto);
		}
		Log.d(TAG, "talks count =" + talks.size());
		
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = new SparseArray<TiledSprite>();
		int count = talks.size();
		for (int i = 0; i < count; i++) {
			int playerId = talks.get(i).getPlayerId();
			if (actorFaces.indexOfKey(playerId) >= 0 ) {
				continue;
			}
			// TODO: test用 本当はマスタから引っ張る
			int imgResId = playerId == 1 ? 110 : 34;
			actorFaces.put(playerId, getResourceFaceSprite(playerId, imgResId));
		}
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
				
					// 次のシナリオへ
					nextScenario(scenarioNo, seqNo);
				}
			}
		}
		return false;
	}
}
