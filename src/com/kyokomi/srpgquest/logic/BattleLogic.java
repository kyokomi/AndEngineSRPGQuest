package com.kyokomi.srpgquest.logic;

import com.kyokomi.srpgquest.actor.ActorPlayer;

import android.util.Log;

/**
 * バトルによるダメージ計算などの汎用ロジック.
 * @author kyokomi
 *
 */
public class BattleLogic {
	private final static String TAG = "BattleLogic";

	public void attack(ActorPlayer from, ActorPlayer to) {
		Log.d(TAG, "-------------- FROM STATUS -------------- ");
		Log.d(TAG, "attack  : " + from.getAttackPoint());
		Log.d(TAG, "defence : " + from.getDefencePoint());
		Log.d(TAG, "hp      : " + from.getHitPoint());
		Log.d(TAG, "-------------- TO STATUS -------------- ");
		Log.d(TAG, "attack  : " + to.getAttackPoint());
		Log.d(TAG, "defence : " + to.getDefencePoint());
		Log.d(TAG, "hp      : " + to.getHitPoint());
		Log.d(TAG, "-------------- BATTLE START -------------- ");
		
		// ここでの計算は、乱数などは使わない
		// ダメージ = 攻撃力 * (100 - 防御力(%))
		int damege = from.getAttackPoint() * (100 - to.getDefencePoint()) / 100;
		Log.d(TAG, "Damege : " + damege);
		
		// オーバーキル判定
		if (damege > to.getHitPoint()) {
			damege = to.getHitPoint();
		}
		to.setHitPoint(to.getHitPoint() - damege);
		Log.d(TAG, "to beforeHP : " + to.getHitPoint());
		
	}
}
