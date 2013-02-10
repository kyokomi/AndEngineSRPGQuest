package com.kyokomi.core.logic;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.core.constants.SkillType;
import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.ActorPlayerEquipDto;
import com.kyokomi.core.dto.ActorPlayerSkillDto;

public class ActorPlayerLogic {

	public ActorPlayerDto createActorPlayerDto(int playerId) {
		ActorPlayerDto actorPlayer = new ActorPlayerDto();
		actorPlayer.setPlayerId(playerId);
		// ---------------------------------------------
		// 基本ステータス
		// ---------------------------------------------
		// TODO: DBとかから取得
		if (playerId == 1) {
			actorPlayer.setName("アスリーン");
			actorPlayer.setImageResId(110);
			actorPlayer.setLv(2);
			actorPlayer.setExp(10);
			
			actorPlayer.setMovePoint(6);
			actorPlayer.setAttackRange(1);
			
			actorPlayer.setHitPoint(100);
			actorPlayer.setHitPointLimit(100);
			actorPlayer.setAttackPoint(60);
			actorPlayer.setDefencePoint(30);
		} else {
			actorPlayer.setName("ラーティ・クルス");
			actorPlayer.setImageResId(34);
			actorPlayer.setLv(1);
			actorPlayer.setExp(10);
			
			actorPlayer.setMovePoint(5);
			actorPlayer.setAttackRange(1);
			
			actorPlayer.setHitPoint(100);
			actorPlayer.setHitPointLimit(100);
			actorPlayer.setAttackPoint(40);
			actorPlayer.setDefencePoint(10);
		}
		
		// ---------------------------------------------
		// 装備
		// ---------------------------------------------
		// TODO: DBとかから取得
		ActorPlayerEquipDto equipDto = new ActorPlayerEquipDto();
		// 武器
		equipDto.setWeaponName("レイピア");
		equipDto.setWeaponImgResId(3);
		equipDto.setWeaponStr(10);
		// アクセサリー
		equipDto.setAccessoryName("普通の指輪");
		equipDto.setAccessoryImgResId(33);
		equipDto.setAccessoryDef(5);
		
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
