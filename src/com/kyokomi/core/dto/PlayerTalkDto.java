package com.kyokomi.core.dto;

/**
 * プレイヤー会話内容DTO.
 * @author kyokomi
 *
 */
public class PlayerTalkDto {
	
	public enum TalkDirection {
		TALK_DIRECT_LEFT(1),
		TALK_DIRECT_RIGHT(2),
		;
		private Integer value;
		
		private TalkDirection(Integer value) {
			this.value = value;
		}
		public static TalkDirection get(Integer value) {
			TalkDirection[] values = values();
			for (TalkDirection type : values) {
				if (type.getValue() == value) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type.");
		}
		public Integer getValue() {
			return value;
		}
	}
	
	private Integer playerId;
	private String name;
	private Integer currentTileIndex;
	private TalkDirection talkDirection;
	private String talk;
	
	public PlayerTalkDto(Integer playerId, String name, Integer currentTileIndex, TalkDirection talkDirection, String talk) {
		this.playerId = playerId;
		this.name = name;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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