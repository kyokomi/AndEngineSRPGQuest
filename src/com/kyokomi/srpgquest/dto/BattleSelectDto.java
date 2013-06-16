package com.kyokomi.srpgquest.dto;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.srpgquest.constant.BattleActorType;
import com.kyokomi.srpgquest.constant.BattleMenuType;

public class BattleSelectDto {

	private boolean isAction;
	
	private BattleActorType battleActorType;
	private ActorPlayerDto actorPlayerDto;
	private ActorPlayerDto targetDto;
	
	private BattleMenuType battleMenuType;

	public boolean isAction() {
		return isAction;
	}

	public void setAction(boolean isAction) {
		this.isAction = isAction;
	}

	public BattleActorType getBattleActorType() {
		return battleActorType;
	}

	public void setBattleActorType(BattleActorType battleActorType) {
		this.battleActorType = battleActorType;
	}

	public ActorPlayerDto getActorPlayerDto() {
		return actorPlayerDto;
	}

	public void setActorPlayerDto(ActorPlayerDto actorPlayerDto) {
		this.actorPlayerDto = actorPlayerDto;
	}

	public BattleMenuType getBattleMenuType() {
		return battleMenuType;
	}

	public void setBattleMenuType(BattleMenuType battleMenuType) {
		this.battleMenuType = battleMenuType;
	}

	public ActorPlayerDto getTargetDto() {
		return targetDto;
	}

	public void setTargetDto(ActorPlayerDto targetDto) {
		this.targetDto = targetDto;
	}
}
