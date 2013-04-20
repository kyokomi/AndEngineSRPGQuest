package com.kyokomi.srpgquest.scene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.util.modifier.IModifier;

import android.util.Log;
import android.util.SparseArray;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dao.MActorDao;
import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.PlayerTalkDto.TalkDirection;
import com.kyokomi.core.entity.MActorEntity;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.ActorSprite;

public abstract class SrpgBaseScene extends KeyListenScene {
	
	private Sprite mTouchSprite;
	
	public abstract MScenarioEntity getScenarioEntity();
	
	public void touchSprite(float x, float y) {
		if (mTouchSprite == null) {
			mTouchSprite = getResourceSprite("touch.png");
			mTouchSprite.setVisible(false);
			mTouchSprite.setZIndex(999);
			attachChild(mTouchSprite);
		}
		mTouchSprite.setPosition(x - mTouchSprite.getWidth() / 2, y - mTouchSprite.getHeight() / 2);
		mTouchSprite.registerEntityModifier(new ParallelEntityModifier(
				new ScaleModifier(0.2f, 1.0f, 1.5f, new IEntityModifier.IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						mTouchSprite.setVisible(true);
					}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					}
				}),
				new AlphaModifier(0.2f, 1.0f, 0.0f, new IEntityModifier.IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						mTouchSprite.setVisible(false);
					}
				})
			));
	}
	
	// ----- DB ------
	private MScenarioDao mScenarioDao;
	
//	// ----- SE, BGM -----
//	private Sound mBtnPressedSound;
//	public Sound getBtnPressedSound() {
//		return mBtnPressedSound;
//	}
	
	/** サウンドの準備. */
	@Override
	public void prepareSoundAndMusic() {
//		try {
//			mBtnPressedSound = createSoundFromFileName("btn_se1.wav");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		initSoundAndMusic();
	}
	public abstract void initSoundAndMusic();
	public abstract void onResume();
	public abstract void onPause();
	
	public SrpgBaseScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		initDB();
	}
	
	public TiledSprite getResourceFaceSprite(int playerId, int imageResId) {
		return getResourceFaceSprite(playerId, ActorSprite.getFaceFileName(imageResId));
	}
	public TiledSprite getResourceFaceSprite(int tag, String faceFileName) {
		TiledSprite tiledSprite = getResourceTiledSprite(faceFileName, 4, 2);
		tiledSprite.setTag(tag);
		return tiledSprite;
	}
	
	/**
	 * IconSetからSpriteを取得.
	 * @return TiledSprite
	 */
	public TiledSprite getIconSetTiledSprite() {
		return getBaseActivity().getResourceUtil().getTiledSprite("icon_set.png", 16, 48);
	}
	
	// ------------------ 会話 ---------------------
	public List<PlayerTalkDto> getTalkDtoList(int scenarioNo, int seqNo) {
		// ファイル読み込み
		List<String> talkDataList = new ArrayList<String>();
		try {
			InputStream in = getBaseActivity().getAssets().open("scenario/" + scenarioNo + "_" + seqNo + ".txt");
			BufferedReader reader = 
		        new BufferedReader(new InputStreamReader(in));
		    String str;
		    while ((str = reader.readLine()) != null) {
		    	talkDataList.add(str);
		    }
		    in.close();
		    reader.close();
		    
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
		
		return talks;
	}
	public SparseArray<TiledSprite> getTalkFaceSparse(List<PlayerTalkDto> talks) {
		getBaseActivity().openDB();
		MActorDao mActorDao = new MActorDao();
		// 顔画像作成
		SparseArray<TiledSprite> actorFaces = new SparseArray<TiledSprite>();
		int count = talks.size();
		for (int i = 0; i < count; i++) {
			PlayerTalkDto playerTalkDto = talks.get(i);
			MActorEntity mActorEntity = mActorDao.selectById(getBaseActivity().getDB(), playerTalkDto.getPlayerId());
			playerTalkDto.setName(mActorEntity.getActorName());
			if (actorFaces.indexOfKey(mActorEntity.getActorId()) >= 0 ) {
				continue;
			}
			actorFaces.put(mActorEntity.getActorId(), 
					getResourceFaceSprite(mActorEntity.getActorId(), mActorEntity.getImageResId()));
		}
		getBaseActivity().closeDB();
		return actorFaces;
	}
	// ------------------ DB ----------------------
	
	/**
	 * @deprecated
	 */
	private void initDB() {
		 mScenarioDao = new MScenarioDao();
	}
	
	// ------------------ シナリオ ----------------------
	
	/**
	 * 現在のセーブをロードしシナリオを読み込む
	 * @deprecated
	 */
	public void loadScenario() {
		getBaseActivity().openDB();
		// シナリオを検索
		MScenarioEntity scenarioEntity = mScenarioDao.selectById(
				getBaseActivity().getDB(),
				getBaseActivity().getGameController().getScenarioId());
		// シナリオ開始
		startScenario(scenarioEntity);
		getBaseActivity().closeDB();
	}
	/**
	 * 次シナリオ読み込み
	 * @deprecated
	 */
	public void nextScenario() {
		nextScenario(getScenarioEntity());
	}
	/**
	 * @deprecated
	 * @param scenarioEntity
	 */
	public void nextScenario(MScenarioEntity scenarioEntity) {
		getBaseActivity().openDB();
		ditactionScene(mScenarioDao.selectNextSeq(getBaseActivity().getDB(), 
				scenarioEntity.getScenarioNo(), 
				scenarioEntity.getSeqNo()));
		getBaseActivity().closeDB();
	}
	
	/**
	 * シナリオ読み込み
	 * @deprecated
	 * @param scenarioEntity
	 */
	public void startScenario(MScenarioEntity scenarioEntity) {
		getBaseActivity().openDB();
		ditactionScene(mScenarioDao.selectByScenarioNoAndSeqNo(getBaseActivity().getDB(), 
				scenarioEntity.getScenarioNo(), 
				scenarioEntity.getSeqNo()));
		getBaseActivity().closeDB();
	}
	
	/**
	 * シーン振り分け
	 * @deprecated 
	 */
	private void ditactionScene(MScenarioEntity mScenarioEntity) {
		if (mScenarioEntity == null) {
			getBaseActivity().backToInitial();
			return;
		}
		
		// セーブ
		getBaseActivity().getGameController().save(getBaseActivity(), mScenarioEntity);
		
		Log.d("SRPGBaseScene", "ditactionScene " + mScenarioEntity.getSceneType());
		switch (mScenarioEntity.getSceneType()) {
		case SCENE_TYPE_MAP:
//			showScene(new MapBattleScene(getBaseActivity(), mScenarioEntity));
			break;
		case SCENE_TYPE_NOVEL:
			showScene(new NovelScene(getBaseActivity(), mScenarioEntity));
			break;
		case SCENE_TYPE_RESULT:
			showScene(new ResultScene(getBaseActivity(), mScenarioEntity));
			break;	
		default:
			getBaseActivity().backToInitial();
			break;
		}
		// 開放
		destory();
		reset();
	}
	
	/**
	 * @deprecated 
	 */
	public abstract void destory();
}
