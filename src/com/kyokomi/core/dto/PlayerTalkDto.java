package com.kyokomi.core.dto;

/**
 * プレイヤー会話内容DTO.
 * @author kyokomi
 *
 */
public class PlayerTalkDto {
	
	public enum TalkDirection {
		TALK_DIRECT_LEFT,
		TALK_DIRECT_RIGHT,
	}
	
	private Integer playerId;
	private Integer currentTileIndex;
	private TalkDirection talkDirection;
	private String talk;
	
	public PlayerTalkDto(Integer playerId, Integer currentTileIndex, TalkDirection talkDirection, String talk) {
		this.playerId = playerId;
		this.currentTileIndex = currentTileIndex;
		this.talkDirection = talkDirection;
		this.talk = talk;
	}
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
	 * @return the currentTileIndex
	 */
	public Integer getCurrentTileIndex() {
		return currentTileIndex;
	}
	/**
	 * @param currentTileIndex the currentTileIndex to set
	 */
	public void setCurrentTileIndex(Integer currentTileIndex) {
		this.currentTileIndex = currentTileIndex;
	}
	/**
	 * @return the talkDirection
	 */
	public TalkDirection getTalkDirection() {
		return talkDirection;
	}
	/**
	 * @param talkDirection the talkDirection to set
	 */
	public void setTalkDirection(TalkDirection talkDirection) {
		this.talkDirection = talkDirection;
	}
	/**
	 * @return the talk
	 */
	public String getTalk() {
		return talk;
	}
	/**
	 * @param talk the talk to set
	 */
	public void setTalk(String talk) {
		this.talk = talk;
	}
}