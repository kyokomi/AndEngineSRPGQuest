package com.kyokomi.srpgquest.map.item;

public class ActorPlayerMapItem extends MapItem {

	/** プレイヤーを一意に識別するID. */
	private Integer playerId;
	
	/** 移動済みフラグ. */
	private boolean moveDone;
	/** 攻撃済みフラグ. */
	private boolean attackDone;
	
	/**
	 * @return the playerId
	 */
	public Integer getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	/**
	 * @return the moveDone
	 */
	public boolean isMoveDone() {
		return moveDone;
	}

	/**
	 * @param moveDone the moveDone to set
	 */
	public void setMoveDone(boolean moveDone) {
		this.moveDone = moveDone;
	}

	/**
	 * @return the attackDone
	 */
	public boolean isAttackDone() {
		return attackDone;
	}

	/**
	 * @param attackDone the attackDone to set
	 */
	public void setAttackDone(boolean attackDone) {
		this.attackDone = attackDone;
	}
}
