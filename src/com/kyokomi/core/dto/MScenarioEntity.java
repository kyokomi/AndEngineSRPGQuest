package com.kyokomi.core.dto;

import android.database.Cursor;

import com.kyokomi.core.constants.SceneType;

/**
 * シナリオマスタ.
 * @author kyokomi
 *
 */
public class MScenarioEntity implements ADatabaseEntity {
	private Integer id;
	private Integer scenarioNo;
	private Integer seqNo;
	private Integer sceneType;
	private Integer sceneId;
	private String scenarioTitle; // TODO: 正規化対象
	
	public MScenarioEntity() {
		
	}
	public MScenarioEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	@Override
	public void initCursor(Cursor pCursor) {
		this.id            = pCursor.getInt(0);
		this.scenarioNo    = pCursor.getInt(1);
		this.seqNo         = pCursor.getInt(2);
		this.sceneType     = pCursor.getInt(3);
		this.sceneId       = pCursor.getInt(4);
		this.scenarioTitle = pCursor.getString(5);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getScenarioNo() {
		return scenarioNo;
	}
	public void setScenarioNo(Integer scenarioNo) {
		this.scenarioNo = scenarioNo;
	}
	public String getScenarioTitle() {
		return scenarioTitle;
	}
	public void setScenarioTitle(String scenarioTitle) {
		this.scenarioTitle = scenarioTitle;
	}
	public Integer getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}
	public SceneType getSceneType() {
		return SceneType.get(sceneType);
	}
	public void setSceneType(Integer sceneType) {
		this.sceneType = sceneType;
	}
	public void setSceneType(SceneType sceneType) {
		this.sceneType = sceneType.getValue();
	}
	public Integer getSceneId() {
		return sceneId;
	}
	public void setSceneId(Integer sceneId) {
		this.sceneId = sceneId;
	}
}
