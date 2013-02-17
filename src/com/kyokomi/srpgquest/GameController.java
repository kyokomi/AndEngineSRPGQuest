package com.kyokomi.srpgquest;

import android.util.Log;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dao.TSaveDataDao;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.entity.TSaveDataEntity;

/**
 * ゲーム全体を制御します.
 * ストーリー進行やScene切替、セーブ、ロードなど
 * @author kyokomi
 *
 */
public class GameController {

	private final static int START_SAVE_ID = 1;
	private TSaveDataDao tSaveDataDao;
	private TSaveDataEntity tSaveDataEntity;
	
	public GameController() {
		tSaveDataDao = new TSaveDataDao();
	}
	/**
	 * NewGame
	 * @param pBaseActivity
	 */
	public void start(MultiSceneActivity pBaseActivity) {
		pBaseActivity.openDB();

		// ID1のシナリオから始める
		MScenarioDao mScenarioDao = new MScenarioDao();
		MScenarioEntity mScenarioEntity = mScenarioDao.selectById(pBaseActivity.getDB(), 1);
		
		// すでに存在すればUpdateし、存在しない場合はInsertする
		if (load(pBaseActivity)) {
			save(pBaseActivity, mScenarioEntity);
		} else {
			tSaveDataEntity = new TSaveDataEntity();
			tSaveDataEntity.setSaveId(START_SAVE_ID);
			tSaveDataEntity.setScenariNo(mScenarioEntity.getScenarioNo());
			tSaveDataEntity.setSeqNo(mScenarioEntity.getSeqNo());
			tSaveDataDao.insert(pBaseActivity.getDB(), tSaveDataEntity);
		}
		
		pBaseActivity.closeDB();
	}
	
	/**
	 * SaveGame
	 * @param pBaseActivity
	 * @return
	 */
	public boolean save(MultiSceneActivity pBaseActivity, MScenarioEntity pScenarioEntity) {
		tSaveDataEntity.setScenariNo(pScenarioEntity.getScenarioNo());
		tSaveDataEntity.setSeqNo(pScenarioEntity.getSeqNo());
		return save(pBaseActivity, tSaveDataEntity);
	}
	/**
	 * SaveGame
	 * @param pBaseActivity
	 * @return
	 */
	private boolean save(MultiSceneActivity pBaseActivity, TSaveDataEntity saveDataEntity) {
		pBaseActivity.openDB();
		// Updateする
		long count = tSaveDataDao.update(pBaseActivity.getDB(), saveDataEntity);
		if (count != 1) {
			Log.e("Save", "save error");
			return false;
		}
		pBaseActivity.closeDB();
		return true;
	}
	
	/**
	 * LoadGame
	 * @param pBaseActivity
	 * @return
	 */
	public boolean load(MultiSceneActivity pBaseActivity) {
		pBaseActivity.openDB();
		tSaveDataEntity = tSaveDataDao.selectById(pBaseActivity.getDB(), START_SAVE_ID);
		pBaseActivity.closeDB();
		if (tSaveDataEntity == null) {
			return false;
		}
		return true;
	}
	
	public TSaveDataEntity getSaveData() {
		return tSaveDataEntity;
	}
}
