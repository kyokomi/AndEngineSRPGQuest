package com.kyokomi.core.logic;

import com.kyokomi.core.dao.MScenarioDao;
import com.kyokomi.core.dto.SaveDataDto;
import com.kyokomi.core.entity.MScenarioEntity;
import com.kyokomi.core.entity.TSaveDataEntity;
import com.kyokomi.core.scene.KeyListenScene;

public class SaveDataLogic {

	private MScenarioDao mScenarioDao;
	
	public SaveDataLogic() {
		mScenarioDao = new MScenarioDao();
	}
	
	public SaveDataDto createSaveDataDto(KeyListenScene pBaseScene, TSaveDataEntity tSaveDataEntity) {
		SaveDataDto saveDataDto = new SaveDataDto();
		if (tSaveDataEntity == null) {
			return null;
		}
		
		saveDataDto.setSaveId(tSaveDataEntity.getSaveId());
		saveDataDto.setExp(tSaveDataEntity.getExp());
		saveDataDto.setGold(tSaveDataEntity.getGold());
		
		MScenarioEntity mScenarioEntity = mScenarioDao.selectById(
				pBaseScene.getBaseActivity().getDB(), tSaveDataEntity.getScenariId());
		
		saveDataDto.setScenarioId(mScenarioEntity.getScenarioId());
		saveDataDto.setScenarioNo(mScenarioEntity.getScenarioNo());
		
		saveDataDto.setScenarioTitle(mScenarioEntity.getScenarioTitle());
		saveDataDto.setSeqNo(mScenarioEntity.getSeqNo());
		
		saveDataDto.setSceneType(mScenarioEntity.getSceneType());
		saveDataDto.setSceneId(mScenarioEntity.getSceneId());
		
		return saveDataDto;
	}
}
