package com.kyokomi.core.dto;

/**
 * 装備情報.
 * @author kyokomi
 *
 */
public class ActorPlayerEquipDto {

	// 武器
	private String weaponName;
	private Integer weaponImgResId;
	private Integer weaponStr;
	// アクセサリー
	private String accessoryName;
	private Integer accessoryImgResId;
	private Integer accessoryDef;
	
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
	/**
	 * @return the weaponStr
	 */
	public Integer getWeaponStr() {
		return weaponStr;
	}
	/**
	 * @param weaponStr the weaponStr to set
	 */
	public void setWeaponStr(Integer weaponStr) {
		this.weaponStr = weaponStr;
	}
	/**
	 * @return the accessoryDef
	 */
	public Integer getAccessoryDef() {
		return accessoryDef;
	}
	/**
	 * @param accessoryDef the accessoryDef to set
	 */
	public void setAccessoryDef(Integer accessoryDef) {
		this.accessoryDef = accessoryDef;
	}
}
