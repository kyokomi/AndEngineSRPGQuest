package com.kyokomi.core.dao;

import com.kyokomi.core.entity.TPlayerPartyEntity;

public class TPlayerPartyDao extends AGameBaseDao<TPlayerPartyEntity> {
	
	private static final String TBL_NAME = "M_PLAYER_PARTY";
	
	public TPlayerPartyDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<TPlayerPartyEntity> getDtoClass() {
		return TPlayerPartyEntity.class;
	}
}
