package com.kyokomi.core.dto;

/**
 * 装備情報.
 * @author kyokomi
 *
 */
public class EquipDto {

	private Integer playerId;
	// 武器
	private String weaponName;
	private Integer weaponImgResId;
	// アクセサリー
	private String accessoryName;
	private Integer accessoryImgResId;
	
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
	 * @return the weaponName
	 */
	public String getWeaponName() {
		return weaponName;
	}
	/**
	 * @param weaponName the weaponName to set
	 */
	public void setWeaponName(String weaponName) {
		this.weaponName = weaponName;
	}
	/**
	 * @return the weaponImgResId
	 */
	public Integer getWeaponImgResId() {
		return weaponImgResId;
	}
	/**
	 * @param weaponImgResId the weaponImgResId to set
	 */
	public void setWeaponImgResId(Integer weaponImgResId) {
		this.weaponImgResId = weaponImgResId;
	}
	/**
	 * @return the accessoryName
	 */
	public String getAccessoryName() {
		return accessoryName;
	}
	/**
	 * @param accessoryName the accessoryName to set
	 */
	public void setAccessoryName(String accessoryName) {
		this.accessoryName = accessoryName;
	}
	/**
	 * @return the accessoryImgResId
	 */
	public Integer getAccessoryImgResId() {
		return accessoryImgResId;
	}
	/**
	 * @param accessoryImgResId the accessoryImgResId to set
	 */
	public void setAccessoryImgResId(Integer accessoryImgResId) {
		this.accessoryImgResId = accessoryImgResId;
	}
}
