package com.kyokomi.core.logic;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.core.constants.SkillType;
import com.kyokomi.core.dao.MActorDao;
import com.kyokomi.core.dao.TActorStatusDao;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.ActorPlayerEquipDto;
import com.kyokomi.core.dto.ActorPlayerSkillDto;
import com.kyokomi.core.entity.MActorEntity;
import com.kyokomi.core.entity.TActorStatusEntity;
import com.kyokomi.core.scene.KeyListenScene;

public class ActorPlayerLogic {

	private MActorDao mActorDao;
	private TActorStatusDao tActorStatusDao;
	
	public ActorPlayerLogic() {
		mActorDao = new MActorDao();
		tActorStatusDao = new TActorStatusDao();
	}
	public ActorPlayerDto createActorPlayerDto(KeyListenScene pBaseScene, int playerId) {
		ActorPlayerDto actorPlayer = new ActorPlayerDto();
		actorPlayer.setPlayerId(playerId);
		// ---------------------------------------------
		// 基本ステータス
		// ---------------------------------------------
		
		MActorEntity mActorEntity = mActorDao.selectById(
				pBaseScene.getBaseActivity().getDB(), playerId);
		if (mActorEntity == null) {
			pBaseScene.getBaseActivity().closeDB();
			throw new RuntimeException("マスタエラー id=" + playerId);
		}
		TActorStatusEntity tActorStatusEntity = tActorStatusDao.selectById(
				pBaseScene.getBaseActivity().getDB(), playerId);
		pBaseScene.getBaseActivity().closeDB();
		
		actorPlayer.setName(mActorEntity.getActorName());
		actorPlayer.setImageResId(mActorEntity.getImageResId());
		actorPlayer.setLv(tActorStatusEntity.getLevel());
		actorPlayer.setExp(tActorStatusEntity.getExp());
		
		actorPlayer.setMovePoint(tActorStatusEntity.getMovePoint());
		actorPlayer.setAttackRange(tActorStatusEntity.getAttackRange());
		
		actorPlayer.setHitPoint(tActorStatusEntity.getHitPoint());
		actorPlayer.setHitPointLimit(tActorStatusEntity.getHitPoint());
		actorPlayer.setAttackPoint(tActorStatusEntity.getAttackPoint());
		actorPlayer.setDefencePoint(tActorStatusEntity.getDefencePoint());
		
		// ---------------------------------------------
		// 装備
		// ---------------------------------------------
		ItemLogic itemLogic = new ItemLogic();
		ActorPlayerEquipDto equipDto = itemLogic.createActorPlayerEquipDto(pBaseScene, 
				tActorStatusEntity.getWeaponId(), 
				tActorStatusEntity.getAccessoryId());
		
		// TODO: DBとかから取得
//		ActorPlayerEquipDto equipDto = new ActorPlayerEquipDto();
//		// 武器
//		equipDto.setWeaponName("レイピア");
//		equipDto.setWeaponImgResId(3);
//		equipDto.setWeaponStr(10);
//		// アクセサリー
//		equipDto.setAccessoryName("普通の指輪");
//		equipDto.setAccessoryImgResId(33);
//		equipDto.setAccessoryDef(5);
		
		actorPlayer.setEquipDto(equipDto);
		
		// ---------------------------------------------
		// スキル
		// ---------------------------------------------
		// TODO: DBとかから取得
		List<ActorPlayerSkillDto> skillDtos = new ArrayList<ActorPlayerSkillDto>();
		ActorPlayerSkillDto skillDto = new ActorPlayerSkillDto();
		skillDto.setSkillId(1);
		skillDto.setSkillImgResId(489);
		skillDto.setSkillName("流星剣");
		skillDto.setSkillText("5連続の剣攻撃");
		skillDto.setSkillType(SkillType.ATTACK_COUNT);
		skillDto.setSkillValue(5);
		skillDtos.add(skillDto);
		
		actorPlayer.setSkillDtoList(skillDtos);
		
		return actorPlayer;
	}
}
