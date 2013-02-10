package com.kyokomi.core.dto;

import com.kyokomi.core.constants.SkillType;

public class ActorPlayerSkillDto {
	/** スキルID. */
	private Integer skillId;
	/** スキル画像ID. */
	private Integer skillImgResId;
	/** スキル名. */
	private String skillName;
	/** スキル詳細. */
	private String skillText;
	/** スキル区分. */
	private SkillType skillType;
	/** スキル値. */
	private Integer skillValue;
	public Integer getSkillId() {
		return skillId;
	}
	public void setSkillId(Integer skillId) {
		this.skillId = skillId;
	}
	public Integer getSkillImgResId() {
		return skillImgResId;
	}
	public void setSkillImgResId(Integer skillImgResId) {
		this.skillImgResId = skillImgResId;
	}
	public String getSkillName() {
		return skillName;
	}
	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}
	public String getSkillText() {
		return skillText;
	}
	public void setSkillText(String skillText) {
		this.skillText = skillText;
	}
	public SkillType getSkillType() {
		return skillType;
	}
	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}
	public Integer getSkillValue() {
		return skillValue;
	}
	public void setSkillValue(Integer skillValue) {
		this.skillValue = skillValue;
	}
}
