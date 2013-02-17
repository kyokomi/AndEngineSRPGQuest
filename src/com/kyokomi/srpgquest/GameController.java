package com.kyokomi.srpgquest;

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
		
		tSaveDataEntity = new TSaveDataEntity();
		tSaveDataEntity.setSaveId(START_SAVE_ID);
		// ID1のシナリオから始める
		MScenarioDao mScenarioDao = new MScenarioDao();
		MScenarioEntity mScenarioEntity = mScenarioDao.selectById(pBaseActivity.getDB(), 1);
		tSaveDataEntity.setScenariNo(mScenarioEntity.getScenarioNo());
		tSaveDataEntity.setSeqNo(mScenarioEntity.getSeqNo());
		
		// Insertする
		tSaveDataDao.insert(pBaseActivity.getDB(), tSaveDataEntity);
		
		pBaseActivity.closeDB();
	}
	
	/**
	 * SaveGame
	 * @param pBaseActivity
	 * @return
	 */
	public boolean save(MultiSceneActivity pBaseActivity) {
		// Updateする
		return true;
	}
	
	/**
	 * LoadGame
	 * @param pBaseActivity
	 * @return
	 */
	public boolean load(MultiSceneActivity pBaseActivity) {
		tSaveDataEntity = tSaveDataDao.selectById(pBaseActivity.getDB(), START_SAVE_ID);
		if (tSaveDataEntity == null) {
			return false;
		}
		return true;
	}
	
	public TSaveDataEntity getSaveData() {
		return tSaveDataEntity;
	}
}
