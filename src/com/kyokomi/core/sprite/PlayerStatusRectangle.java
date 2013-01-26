package com.kyokomi.core.sprite;

import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.scene.KeyListenScene;

public class PlayerStatusRectangle extends Rectangle {

	private final PlayerSprite mPlayerSprite;
	// 顔、名前
	private TiledSprite mFaceTiledSprite;
	private Text mNameText;
	// ステータス
	private Text mHitPointText;
	private Text mMoveAttackDirection;
	private Text mAttackPointText;
	private Text mDefencePointText;
	// スキル
	private Text mSkillHeaderText;
	private Rectangle mSkillIconRectangle;
	private List<TiledSprite> mSkillIconSpriteList;
	// 装備
	private Text mEquipHeaderText;
	private Rectangle mEquipIconRectangle;
	private Text mWeaponNameText;
	private TiledSprite mWeaponIconSprite;
	private Text mAccessoryNameText;
	private TiledSprite mAccessoryIconSprite;
	
	public PlayerStatusRectangle(KeyListenScene pBaseScene, final PlayerSprite pPlayerSprite, final Font pFont, float pX, float pY, float pWidth,
			float pHeight, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		this.setColor(Color.WHITE);
		this.setAlpha(0.5f);
		
		this.mPlayerSprite = pPlayerSprite;
		init(pBaseScene, pFont, pVertexBufferObjectManager);
	}
	
	private void init(KeyListenScene pBaseScene, Font pFont, VertexBufferObjectManager pVertexBufferObjectManager) {
		ActorPlayerDto actor = mPlayerSprite.getActorPlayer();
		mFaceTiledSprite = pBaseScene.getResourceTiledSprite(mPlayerSprite.getFaceFileName(), 4, 2);
		attachChild(mFaceTiledSprite);
		
		float faceRigthX = mFaceTiledSprite.getWidth();
		mNameText = new Text(faceRigthX, 0, pFont, 
				actor.getName(), 
				pVertexBufferObjectManager);
		attachChild(mNameText);
		mHitPointText = new Text(faceRigthX, mNameText.getY() + mNameText.getHeight(), pFont, 
				String.format("HP %02d/%02d", actor.getHitPoint(), actor.getHitPoint()),  // TODO: limit
				pVertexBufferObjectManager);
		attachChild(mHitPointText);
		mMoveAttackDirection = new Text(faceRigthX, mHitPointText.getY() + mHitPointText.getHeight(), pFont, 
				String.format("移動力 %d 射程 %d", actor.getMovePoint(), actor.getAttackRange()), 
				pVertexBufferObjectManager);
		attachChild(mMoveAttackDirection);
		
		float nameRigthX = 0;
		if (mNameText.getWidth() >=  mHitPointText.getWidth() && mNameText.getWidth() >= mMoveAttackDirection.getWidth()) {
			nameRigthX = mNameText.getX() + mNameText.getWidth();
		} else if (mHitPointText.getWidth() >=  mNameText.getWidth() && mHitPointText.getWidth() >= mMoveAttackDirection.getWidth()) { 
			nameRigthX = mHitPointText.getX() + mHitPointText.getWidth();
		} else if (mMoveAttackDirection.getWidth() >=  mNameText.getWidth() && mMoveAttackDirection.getWidth() >= mHitPointText.getWidth()) { 
			nameRigthX = mMoveAttackDirection.getX() + mMoveAttackDirection.getWidth();
		}
		mAttackPointText = new Text(nameRigthX, 0, pFont, 
				String.format("攻撃力 %3d" ,actor.getAttackPoint()), 
				pVertexBufferObjectManager);
		attachChild(mAttackPointText);
		mDefencePointText = new Text(nameRigthX, mAttackPointText.getY() + mAttackPointText.getHeight(), pFont, 
				String.format("防御力 %3d", actor.getDefencePoint()), 
				pVertexBufferObjectManager);
		attachChild(mDefencePointText);
	
		mSkillHeaderText = new Text(0, getHeight() / 2, pFont, 
				"スキル", 
				pVertexBufferObjectManager);
		attachChild(mSkillHeaderText);
		
		mEquipHeaderText = new Text(getWidth() / 2, getHeight() / 2, pFont, 
				"装備", 
				pVertexBufferObjectManager);
		attachChild(mEquipHeaderText);
		mWeaponNameText = new Text(mEquipHeaderText.getX(), mEquipHeaderText.getY() + mEquipHeaderText.getHeight(), pFont, 
				"ひのきの棒", 
				pVertexBufferObjectManager);
		attachChild(mWeaponNameText);
		mAccessoryNameText = new Text(mEquipHeaderText.getX(), mWeaponNameText.getY() + mWeaponNameText.getHeight(), pFont, 
				"ただの布切れ", 
				pVertexBufferObjectManager);
		attachChild(mAccessoryNameText);
	}
}
