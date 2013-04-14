package com.kyokomi.srpgquest.logic;

import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.scene.KeyListenScene;

public class ScenarioLogic {

	public void loadScenario(KeyListenScene baseScene) {
		baseScene.getBaseActivity().openDB();
		// シナリオを検索
//		MScenarioEntity scenarioEntity = mScenarioDao.selectById(
//				baseScene.getBaseActivity().getDB(),
//				baseScene.getBaseActivity().getGameController().getScenarioId());
		SaveDataDto saveDataDto = baseScene.getBaseActivity().getGameController().
				createSaveDataDto(baseScene);
		// シナリオ開始
//		startScenario(baseScene, saveDataDto.getScenarioNo(), saveDataDto.getSeqNo());
		baseScene.getBaseActivity().closeDB();
	}
	
	public void getNextScenario(KeyListenScene baseScene, int scenarioNo, int seqNo) {
		baseScene.getBaseActivity().openDB();
		MScenarioDao mScenarioDao = new MScenarioDao();
		// 次のシナリオ取得
		MScenarioEntity mScenarioEntity = mScenarioDao.selectNextSeq(
				baseScene.getBaseActivity().getDB(), scenarioNo, seqNo);
//		startScenario(baseScene, saveDataDto.getScenarioNo(), saveDataDto.getSeqNo());
		baseScene.getBaseActivity().closeDB();
	}
	
	// シナリオ読み込み
	public void startScenario(KeyListenScene baseScene, int scenarioNo, int seqNo) {
//		baseScene.getBaseActivity().openDB();
//		MScenarioDao mScenarioDao = new MScenarioDao();
//		MScenarioEntity mScenarioEntity = mScenarioDao.selectByScenarioNoAndSeqNo(
//				baseScene.getBaseActivity().getDB(), scenarioNo, seqNo);
//		ditactionScene();
//		baseScene.getBaseActivity().closeDB();
	}
}
