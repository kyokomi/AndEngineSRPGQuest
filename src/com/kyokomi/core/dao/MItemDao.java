package com.kyokomi.core.dao;

import android.database.sqlite.SQLiteDatabase;

import com.kyokomi.core.constants.ItemType;
import com.kyokomi.core.entity.MItemEntity;

public class MItemDao extends AGameBaseDao<MItemEntity> {

private static final String TBL_NAME = "M_ITEM";
	
	public MItemDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<MItemEntity> getDtoClass() {
		return MItemEntity.class;
	}

	public MItemEntity selectByWeaponId(SQLiteDatabase pSqLiteDatabase, int weaponId) {
		String where = "item_type = ? and item_object_id = ?";
		String[] whereArgs = {String.valueOf(ItemType.WEAPON.getValue()), String.valueOf(weaponId)};
		return queryOne(pSqLiteDatabase, where, whereArgs);
	}
	
	public MItemEntity selectByAccessoryId(SQLiteDatabase pSqLiteDatabase, int accessoryId) {
		String where = "item_type = ? and item_object_id = ?";
		String[] whereArgs = {String.valueOf(ItemType.ACCESSORY.getValue()), String.valueOf(accessoryId)};
		return queryOne(pSqLiteDatabase, where, whereArgs);
	}
}