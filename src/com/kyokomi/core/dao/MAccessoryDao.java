package com.kyokomi.core.dao;

import com.kyokomi.core.entity.MAccessoryEntity;

public class MAccessoryDao extends AGameBaseDao<MAccessoryEntity> {

	private static final String TBL_NAME = "M_ACCESSORY";
	
	public MAccessoryDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<MAccessoryEntity> getDtoClass() {
		return MAccessoryEntity.class;
	}

}
