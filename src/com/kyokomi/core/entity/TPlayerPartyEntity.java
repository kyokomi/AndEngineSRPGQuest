package com.kyokomi.core.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 	_ID INTEGER NOT NULL,
	PARTY_ID INTEGER NOT NULL,
	ACTOR_ID INTEGER NOT NULL,
 * @author kyokomi
 *
 */
public class TPlayerPartyEntity implements IDatabaseEntity {

	private Integer partyMemberId;
	private Integer partyId;
	private Integer actorId;
	
	@Override
	public void initCursor(Cursor pCursor) {
		int count = 0;
		this.partyMemberId = pCursor.getInt(count); count++;
		this.partyId = pCursor.getInt(count); count++;
		this.actorId = pCursor.getInt(count); count++;
	}

	@Override
	public ContentValues createContentValues() {
		return null;
	}

	@Override
	public int getId() {
		return this.partyMemberId;
	}

	public Integer getPartyMemberId() {
		return partyMemberId;
	}
	public void setPartyMemberId(Integer partyMemberId) {
		this.partyMemberId = partyMemberId;
	}
	public Integer getPartyId() {
		return partyId;
	}
	public void setPartyId(Integer partyId) {
		this.partyId = partyId;
	}
	public Integer getActorId() {
		return actorId;
	}
	public void setActorId(Integer actorId) {
		this.actorId = actorId;
	}
}
