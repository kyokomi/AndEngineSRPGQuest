package com.kyokomi.core.logic;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.kyokomi.core.constants.GameObjectType;
import com.kyokomi.core.dao.MItemDao;
import com.kyokomi.core.dao.MMapBattleRewardDao;
import com.kyokomi.core.dto.MapBattleRewardDto;
import com.kyokomi.core.entity.MItemEntity;
import com.kyokomi.core.entity.MMapBattleRewardEntity;
import com.kyokomi.core.scene.KeyListenScene;

public class MapBattleRewardLogic {
	private MItemDao mItemDao;
	private MMapBattleRewardDao mMapBattleRewardDao;
	public MapBattleRewardLogic() {
		mItemDao = new MItemDao();
		mMapBattleRewardDao = new MMapBattleRewardDao();
	}
	
	public MapBattleRewardDto createMapBattleRewardDto(KeyListenScene pBaseScene, int mapBattleId) {
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
				totalExp += mMapBattleReward.getObjectId();
				break;
			}
		}
		database.close();
		
		mapBattleRewardDto.setTotalExp(totalExp);
		mapBattleRewardDto.setTotalGold(totalGold);
		mapBattleRewardDto.setItemList(itemList);
		
		return mapBattleRewardDto;
	}
	
}
