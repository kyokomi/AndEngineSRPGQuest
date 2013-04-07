package com.kyokomi.core.dto;

import com.kyokomi.core.constants.SceneType;

public class SaveDataDto {

	private Integer saveId;
	private Integer scenarioId;
	private Integer partyId;
	private Integer gold;
	private Integer exp;
	
	private Integer scenarioNo;
	private Integer seqNo;
	private SceneType sceneType;
	private Integer sceneId;
	private String scenarioTitle;
	
	public Integer getSaveId() {
		return saveId;
	}
	public void setSaveId(Integer saveId) {
		this.saveId = saveId;
	}
	public Integer getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(Integer scenarioId) {
		this.scenarioId = scenarioId;
	}
	public Integer getPartyId() {
		return partyId;
	}
	public void setPartyId(Integer partyId) {
		this.partyId = partyId;
	}
	public Integer getGold() {
		return gold;
	}
	public void setGold(Integer gold) {
		this.gold = gold;
	}
	public Integer getExp() {
		return exp;
	}
	public void setExp(Integer exp) {
		this.exp = exp;
	}
	public Integer getScenarioNo() {
		return scenarioNo;
	}
	public void setScenarioNo(Integer scenarioNo) {
		this.scenarioNo = scenarioNo;
	}
	public Integer getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}
	public SceneType getSceneType() {
		return sceneType;
	}
	public void setSceneType(SceneType sceneType) {
		this.sceneType = sceneType;
	}
	public Integer getSceneId() {
		return sceneId;
	}
	public void setSceneId(Integer sceneId) {
		this.sceneId = sceneId;
	}
	public String getScenarioTitle() {
		return scenarioTitle;
	}
	public void setScenarioTitle(String scenarioTitle) {
		this.scenarioTitle = scenarioTitle;
	}
}
