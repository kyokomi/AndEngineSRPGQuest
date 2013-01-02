package com.kyokomi.core.sprite;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.kyokomi.core.constants.PlayerSpriteType;
import com.kyokomi.core.scene.KeyListenScene;

public class PlayerSprite {
	
	/** ベースレイヤー. */
	private Rectangle layer;
	
	/** 武器（アイコンセット）. */
	private AnimatedSprite weapon;
	/** 攻撃エフェクト. */
	private AnimatedSprite attackEffect;
	
	/** プレイヤーキャラクター. */
	private AnimatedSprite player;
	/** プレイヤーキャラクター(回避). */
	private AnimatedSprite playerDefense;
	/** プレイヤーキャラクター(攻撃). */
	private AnimatedSprite playerAttack;
	
	public PlayerSprite(KeyListenScene baseSence, int x, int y) {
		
		// 透明な背景を作成
		layer = new Rectangle(
				x, y,
				baseSence.getWindowWidth(), baseSence.getWindowHeight(), 
				baseSence.getBaseActivity().getVertexBufferObjectManager());
		// 透明にする
		layer.setColor(Color.TRANSPARENT);
		
		// アイコンオブジェクトを追加（プレイヤーの下に表示）
		weapon = baseSence.getResourceAnimatedSprite("icon_set.png", 16, 48);
		weapon.setScale(2f);
		weapon.setAlpha(0.0f);
		layer.attachChild(weapon);
		
		// エフェクトオブジェクトを追加（武器の上に表示）
		attackEffect = baseSence.getResourceAnimatedSprite("effect002_b2.png", 5, 1);
		attackEffect.setAlpha(0.0f);
		attackEffect.setScale(0.5f);
		layer.attachChild(attackEffect);
		
		// playerキャラを追加
		player = baseSence.getResourceAnimatedSprite("actor110_0_s.png", 3, 4);
		layer.attachChild(player);
		// 攻撃と防御のスプライトも読み込んでおく
		playerDefense = baseSence.getResourceAnimatedSprite("actor110_2_s2.png", 3, 4);
		layer.attachChild(playerDefense);
		playerAttack = baseSence.getResourceAnimatedSprite("actor110_3_s2.png", 3, 4);
		layer.attachChild(playerAttack);

		showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
		setPlayerScale(2f);
		setPlayerPosition(80, 400);
		playerDefense.setCurrentTileIndex(0);
	}

	public Rectangle getLayer() {
		return layer;
	}
	
	public void setPlayerScale(float scale) {
		player.setScale(scale);
		playerDefense.setScale(scale);
		playerAttack.setScale(scale);
	}
	public void setPlayerPosition(float x, float y) {
		player.setPosition(x, y);
		playerDefense.setPosition(x, y);
		playerAttack.setPosition(x, y);
	}
	
	public void showPlayer(PlayerSpriteType playerType) {
		float normal = 0.0f;
		float attack = 0.0f;
		float defense = 0.0f;
		
		switch (playerType) {
		case PLAYER_TYPE_NORMAL:
			normal = 1.0f;
			break;
		case PLAYER_TYPE_ATTACK:
			attack = 1.0f;
			break;
		case PLAYER_TYPE_DEFENSE:
			defense = 1.0f;
			break;
		case PLAYER_TYPE_HIDE:
			break;
		default:
			break;
		}
		player.setAlpha(normal);
		playerAttack.setAlpha(attack);
		playerDefense.setAlpha(defense);
	}
	
	/**
	 * プレイヤーデフォルトポジション設定.
	 */
	public void setPlayerToDefaultPosition() {
		player.animate(
				new long[]{100, 100, 100}, 
				new int[]{6, 7, 8}, 
				true);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_NORMAL);
	}
	
	/**
	 * 武器ポジション設定.
	 */
	public void setWeaponPosition() {
		
	}
	
	/**
	 * プレイヤー攻撃ポジション設定.
	 */
	public void setPlayerToAttackPosition() {
		playerAttack.animate(
				new long[]{100, 100, 100}, 
				new int[]{8, 7, 6}, 
				false);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_ATTACK);
		
		setPlayerPosition(playerAttack.getX(), playerAttack.getY());
	}
	
	/**
	 * プレイヤージャンプポジション設定.
	 */
	public void setPlayerToJumpPositon() {
		playerDefense.animate(
				new long[]{200, 300}, 
				new int[]{0, 3}, 
				false);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
		
		// 上に飛ぶ感じのアニメーション
		playerDefense.registerEntityModifier(new SequenceEntityModifier(
			new MoveModifier(0.2f, 
					playerDefense.getX(), playerDefense.getX(), 
					playerDefense.getY(), playerDefense.getY() - 100),
			new DelayModifier(0.1f),
			new MoveModifier(0.2f, 
					playerDefense.getX(), playerDefense.getX(), 
					playerDefense.getY() - 100, playerDefense.getY())) {
				@Override
				public void onModifierFinished(
						IModifier<IEntity> pModifier, IEntity pItem) {
					super.onModifierFinished(pModifier, pItem);
					setPlayerPosition(playerDefense.getX(), playerDefense.getY());
				}
			});
	}
				
	/**
	 * プレイヤースライディングポジション設定.
	 */
	public void setPlayerToSlidePositon() {
		playerDefense.animate(
				new long[]{200, 100}, 
				new int[]{0, 11}, 
				false);
		showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
	}

	public void attack() {
		
		showPlayer(PlayerSpriteType.PLAYER_TYPE_ATTACK);
		
		// 攻撃
		setPlayerToAttackPosition();

		playerAttack.registerEntityModifier(new ParallelEntityModifier(
			// 武器
			new DelayModifier(0.3f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setCurrentTileIndex(16 * 16 + 5);
					weapon.setRotation(0f);
					weapon.setPosition(playerAttack.getX() - 30, playerAttack.getY());
					weapon.setAlpha(1.0f);
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setRotation(100f);
					weapon.setPosition(playerAttack.getX() + 50, playerAttack.getY() - 10);
				}
			}),
			// エフェクト
			new DelayModifier(0.2f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					attackEffect.setCurrentTileIndex(0);
					attackEffect.setAlpha(1.0f);
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					attackEffect.animate(
							new long[]{100, 100, 100}, 
							new int[]{4, 3, 2}, 
							false);
					attackEffect.setPosition(playerAttack.getX(), playerAttack.getY() - 70);
				}
			}),
			// 全体
			new DelayModifier(0.5f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setAlpha(0.0f);
					attackEffect.setAlpha(0.0f);
					// デフォルトに戻す
					showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
					playerDefense.setCurrentTileIndex(0);
				}
			})
		));
	}
}