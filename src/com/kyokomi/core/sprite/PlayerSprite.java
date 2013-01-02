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
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import android.graphics.Typeface;

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
	
	/** Text用. */
	private Rectangle talkTextLayer;
	/** プレイヤー会話. */
	private TiledSprite playerTalk;
	/** 現在のスコアを表示するテキスト. */
	private Text talkText;
	
	private final float windowWidth;
	private final float windowHeight;
	
	public PlayerSprite(KeyListenScene baseScene, int x, int y) {
		windowWidth = baseScene.getWindowWidth();
		windowHeight = baseScene.getWindowHeight();
		
		// 透明な背景を作成
		layer = new Rectangle(
				x, y,
				baseScene.getWindowWidth(), baseScene.getWindowHeight(), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
		// 透明にする
		layer.setColor(Color.TRANSPARENT);
		
		// アイコンオブジェクトを追加（プレイヤーの下に表示）
		weapon = baseScene.getResourceAnimatedSprite("icon_set.png", 16, 48);
		weapon.setScale(2f);
		weapon.setAlpha(0.0f);
		layer.attachChild(weapon);
		
		// エフェクトオブジェクトを追加（武器の上に表示）
		attackEffect = baseScene.getResourceAnimatedSprite("effect002_b2.png", 5, 1);
		attackEffect.setAlpha(0.0f);
		attackEffect.setScale(0.5f);
		layer.attachChild(attackEffect);
		
		// playerキャラを追加
		player = baseScene.getResourceAnimatedSprite("actor110_0_s.png", 3, 4);
		layer.attachChild(player);
		// 攻撃と防御のスプライトも読み込んでおく
		playerDefense = baseScene.getResourceAnimatedSprite("actor110_2_s2.png", 3, 4);
		layer.attachChild(playerDefense);
		playerAttack = baseScene.getResourceAnimatedSprite("actor110_3_s2.png", 3, 4);
		layer.attachChild(playerAttack);

//		// カットイン
//		playerCutIn = baseScene.getResourceSprite("actor110_cutin_l2.jpg");
//		playerCutIn.setAlpha(0.0f);
//		layer.attachChild(playerCutIn);

		// 顔
		playerTalk = baseScene.getResourceTiledSprite("actor110_f.png", 4, 2);
//		playerTalk.setAlpha(0.0f);
		
		// 会話ウィンドウ
		talkTextLayer = new Rectangle(
				0, 0,
				baseScene.getWindowWidth(), 
				playerTalk.getHeight(), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
		talkTextLayer.setColor(Color.TRANSPARENT);
		layer.attachChild(talkTextLayer);
		
		Texture texture = new BitmapTextureAtlas(
				baseScene.getBaseActivity().getTextureManager(), 512, 512, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		Font font = new Font(baseScene.getBaseActivity().getFontManager(), 
				texture, Typeface.DEFAULT_BOLD, 22, true, Color.WHITE);
		// EngineのTextureManagerにフォントTextureを読み込み
		baseScene.getBaseActivity().getTextureManager().loadTexture(texture);
		baseScene.getBaseActivity().getFontManager().loadFont(font);
		
		talkText = new Text(20, 20, font, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 
				new TextOptions(HorizontalAlign.LEFT), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
		talkText.setColor(Color.TRANSPARENT);
		talkTextLayer.setAlpha(0.0f);
		playerTalk.setAlpha(0.0f);
		
		talkTextLayer.setZIndex(1);
		playerTalk.setZIndex(2);
		talkTextLayer.attachChild(talkText);
		talkTextLayer.attachChild(playerTalk);
		talkTextLayer.sortChildren();
		
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
	
	public void talk(float y, String text) {
		playerTalk.setCurrentTileIndex(0);
		playerTalk.setAlpha(1.0f);
		playerTalk.setPosition(0, 0);
		talkTextLayer.setPosition(0, y);
		talkTextLayer.setAlpha(1.0f);
		
		// TODO: text作成
		talkText.setColor(Color.WHITE);
		talkText.setPosition(playerTalk.getWidth(), 0);
		talkText.setText(text);
	}
	
	public Rectangle getTalkTextLayer() {
		return talkTextLayer;
	}
	
	/**
	 * 攻撃モーション再生.
	 */
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
}