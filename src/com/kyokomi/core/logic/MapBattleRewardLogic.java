package com.kyokomi.core.logic;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.kyokomi.core.constants.GameObjectType;
import com.kyokomi.core.dao.MItemDao;
import com.kyokomi.core.dao.MMapBattleRewardDao;
import com.kyokomi.core.dao.TUserItemDao;
import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.entity.MItemEntity;
import com.kyokomi.core.entity.MMapBattleRewardEntity;
import com.kyokomi.core.entity.TUserItemEntity;
import com.kyokomi.core.scene.KeyListenScene;

public class MapBattleRewardLogic {
	
	public MapBattleRewardDto createMapBattleRewardDto(KeyListenScene pBaseScene, int mapBattleId) {
		MMapBattleRewardDao mMapBattleRewardDao = new MMapBattleRewardDao(); 
		MItemDao mItemDao = new MItemDao();
		
		MapBattleRewardDto mapBattleRewardDto = new MapBattleRewardDto();
		
		SQLiteDatabase database = pBaseScene.getBaseActivity().getDB();
		
		// 報酬マスタを取得
		List<MMapBattleRewardEntity> mMapBattleRewardList = mMapBattleRewardDao.selectByMapBattleId(
				database, mapBattleId);
		
		int totalExp = 0;
		int totalGold = 0;
		List<MItemEntity> itemList = new ArrayList<MItemEntity>();
		for (MMapBattleRewardEntity mMapBattleReward : mMapBattleRewardList) {
			GameObjectType gameObjectType = GameObjectType.get(mMapBattleReward.getObjectType());
			switch (gameObjectType) {
			case EXP:
				totalExp += mMapBattleReward.getObjectId();
				break;
			case GOLD:
				totalGold += mMapBattleReward.getObjectId();
				break;
			case ITEM:
				MItemEntity itemEntity = mItemDao.selectById(
						database, mMapBattleReward.getObjectId());
				itemList.add(itemEntity);
				break;
			}
		}
		database.close();
		
		mapBattleRewardDto.setTotalExp(totalExp);
		mapBattleRewardDto.setTotalGold(totalGold);
		mapBattleRewardDto.setItemList(itemList);
		
		return mapBattleRewardDto;
	}
	
	public MapBattleRewardDto addMapBattleReward(KeyListenScene pBaseScene, int saveId, int mapBattleId) {
		// 報酬取得
		MapBattleRewardDto mapBattleRewardDto = createMapBattleRewardDto(pBaseScene, mapBattleId);
		
		// DBオープン
		SQLiteDatabase database = pBaseScene.getBaseActivity().getDB();
		TUserItemDao tUserItemDao = new TUserItemDao();
		
		// 所持アイテム更新
		for (MItemEntity itemEntity : mapBattleRewardDto.getItemList()) {
			TUserItemEntity tUserItemEntity = tUserItemDao.selectById(database, itemEntity.getItemId());
			if (tUserItemEntity != null) {
				// update
				tUserItemEntity.setItemCount(tUserItemEntity.getItemCount() + 1);
				tUserItemDao.update(database, tUserItemEntity);
			} else {
				// insert
				tUserItemEntity = new TUserItemEntity();
				tUserItemEntity.setItemId(itemEntity.getItemId());
				tUserItemEntity.setItemCount(1);
				tUserItemEntity.setSaveId(saveId);
				tUserItemDao.insert(database, tUserItemEntity);
			}
		}
		pBaseScene.getBaseActivity().closeDB();
		
		return mapBattleRewardDto;
	}
}
