package com.kyokomi.core.dao;

import com.kyokomi.core.entity.MWeaponEntity;

public class MWeaponDao extends AGameBaseDao<MWeaponEntity> {
	
	private static final String TBL_NAME = "M_WEAPON";
	
	public MWeaponDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<MWeaponEntity> getDtoClass() {
		return MWeaponEntity.class;
	}
}
