package com.kyokomi.core.dao.sys;

import com.kyokomi.core.dao.AGameBaseDao;
import com.kyokomi.core.entity.sys.SysImageEntity;

public class SysImageDao extends AGameBaseDao<SysImageEntity> {

	private static final String TBL_NAME = "SYS_IMAGE";
	
	public SysImageDao() {
		
	}
	
	@Override
	protected String getDatabaseTableName() {
		return TBL_NAME;
	}

	@Override
	protected Class<SysImageEntity> getDtoClass() {
		return SysImageEntity.class;
	}

}
