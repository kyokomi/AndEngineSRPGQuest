package com.kyokomi.core.dao;

import com.kyokomi.core.entity.TActorStatusEntity;

public class TActorStatusDao extends AGameBaseDao<TActorStatusEntity> {

	private static final String TBL_NAME = "T_ACTOR_STATUS";
	
	public TActorStatusDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<TActorStatusEntity> getDtoClass() {
		return TActorStatusEntity.class;
	}
}
