package com.kyokomi.core.sprite;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;

import com.kyokomi.core.dto.ActorPlayerDto;
import com.kyokomi.core.dto.ActorPlayerEquipDto;
import com.kyokomi.core.dto.ActorPlayerSkillDto;
import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.srpgquest.scene.AbstractGameScene;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

/**
 * プレイヤーステータスウィンドウ.
 * @author kyokomi
 *
 */
public class PlayerStatusRectangle extends Rectangle {

	private final static float BASE_X = 8;
	private final static float BASE_Y = 14;
	
	private final ActorPlayerDto mActorPlayerDto;
	
	// 顔、名前
	private TiledSprite mFaceTiledSprite;
	private Text mNameText;
	// ステータス
	private Text mLevelExpText;
	private Text mHitPointText;
	private Text mMoveAttackDirectionText;
	private Text mAttackPointText;
	private Text mDefencePointText;
	// スキル
//	private Text mSkillHeaderText;
	private Rectangle mSkillIconRectangle;
	private List<TiledSprite> mSkillIconSpriteList;
	// 装備
//	private Text mEquipHeaderText;
	private Rectangle mEquipIconRectangle;
	private Text mWeaponNameText;
	private TiledSprite mWeaponIconSprite;
	private Text mAccessoryNameText;
	private TiledSprite mAccessoryIconSprite;
	
	/** リフレッシュ用に全テキストを管理. */
	private List<Text> mTextList;
	
	private float baseWidth;
	private float baseHeight;

	public PlayerStatusRectangle(SrpgBaseScene pBaseScene, final Font pFont, 
			final ActorPlayerDto pActorPlayerDto, final String pFaceFileName, 
			float pX, float pY) {
		super(pX, pY, 
				pBaseScene.getWindowWidth() / 2, 
				pBaseScene.getWindowHeight() / 2, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		this.baseWidth = getWidth();
		this.baseHeight = getHeight();		
		this.setColor(Color.WHITE);
		this.setAlpha(0.5f);
		
		this.mActorPlayerDto = pActorPlayerDto;
		init(pBaseScene, pFont, pFaceFileName);
	}
	
	public PlayerStatusRectangle(SrpgBaseScene pBaseScene, final Font pFont, 
			final ActorPlayerDto pActorPlayerDto, final String pFaceFileName, 
			float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY, pWidth, pHeight, pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		this.baseWidth = pWidth;
		this.baseHeight = pHeight;
		this.setColor(Color.WHITE);
		this.setAlpha(0.5f);
		
		this.mActorPlayerDto = pActorPlayerDto;
		init(pBaseScene, pFont, pFaceFileName);
	}
	
	/**
	 * ウィンドウ初期化.
	 * TODO: 文言はString.xmlに移動する
	 * @param pBaseScene
	 * @param pFont
	 * @param pVertexBufferObjectManager
	 */
	private void init(SrpgBaseScene pBaseScene, Font pFont, String pFaceFileName) {
		
		mTextList = new ArrayList<Text>();
		
		mFaceTiledSprite = pBaseScene.getResourceFaceSprite(
				mActorPlayerDto.getPlayerId(), pFaceFileName);
		mFaceTiledSprite.setPosition(BASE_X, BASE_Y);
		attachChild(mFaceTiledSprite);
		
		float faceRigthX = mFaceTiledSprite.getWidth() + mFaceTiledSprite.getX();
		float faceRigthY = mFaceTiledSprite.getY() - 4;
		mNameText = new Text(faceRigthX, faceRigthY, pFont, 
				mActorPlayerDto.getName(), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mNameText);
		mLevelExpText = new Text(faceRigthX, mNameText.getY() + mNameText.getHeight(), pFont, 
				String.format("Lv.%2d (%3d/%3d)", 
						mActorPlayerDto.getLv(), 
						mActorPlayerDto.getExp(), 100), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mLevelExpText);
		mHitPointText = new Text(faceRigthX, mLevelExpText.getY() + mLevelExpText.getHeight(), pFont, 
				String.format("HP %02d/%02d", 
						mActorPlayerDto.getHitPoint(), 
						mActorPlayerDto.getHitPointLimit()),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mHitPointText);
		mMoveAttackDirectionText = new Text(faceRigthX, mHitPointText.getY() + mHitPointText.getHeight(), pFont, 
				String.format("移動力 %d 射程 %d", 
						mActorPlayerDto.getMovePoint(), 
						mActorPlayerDto.getAttackRange()), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mMoveAttackDirectionText);
		
		// スキル領域を作成
		mSkillIconRectangle = new Rectangle(mMoveAttackDirectionText.getX(), mMoveAttackDirectionText.getY() + mMoveAttackDirectionText.getHeight(), 
				getWidth() / 2, getHeight() / 6, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mSkillIconRectangle.setColor(Color.TRANSPARENT);
		attachChild(mSkillIconRectangle);
		// スキル欄ヘッダー
		float skillHeadX = 0;
		float skillHeadY = 2;
		
		if (mActorPlayerDto.getSkillDtoList() != null && !mActorPlayerDto.getSkillDtoList().isEmpty()) {
			// スキルアイコン
			mSkillIconSpriteList = new ArrayList<TiledSprite>();
			float x = skillHeadX;
			float y = skillHeadY;
			for (ActorPlayerSkillDto skillDto : mActorPlayerDto.getSkillDtoList()) {
				TiledSprite skillIcon = pBaseScene.getIconSetTiledSprite();
				skillIcon.setCurrentTileIndex(skillDto.getSkillImgResId());
				skillIcon.setPosition(x, y);
				mSkillIconRectangle.attachChild(skillIcon);
				mSkillIconSpriteList.add(skillIcon);
				x += skillIcon.getWidth();
			}
		}
		
		float nameRigthX = (getWidth() - faceRigthX) / 2 + faceRigthX - (BASE_X * 2);
		float nameBottomY = mNameText.getY() + mNameText.getHeight();
		mAttackPointText = new Text(nameRigthX, nameBottomY, pFont, 
				String.format("攻撃 %3d" ,mActorPlayerDto.getAttackPoint()), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mAttackPointText);
		mDefencePointText = new Text(
				mAttackPointText.getX() + mAttackPointText.getWidth() + 8,
				nameBottomY, pFont, 
				String.format("防御 %3d", mActorPlayerDto.getDefencePoint()), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mDefencePointText);
	
		// 装備領域を作成
		mEquipIconRectangle = new Rectangle(nameRigthX, mDefencePointText.getY() + mDefencePointText.getHeight(), 
				getWidth() / 2, getHeight() / 4, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mEquipIconRectangle.setColor(Color.TRANSPARENT);
		attachChild(mEquipIconRectangle);
		float equipHeadX = 0;
		float equipHeadY = 5;
		
		ActorPlayerEquipDto equipDto = mActorPlayerDto.getEquipDto();
		if (equipDto != null) {
			// 武器アイコン
			mWeaponIconSprite = pBaseScene.getIconSetTiledSprite();
			mWeaponIconSprite.setCurrentTileIndex(equipDto.getWeaponImgResId());
			mWeaponIconSprite.setPosition(equipHeadX, equipHeadY);
			mEquipIconRectangle.attachChild(mWeaponIconSprite);
			// 武器テキスト
			mWeaponNameText = new Text(mWeaponIconSprite.getWidth() + mWeaponIconSprite.getX(), 
					mWeaponIconSprite.getY() + mWeaponIconSprite.getHeight() / 2, 
					pFont, equipDto.getWeaponName(), 
					pBaseScene.getBaseActivity().getVertexBufferObjectManager());
			mWeaponNameText.setY(mWeaponNameText.getY() - mWeaponNameText.getHeight() / 2);
			attachChildText(mEquipIconRectangle, mWeaponNameText);
			// アクセサリーアイコン
			mAccessoryIconSprite = pBaseScene.getIconSetTiledSprite();
			mAccessoryIconSprite.setCurrentTileIndex(equipDto.getAccessoryImgResId());
			mAccessoryIconSprite.setPosition(mWeaponIconSprite.getX(), mWeaponIconSprite.getY() + mWeaponIconSprite.getHeight());
			mEquipIconRectangle.attachChild(mAccessoryIconSprite);
			// アクセサリーテキスト
			mAccessoryNameText = new Text(mAccessoryIconSprite.getWidth() + mAccessoryIconSprite.getX(), 
					mAccessoryIconSprite.getY() + mAccessoryIconSprite.getHeight() / 2, 
					pFont, equipDto.getAccessoryName(), 
					pBaseScene.getBaseActivity().getVertexBufferObjectManager());
			mAccessoryNameText.setY(mAccessoryNameText.getY() - mAccessoryNameText.getHeight() / 2);
			attachChildText(mEquipIconRectangle, mAccessoryNameText);	
		}
	}
	
	public void attachChildText(Text text) {
		mTextList.add(text);
		attachChild(text);
	}
	public void attachChildText(IEntity entity, Text text) {
		mTextList.add(text);
		entity.attachChild(text);
	}
	public void setFontColor(Color pColor) {
		for (Text text : mTextList) {
			text.setColor(pColor);
		}
	}
	
	public void refresh() {
		
		mNameText.setText(mActorPlayerDto.getName()); 
		
		mLevelExpText.setText( 
				String.format("Lv.%2d (%3d/%3d)", mActorPlayerDto.getLv(), mActorPlayerDto.getExp(), 100));
		mHitPointText.setText( 
				String.format("HP %3d/%3d", mActorPlayerDto.getHitPoint(), mActorPlayerDto.getHitPointLimit()));
		mMoveAttackDirectionText.setText( 
				String.format("移動力 %d 射程 %d", mActorPlayerDto.getMovePoint(), mActorPlayerDto.getAttackRange()));
		mAttackPointText.setText( 
				String.format("攻撃 %3d" ,mActorPlayerDto.getAttackPoint()));
		mDefencePointText.setText( 
				String.format("防御 %3d", mActorPlayerDto.getDefencePoint()));
	
		// 武器テキスト
		if (mActorPlayerDto.getEquipDto() != null) {
			mWeaponNameText.setText(mActorPlayerDto.getEquipDto().getWeaponName()); 
			// アクセサリーテキスト
			mAccessoryNameText.setText(mActorPlayerDto.getEquipDto().getAccessoryName()); 			
		}
	}

	public enum PlayerStatusRectangleType {
		MINI_STATUS,
		ALL_STATUS,
		LVUP_STATUS
	}
	public void show(PlayerStatusRectangleType rectangleType) {
		switch (rectangleType) {
		case MINI_STATUS:
			setHeight(this.baseHeight / 2);
			break;
		case ALL_STATUS:
			setWidth(this.baseWidth);
			setHeight(this.baseHeight);
			break;
		case LVUP_STATUS:
			allTextVisible(false);
			skillVisible(false);
			equipVisible(false);
			// 名前とレベルだけ表示
			mNameText.setVisible(true);
			mLevelExpText.setVisible(true);
			
			// 顔グラのサイズにする
			setHeight(mFaceTiledSprite.getHeight());
			setWidth(this.baseWidth / 2);
			break;
		default:
			break;
		}
		
		this.setVisible(true);
	}
	public void hide() {
		this.setVisible(false);
	}
	
	public void skillVisible(boolean pVisible) {
		// スキル
		mSkillIconRectangle.setVisible(pVisible);
		for (TiledSprite sprite : mSkillIconSpriteList) {
			sprite.setVisible(pVisible);
		}
	}
	public void equipVisible(boolean pVisible) {
		// 装備
		mEquipIconRectangle.setVisible(pVisible);
		mWeaponNameText.setVisible(pVisible);
		mWeaponIconSprite.setVisible(pVisible);
		mAccessoryNameText.setVisible(pVisible);
		mAccessoryIconSprite.setVisible(pVisible);
	}
	public void allTextVisible(boolean pVisible) {
		// 全テキスト非表示
		for (Text text : mTextList) {
			text.setVisible(pVisible);
		}
	}
}
