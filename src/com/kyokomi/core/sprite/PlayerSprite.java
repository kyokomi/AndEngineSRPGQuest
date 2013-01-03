package com.kyokomi.core.sprite;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
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
	/** プレイヤーカットイン. */
	private Sprite playerCutIn;

	/** プレイヤーキャラクター. */
	private AnimatedSprite player;
	/** プレイヤーキャラクター(回避). */
	private AnimatedSprite playerDefense;
	/** プレイヤーキャラクター(攻撃). */
	private AnimatedSprite playerAttack;
	
	/** プレイヤー会話. */
	private TiledSprite playerFace;
	
	public PlayerSprite(KeyListenScene baseScene, int imageId, int tag) {
		playerSpriteInit(baseScene, imageId, tag, 0, 0, 1.0f);
	}
	public PlayerSprite(KeyListenScene baseScene, int imageId, int tag, float x, float y, float scale) {
		playerSpriteInit(baseScene, imageId, tag, x, y, scale);
	}
	private void playerSpriteInit(KeyListenScene baseScene, int imageId, int tag, float x, float y, float scale) {
		
		// 透明な背景を作成
		layer = new Rectangle(
				x, y,
				baseScene.getWindowWidth(), baseScene.getWindowHeight(), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
		// 透明にする
		layer.setColor(Color.TRANSPARENT);
		
		// playerキャラを追加 攻撃と防御のスプライトもセットで読み込んでおく
		String baseFileName = "actor" + imageId;
		player        = baseScene.getResourceAnimatedSprite(baseFileName + "_0_s.png", 3, 4);
		playerDefense = baseScene.getResourceAnimatedSprite(baseFileName + "_2_s.png", 3, 4);
		playerAttack  = baseScene.getResourceAnimatedSprite(baseFileName + "_3_s.png", 3, 4);
		playerCutIn   = baseScene.getResourceSprite(baseFileName + "_cutin_l.jpg");
		playerFace    = baseScene.getResourceTiledSprite(baseFileName + "_f.png", 4, 2);
		
		layer.attachChild(player);
		layer.attachChild(playerDefense);
		layer.attachChild(playerAttack);

		// カットイン
		playerCutIn.setAlpha(0.0f);
		layer.attachChild(playerCutIn);

		// 顔
		playerFace.setTag(tag);
		playerFace.setAlpha(0.0f);
		
		// デフォルト表示
		showPlayer(PlayerSpriteType.PLAYER_TYPE_DEFENSE);
		setPlayerScale(scale);
		setPlayerPosition(80, 200);
		playerDefense.setCurrentTileIndex(0);
		
		// 武器設定
		initWeaponAndEffect(baseScene, scale);
	}

	/**
	 * 武器初期化.
	 * @param baseScene
	 * @param scale
	 */
	public void initWeaponAndEffect(KeyListenScene baseScene, float scale) {
		// アイコンオブジェクトを追加（プレイヤーの下に表示）
		weapon = baseScene.getResourceAnimatedSprite("icon_set.png", 16, 48);
		weapon.setScale(scale);
		weapon.setAlpha(0.0f);
		layer.attachChild(weapon);
		
		// エフェクトオブジェクトを追加（武器の上に表示）
		attackEffect = baseScene.getResourceAnimatedSprite("effect002_b2.png", 5, 1);
		attackEffect.setAlpha(0.0f);
		attackEffect.setScale(scale / 4);
		layer.attachChild(attackEffect);
	}

	public Rectangle getLayer() {
		return layer;
	}
	
	public void setPlayerScale(float scale) {
		player.setScale(scale);
		playerDefense.setScale(scale);
		playerAttack.setScale(scale);
	}
	public void setPlayerPosition(Sprite sprite) {
		player.setPosition(sprite.getX(), sprite.getY());
		playerDefense.setPosition(sprite.getX(), sprite.getY());
		playerAttack.setPosition(sprite.getX(), sprite.getY());
	}
	public void setPlayerPosition(float x, float y) {
		player.setPosition(x, y);
		playerDefense.setPosition(x, y);
		playerAttack.setPosition(x, y);
	}
	public void setPlayerSize(float w, float h) {
		player.setSize(w, h);
		playerDefense.setSize(w, h);
		playerAttack.setSize(w, h);
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
	
	public TiledSprite getPlayerTalk() {
		return playerFace;
	}

	/**
	 * 攻撃モーション再生(Ver.2).
	 * スケールとか解像度関係なくやれるようにする
	 */
	public void attack2() {
		
		showPlayer(PlayerSpriteType.PLAYER_TYPE_ATTACK);
		
		// 攻撃
		setPlayerToAttackPosition();
		playerAttack.setFlippedHorizontal(true);
		playerAttack.registerEntityModifier(new ParallelEntityModifier(
			// 武器
			new DelayModifier(0.3f, new IEntityModifier.IEntityModifierListener() {
				@Override public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setCurrentTileIndex(16 * 16 + 5);
					weapon.setRotation(0f);
					weapon.setPosition(
							playerAttack.getX() - (playerAttack.getWidth() / 2) + (weapon.getWidth() / 4), 
							playerAttack.getY() + (playerAttack.getHeight() / 4));
					weapon.setAlpha(1.0f);
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					weapon.setRotation(100f);
					weapon.setPosition(
							playerAttack.getX() + (playerAttack.getWidth() / 2) + (int)(weapon.getWidth() / 1.25),
							playerAttack.getY() - (playerAttack.getHeight() / 4) + (weapon.getHeight() / 4));
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
					attackEffect.setPosition(
							playerAttack.getX() - (playerAttack.getWidth() / 2),
							playerAttack.getY() - (attackEffect.getHeight() / 3));
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
	/**
	 * 攻撃モーション再生.
	 */
	public void attack() {
		
		showPlayer(PlayerSpriteType.PLAYER_TYPE_ATTACK);
		
		// 攻撃
		setPlayerToAttackPosition();
		playerAttack.setFlippedHorizontal(true);
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
	
	/**
	 * プレイヤー攻撃ポジション設定.
	 */
	public void setPlayerToAttackPosition() {
		playerAttack.animate(
				new long[]{100, 100, 100}, 
				new int[]{6, 7, 8}, 
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
}