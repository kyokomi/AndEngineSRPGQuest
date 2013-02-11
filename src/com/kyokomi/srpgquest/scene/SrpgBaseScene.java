package com.kyokomi.srpgquest.scene;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.sprite.TiledSprite;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.constants.SceneType;
import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dto.MScenarioEntity;
import com.kyokomi.core.scene.KeyListenScene;

public abstract class SrpgBaseScene extends KeyListenScene {
	
	// ----- DB ------
	private SQLiteDatabase mDB;
	private MScenarioDao mScenarioDao;
	
	// ----- SE, BGM -----
	private Sound mBtnPressedSound;
	public Sound getBtnPressedSound() {
		return mBtnPressedSound;
	}
	
	/** サウンドの準備. */
	@Override
	public void prepareSoundAndMusic() {
		try {
			mBtnPressedSound = createSoundFromFileName("btn_se1.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
		initSoundAndMusic();
	}
	public abstract void initSoundAndMusic();
	
	public SrpgBaseScene(MultiSceneActivity baseActivity) {
		super(baseActivity);
		initDB();
	}
	
	public String createFaceFileName(int imageResId) {
		String baseFileName = "actor" + imageResId;
		return baseFileName + "_f.png";
	}
	public TiledSprite getResourceFaceSprite(int playerId, int imageResId) {
		return getResourceFaceSprite(playerId, createFaceFileName(imageResId));
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
	
	// ------------------ DB ----------------------
	
	private void initDB() {
		 mScenarioDao = new MScenarioDao();
	}
	private void openDB() {
		if (mDB == null || !mDB.isOpen()) {
			mDB = getBaseActivity().getBaseDBOpenHelper().getWritableDatabase();
		}
	}
	private void closeDB() {
		if (mDB != null && mDB.isOpen()) {
			mDB.close();
		}
	}
	
	// 現在のセーブをロードしシナリオを読み込む
	public void loadScenario() {
		// TODO: セーブデータのロード
		int scenarioNo = 1;
		int seqNo = 1;
		startScenario(scenarioNo, seqNo);
	}
	// 次シナリオ読み込み
	public void nextScenario(int scenarioNo, int seqNo) {
		openDB();
		ditactionScene(mScenarioDao.selectNextSeq(mDB, scenarioNo, seqNo));
		closeDB();
	}
	
	// シナリオ読み込み
	public void startScenario(int scenarioNo, int seqNo) {
		openDB();
		ditactionScene(mScenarioDao.selectByScenarioNoAndSeqNo(mDB, scenarioNo, seqNo));
		closeDB();
	}
	
	// シーン振り分け
	private void ditactionScene(MScenarioEntity mScenarioEntity) {
		if (mScenarioEntity == null) {
			getBaseActivity().backToInitial();
			return;
		}
		Log.d("SRPGBaseScene", "ditactionScene " + mScenarioEntity.getSceneType());
		switch (mScenarioEntity.getSceneType()) {
		case SCENE_TYPE_MAP:
			// TODO: map側が未対応
//			showScene(new MapBattleScene(getBaseActivity(), scenarioDto));
			showScene(new MapBattleScene(getBaseActivity()));
			break;
		case SCENE_TYPE_NOVEL:
			showScene(new NovelScene(getBaseActivity(), mScenarioEntity));
			break;
		default:
			getBaseActivity().backToInitial();
			break;
		}
	}
}
