package com.kyokomi.core.dao;

import com.kyokomi.core.entity.MActorEntity;

public class MActorDao extends AGameBaseDao<MActorEntity> {

	private static final String TBL_NAME = "M_ACTOR";
	
	public MActorDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<MActorEntity> getDtoClass() {
		return MActorEntity.class;
	}
}
