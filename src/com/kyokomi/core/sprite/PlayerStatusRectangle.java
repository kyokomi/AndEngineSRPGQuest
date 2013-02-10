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
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

/**
 * プレイヤーステータスウィンドウ.
 * @author kyokomi
 *
 */
public class PlayerStatusRectangle extends Rectangle {

	private final PlayerSprite mPlayerSprite;
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
	
	/** リフレッシュ用に全テキストを管理. */
	private List<Text> mTextList;
	
	public PlayerStatusRectangle(SrpgBaseScene pBaseScene, final PlayerSprite pPlayerSprite, final Font pFont, 
			float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY, pWidth, pHeight, pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		
		this.setColor(Color.WHITE);
		this.setAlpha(0.5f);
		
		this.mPlayerSprite = pPlayerSprite;
		init(pBaseScene, pFont);
	}
	
	/**
	 * ウィンドウ初期化.
	 * TODO: 文言はString.xmlに移動する
	 * @param pBaseScene
	 * @param pFont
	 * @param pVertexBufferObjectManager
	 */
	private void init(SrpgBaseScene pBaseScene, Font pFont) {
		
		mTextList = new ArrayList<Text>();
		
		ActorPlayerDto actor = mPlayerSprite.getActorPlayer();
		mFaceTiledSprite = pBaseScene.getResourceTiledSprite(mPlayerSprite.getFaceFileName(), 4, 2);
		attachChild(mFaceTiledSprite);
		
		float faceRigthX = mFaceTiledSprite.getWidth();
		mNameText = new Text(faceRigthX, 0, pFont, 
				actor.getName(), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mNameText);
		mLevelExpText = new Text(faceRigthX, mNameText.getY() + mNameText.getHeight(), pFont, 
				String.format("Lv.%2d (%3d/%3d)", actor.getLv(), actor.getExp(), 100), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mLevelExpText);
		mHitPointText = new Text(faceRigthX, mLevelExpText.getY() + mLevelExpText.getHeight(), pFont, 
				String.format("HP %02d/%02d", actor.getHitPoint(), actor.getHitPointLimit()),
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mHitPointText);
		mMoveAttackDirectionText = new Text(faceRigthX, mHitPointText.getY() + mHitPointText.getHeight(), pFont, 
				String.format("移動力 %d 射程 %d", actor.getMovePoint(), actor.getAttackRange()), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mMoveAttackDirectionText);
		
		float nameRigthX = (getWidth() - faceRigthX) / 2 + faceRigthX;
		float nameBottomY = mNameText.getY() + mNameText.getHeight();
		mAttackPointText = new Text(nameRigthX, nameBottomY, pFont, 
				String.format("攻撃力 %3d" ,actor.getAttackPoint()), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mAttackPointText);
		mDefencePointText = new Text(nameRigthX, mAttackPointText.getY() + mAttackPointText.getHeight(), pFont, 
				String.format("防御力 %3d", actor.getDefencePoint()), 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mDefencePointText);
	
		// スキル領域を作成
		mSkillIconRectangle = new Rectangle(0, getHeight() / 2, 
				getWidth() / 2, getHeight() / 2, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mSkillIconRectangle.setColor(Color.TRANSPARENT);
		attachChild(mSkillIconRectangle);
		// スキル欄ヘッダー
		mSkillHeaderText = new Text(0, 0, pFont, 
				"[スキル]", 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mSkillIconRectangle, mSkillHeaderText);
		if (actor.getSkillDtoList() != null && !actor.getSkillDtoList().isEmpty()) {
			// スキルアイコン
//			List<Integer> skillIds = new ArrayList<Integer>();
//			skillIds.add(9 + 16 * 30);
//			skillIds.add(10 + 16 * 30);
//			skillIds.add(11 + 16 * 30);
//			skillIds.add(12 + 16 * 30);
			mSkillIconSpriteList = new ArrayList<TiledSprite>();
			float x = mSkillHeaderText.getX();
			float y = mSkillHeaderText.getY() + mSkillHeaderText.getHeight();
			for (ActorPlayerSkillDto skillDto : actor.getSkillDtoList()) {
				TiledSprite skillIcon = pBaseScene.getIconSetTiledSprite();
				skillIcon.setCurrentTileIndex(skillDto.getSkillImgResId());
				skillIcon.setPosition(x, y);
				mSkillIconRectangle.attachChild(skillIcon);
				mSkillIconSpriteList.add(skillIcon);
				x += skillIcon.getWidth();
			}
		}
		
		// 装備領域を作成
		mEquipIconRectangle = new Rectangle(getWidth() / 2, getHeight() / 2, 
				getWidth() / 2, getHeight() / 2, 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mEquipIconRectangle.setColor(Color.TRANSPARENT);
		attachChild(mEquipIconRectangle);
		// 装備欄ヘッダー
		mEquipHeaderText = new Text(0, 0, pFont, 
				"[装備]", 
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		attachChildText(mEquipIconRectangle, mEquipHeaderText);
		
		ActorPlayerEquipDto equipDto = actor.getEquipDto();
		if (equipDto != null) {
			// 武器アイコン
			mWeaponIconSprite = pBaseScene.getIconSetTiledSprite();
			mWeaponIconSprite.setCurrentTileIndex(equipDto.getWeaponImgResId());
			mWeaponIconSprite.setPosition(mEquipHeaderText.getX(), mEquipHeaderText.getY() + mEquipHeaderText.getHeight());
			mEquipIconRectangle.attachChild(mWeaponIconSprite);
			// 武器テキスト
			mWeaponNameText = new Text(mWeaponIconSprite.getWidth() + mEquipHeaderText.getX(), 
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
		
		ActorPlayerDto actor = mPlayerSprite.getActorPlayer();
		
		mNameText.setText(actor.getName()); 
		
		mLevelExpText.setText( 
				String.format("Lv.%2d (%3d/%3d)", actor.getLv(), actor.getExp(), 100));
		mHitPointText.setText( 
				String.format("HP %3d/%3d", actor.getHitPoint(), actor.getHitPointLimit()));
		mMoveAttackDirectionText.setText( 
				String.format("移動力 %d 射程 %d", actor.getMovePoint(), actor.getAttackRange()));
		mAttackPointText.setText( 
				String.format("攻撃力 %3d" ,actor.getAttackPoint()));
		mDefencePointText.setText( 
				String.format("防御力 %3d", actor.getDefencePoint()));
	
		// スキル欄ヘッダー
		mSkillHeaderText.setText("[スキル]");
		// 装備欄ヘッダー
		mEquipHeaderText.setText("[装備]");
		// 武器テキスト
		if (actor.getEquipDto() != null) {
			mWeaponNameText.setText(actor.getEquipDto().getWeaponName()); 
			// アクセサリーテキスト
			mAccessoryNameText.setText(actor.getEquipDto().getAccessoryName()); 			
		}
	}
}
