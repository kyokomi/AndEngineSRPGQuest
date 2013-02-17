package com.kyokomi.core.dao;

import com.kyokomi.core.entity.TSaveDataEntity;

public class TSaveDataDao extends AGameBaseDao<TSaveDataEntity> {

	private static final String TBL_NAME = "T_SAVE_DATA";
	
	public TSaveDataDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<TSaveDataEntity> getDtoClass() {
		return TSaveDataEntity.class;
	}
}
