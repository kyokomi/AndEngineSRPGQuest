package com.kyokomi.core.dto;

import java.util.ArrayList;
import java.util.List;

import com.kyokomi.core.entity.MItemEntity;

public class MapBattleRewardDto {

	private int totalExp = 0;
	private int totalGold = 0;
	private List<MItemEntity> itemList = new ArrayList<MItemEntity>();
	public int getTotalExp() {
		return totalExp;
	}
	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
	}
	public int getTotalGold() {
		return totalGold;
	}
	public void setTotalGold(int totalGold) {
		this.totalGold = totalGold;
	}
	public List<MItemEntity> getItemList() {
		return itemList;
	}
	public void setItemList(List<MItemEntity> itemList) {
		this.itemList = itemList;
	}
	
	
}
