package com.kyokomi.core.manager;

import android.util.Log;

import com.kyokomi.core.activity.MultiSceneActivity;
import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dao.TSaveDataDao;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.entity.TSaveDataEntity;
import com.kyokomi.core.scene.KeyListenScene;

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
			tSaveDataEntity.setScenariId(mScenarioEntity.getScenarioId());
			tSaveDataEntity.setPartyId(1); // PartyIdは1スタート
			tSaveDataEntity.setGold(0);
			tSaveDataEntity.setExp(0);
			tSaveDataDao.insert(pBaseActivity.getDB(), tSaveDataEntity);
		}
		
		pBaseActivity.closeDB();
	}
	
	/**
	 * SaveGame
	 * @param pBaseActivity
	 * @return
	 */
	public boolean save(MultiSceneActivity pBaseActivity, int scenarioId) {
		tSaveDataEntity.setScenariId(scenarioId);
		return save(pBaseActivity, tSaveDataEntity);
	}
	
	/**
	 * SaveGame
	 * @param pBaseActivity
	 * @return
	 */
	public boolean save(MultiSceneActivity pBaseActivity, MScenarioEntity pScenarioEntity) {
		tSaveDataEntity.setScenariId(pScenarioEntity.getScenarioId());
		return save(pBaseActivity, tSaveDataEntity);
	}
	
	/**
	 * 経験値を追加します。DB更新はしません。
	 * @param addExp
	 * @return
	 */
	public void addExp(int addExp) {
		tSaveDataEntity.setExp(tSaveDataEntity.getExp() + addExp);
	}
	
	/**
	 * ゴールドを追加します。DB更新はしません。
	 * @param addGold
	 * @return
	 */
	public void addGold(int addGold) {
		tSaveDataEntity.setGold(tSaveDataEntity.getGold() + addGold);
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
	
	public int getScenarioId() {
		return tSaveDataEntity.getScenariId();
	}
	public int getSaveId() {
		return tSaveDataEntity.getSaveId();
	}
//	public TSaveDataEntity getSaveData() {
//		return tSaveDataEntity;
//	}
	
	public SaveDataDto nextScenarioAndSave(KeyListenScene pBaseScene) {
		SaveDataDto saveDataDto = createSaveDataDto(pBaseScene);
		
		pBaseScene.getBaseActivity().openDB(); // --- DB OPNE ---
		
		MScenarioDao mScenarioDao = new MScenarioDao();
		MScenarioEntity nextScenario = mScenarioDao.selectNextScenario(
				pBaseScene.getBaseActivity().getDB(), 
				saveDataDto.getScenarioNo());
		if (nextScenario != null) {
			// save
			save(pBaseScene.getBaseActivity(), nextScenario.getScenarioId());
			// 次のシナリオでセーブデータ作成
			saveDataDto = createSaveDataDto(pBaseScene, nextScenario);			
		} else {
			saveDataDto = null;
		}
		return saveDataDto;
	}
	public SaveDataDto createSaveDataDto(KeyListenScene pBaseScene) {
		pBaseScene.getBaseActivity().openDB(); // --- DB OPNE ---
		MScenarioDao mScenarioDao = new MScenarioDao();
		MScenarioEntity mScenarioEntity = mScenarioDao.selectById(
				pBaseScene.getBaseActivity().getDB(), tSaveDataEntity.getScenariId());	
		pBaseScene.getBaseActivity().closeDB(); // --- DB CLOSE ---
		
		return createSaveDataDto(pBaseScene, mScenarioEntity);
	}
	
	private SaveDataDto createSaveDataDto(KeyListenScene pBaseScene, MScenarioEntity mScenarioEntity) {
		SaveDataDto saveDataDto = new SaveDataDto();
		if (tSaveDataEntity == null) {
			return null;
		}
		
		saveDataDto.setSaveId(tSaveDataEntity.getSaveId());
		saveDataDto.setExp(tSaveDataEntity.getExp());
		saveDataDto.setGold(tSaveDataEntity.getGold());
		
		saveDataDto.setScenarioId(mScenarioEntity.getScenarioId());
		saveDataDto.setScenarioNo(mScenarioEntity.getScenarioNo());
		
		saveDataDto.setScenarioTitle(mScenarioEntity.getScenarioTitle());
		saveDataDto.setSeqNo(mScenarioEntity.getSeqNo());
		
		saveDataDto.setSceneType(mScenarioEntity.getSceneType());
		saveDataDto.setSceneId(mScenarioEntity.getSceneId());
		return saveDataDto;
	}
	
}
