package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

import com.kyokomi.core.constants.SceneType;

/**
 * シナリオマスタ.
 * @author kyokomi
 *
 */
public class MScenarioEntity implements IDatabaseEntity {
	private Integer scenarioId;
	private Integer scenarioNo;
	private Integer seqNo;
	private Integer sceneType;
	private Integer sceneId;
	private String scenarioTitle; // TODO: 正規化対象
	private String backImgFilePath; // TODO: imageIdにして管理したいね
	
	public MScenarioEntity() {
		
	}
	public MScenarioEntity(Cursor pCursor) {
		initCursor(pCursor);
	}
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.scenarioId    = pCursor.getInt(count); count++;
		this.scenarioNo    = pCursor.getInt(count); count++;
		this.seqNo         = pCursor.getInt(count); count++;
		this.sceneType     = pCursor.getInt(count); count++;
		this.sceneId       = pCursor.getInt(count); count++;
		this.scenarioTitle = pCursor.getString(count); count++;
		this.backImgFilePath = pCursor.getString(count); count++;
	}
	public Integer getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(Integer scenarioId) {
		this.scenarioId = scenarioId;
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
	
	public String getBackImgFilePath() {
		return backImgFilePath;
	}
	public void setBackImgFilePath(String backImgFilePath) {
		this.backImgFilePath = backImgFilePath;
	}
	@Override
	public ContentValues createContentValues() {
		// マスターはupdateもinsertもしないから不要
		return null;
	}
	@Override
	public int getId() {
		return getScenarioId();
	}
}
