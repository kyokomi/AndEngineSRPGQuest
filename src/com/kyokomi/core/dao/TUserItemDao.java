package com.kyokomi.core.dao;

import com.kyokomi.core.entity.TUserItemEntity;

public class TUserItemDao extends AGameBaseDao<TUserItemEntity> {

	private static final String TBL_NAME = "T_USER_ITEM";
	
	public TUserItemDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<TUserItemEntity> getDtoClass() {
		return TUserItemEntity.class;
	}

//	public TUserItemEntity selectByWeaponId(SQLiteDatabase pSqLiteDatabase, int weaponId) {
//		String where = "item_type = ? and item_object_id = ?";
//		String[] whereArgs = {String.valueOf(ItemType.WEAPON.getValue()), String.valueOf(weaponId)};
//		return queryOne(pSqLiteDatabase, where, whereArgs);
//	}
//	
//	public TUserItemEntity selectByAccessoryId(SQLiteDatabase pSqLiteDatabase, int accessoryId) {
//		String where = "item_type = ? and item_object_id = ?";
//		String[] whereArgs = {String.valueOf(ItemType.ACCESSORY.getValue()), String.valueOf(accessoryId)};
//		return queryOne(pSqLiteDatabase, where, whereArgs);
//	}
}