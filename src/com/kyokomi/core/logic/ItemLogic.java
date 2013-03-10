package com.kyokomi.core.logic;

import com.kyokomi.core.dao.MAccessoryDao;
import com.kyokomi.core.dao.MItemDao;
import com.kyokomi.core.dao.MWeaponDao;
import com.kyokomi.core.dto.ActorPlayerEquipDto;
import com.kyokomi.core.entity.MAccessoryEntity;
import com.kyokomi.core.entity.MItemEntity;
import com.kyokomi.core.entity.MWeaponEntity;
import com.kyokomi.core.scene.KeyListenScene;

public class ItemLogic {

	private MItemDao mItemDao;
	private MWeaponDao mWeaponDao;
	private MAccessoryDao mAccessoryDao;
	
	public ItemLogic() {
		mItemDao = new MItemDao();
		mWeaponDao = new MWeaponDao();
		mAccessoryDao = new MAccessoryDao();
	}
	
	public ActorPlayerEquipDto createActorPlayerEquipDto(
			KeyListenScene pBaseScene, int weaponId, int accessoryId) {
		ActorPlayerEquipDto actorPlayerEquipDto = new ActorPlayerEquipDto();
		// 武器設定
		MItemEntity mWeaponItemEntity = mItemDao.selectById(
				pBaseScene.getBaseActivity().getDB(), weaponId);
		
		MWeaponEntity mWeaponEntity = null;
		if (mWeaponItemEntity != null) {
			mWeaponEntity = mWeaponDao.selectById(
					pBaseScene.getBaseActivity().getDB(), mWeaponItemEntity.getItemObjectId());
		}
		if (mWeaponItemEntity != null && mWeaponEntity != null) {
			actorPlayerEquipDto.setWeaponName(mWeaponItemEntity.getItemName());
			actorPlayerEquipDto.setWeaponImgResId(mWeaponItemEntity.getItemImageId());
			actorPlayerEquipDto.setWeaponStr(mWeaponEntity.getAttackPoint());
		} else {
			actorPlayerEquipDto.setWeaponName("なし");
			actorPlayerEquipDto.setWeaponImgResId(0);
			actorPlayerEquipDto.setWeaponStr(0);
		}
		// アクセサリー設定
		MItemEntity mAccessoryItemEntity = mItemDao.selectById(
				pBaseScene.getBaseActivity().getDB(), accessoryId);
		MAccessoryEntity mAccessoryEntity = null;
		if (mAccessoryItemEntity != null) {
			mAccessoryEntity = mAccessoryDao.selectById(
					pBaseScene.getBaseActivity().getDB(), mAccessoryItemEntity.getItemObjectId());
		}
		if (mAccessoryItemEntity != null && mAccessoryEntity != null) {
			actorPlayerEquipDto.setAccessoryName(mAccessoryItemEntity.getItemName());
			actorPlayerEquipDto.setAccessoryImgResId(mAccessoryItemEntity.getItemImageId());
			actorPlayerEquipDto.setAccessoryDef(mAccessoryEntity.getDefensePoint());			
		} else {
			actorPlayerEquipDto.setAccessoryName("なし");
			actorPlayerEquipDto.setAccessoryImgResId(0);
			actorPlayerEquipDto.setAccessoryDef(0);
		}
		return actorPlayerEquipDto;
	}
}
