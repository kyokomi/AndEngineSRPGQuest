package com.kyokomi.srpgquest.sprite;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.util.color.Color;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.utils.ActorSpriteUtil;

/**
 * アクター
 * @author kyokomi
 *
 */
public class ActorBattleSprite extends Rectangle {
	/**
	 * アクター情報.
	 */
	private ActorPlayerDto mActorPlayer;
	public ActorPlayerDto getActorPlayer() {
		return mActorPlayer;
	}
	public void setActorPlayer(ActorPlayerDto pActorPlayer) {
		this.mActorPlayer = pActorPlayer;
	}
	public Integer getPlayerId() {
		return mActorPlayer.getPlayerId();
	}

	/** プレイヤーキャラクター. */
	public AnimatedSprite getPlayer() {
		return (AnimatedSprite) getChildByTag(getPlayerId());
	}
	
	private int lastMoveCurrentIndex;
	public int getLastMoveCurrentIndex() {
		return lastMoveCurrentIndex;
	}
	public void setLastMoveCurrentIndex(int lastMoveCurrentIndex) {
		this.lastMoveCurrentIndex = lastMoveCurrentIndex;
	}
	/**
	 * コンストラクタ.
	 * @param pActorPlayer
	 * @param baseScene
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param scale
	 * @param pVertexBufferObjectManager
	 */
	public ActorBattleSprite(ActorPlayerDto pActorPlayer, KeyListenScene baseScene, 
			float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY, pWidth, pHeight, baseScene.getBaseActivity().getVertexBufferObjectManager());
		
		this.mActorPlayer = pActorPlayer;
		
		setColor(Color.TRANSPARENT);
		
		playerSpriteInit(baseScene, pX, pY);
	}
	
	private void playerSpriteInit(KeyListenScene baseScene, float x, float y) {
		// playerキャラを追加 攻撃と防御のスプライトもセットで読み込んでおく
		AnimatedSprite player = ActorSpriteUtil.getBattleAnimatedSprite(baseScene, getActorPlayer().getImageResId());
		player.setTag(getPlayerId());
		player.setCurrentTileIndex(7);
		attachChild(player);

		// タグ設定
		setTag(mActorPlayer.getPlayerId());
	}

	// ----------------------------------------------
	// Sprite設定系
	// ----------------------------------------------
	@Override
	public void setPosition(IEntity pOtherEntity) {
		getPlayer().setPosition(pOtherEntity.getX(), pOtherEntity.getY());
	}
	@Override
	public void setPosition(float pX, float pY) {
		getPlayer().setPosition(pX, pY);
	}
	@Override
	public void setSize(float pWidth, float pHeight) {
		getPlayer().setSize(pWidth, pHeight);
	}
	
	/**
	 * 水平方向反転.
	 * @param pFlippedHorizontal
	 */
	public void setFlippedHorizontal(boolean pFlippedHorizontal) {
		getPlayer().setFlippedHorizontal(pFlippedHorizontal);
	}
	
	// ----------------------------------------------
	// 汎用モーション系
	// ----------------------------------------------
	public void attackAnimation() {
		getPlayer().animate(
				new long[]{100, 100, 100, 100}, 
				new int[]{6, 7, 8, 7}, 
				false);
	}

	// ----------------------------------------------
	// 汎用ポジション設定
	// ----------------------------------------------
//	public void setPlayerDirection(MoveDirectionType moveDirectionType) {
//		this.lastMoveCurrentIndex = moveDirectionType.getDirection();
//		ActorSpriteUtil.setPlayerDirection(getPlayer(), moveDirectionType);
//	}
//	public void setPlayerToDefaultPosition() {
//		ActorSpriteUtil.setPlayerToDefaultPosition(getPlayer(), this.lastMoveCurrentIndex);
//	}
//	
//	public void stopAnimation() {
//		getPlayer().stopAnimation(lastMoveCurrentIndex);
//	}
}