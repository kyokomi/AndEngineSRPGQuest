package com.kyokomi.srpgquest.layer;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.ButtonSprite;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.MenuRectangle;
import com.kyokomi.core.sprite.MenuRectangle.MenuDirection;
import com.kyokomi.srpgquest.scene.MapBattleScene.LayerZIndex;
import com.kyokomi.srpgquest.scene.SrpgBaseScene;

public class MapBattleSelectMenuLayer {
	public enum SelectMenuType {
		SELECT_MENU_ATTACK_TYPE(1, "attack_btn.gif", "attack_btn_p.gif"),
		SELECT_MENU_MOVE_TYPE(2, "move_btn.gif", "move_btn_p.gif"),
		SELECT_MENU_WAIT_TYPE(3, "wait_btn.gif", "wait_btn_p.gif"),
		SELECT_MENU_CANCEL_TYPE(4, "cancel_btn.gif", "cancel_btn_p.gif"),
		;
		private Integer value;
		private String normalFileName;
		private String pressedFileName;
		private SelectMenuType(Integer value, String normalFileName, String pressedFileName) {
			this.value = value;
			this.normalFileName = normalFileName;
			this.pressedFileName = pressedFileName;
		}
		public Integer getValue() {
			return value;
		}
		public String getNormalFileName() {
			return normalFileName;
		}
		public String getPressedFileName() {
			return pressedFileName;
		}
		public static SelectMenuType get(Integer value) {
			SelectMenuType[] values = values();
			for (SelectMenuType type : values) {
				if (type.getValue() == value) {
					return type;
				}
			}
			throw new RuntimeException("find not tag type.");
		}
	}
	
	private MenuRectangle mMenuRectangle;
	private ButtonSprite.OnClickListener selectMenuOnClickListener;
	
	public MapBattleSelectMenuLayer(SrpgBaseScene pBaseScene, ButtonSprite.OnClickListener pSelectMenuOnClickListener) {
		this.selectMenuOnClickListener = pSelectMenuOnClickListener;
		createSelectMenuSprite(pBaseScene);
	}
	
	public void createSelectMenuSprite(SrpgBaseScene pBaseScene) {
		
		// ベースメニューを作成
		mMenuRectangle = new MenuRectangle(
				pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		mMenuRectangle.setZIndex(LayerZIndex.POPUP_LAYER.getValue());
		
		// 各ボタン配置
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_ATTACK_TYPE);
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_MOVE_TYPE);
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_WAIT_TYPE);
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_CANCEL_TYPE);
		
		// 縦表示メニュー生成
		mMenuRectangle.create(MenuDirection.MENU_DIRECTION_Y);
		pBaseScene.attachChild(mMenuRectangle);
		pBaseScene.registerTouchArea(mMenuRectangle);

		// 非表示にする
		hideSelectMenu();
	}
	
	private ButtonSprite creatButtonWithAddMenu(SrpgBaseScene pBaseScene, SelectMenuType pSelectMenuType) {
		ButtonSprite btnSprite = pBaseScene.getResourceButtonSprite(
				pSelectMenuType.getNormalFileName(), 
				pSelectMenuType.getPressedFileName());
		mMenuRectangle.addMenuItem(pSelectMenuType.getValue(), btnSprite);
		btnSprite.setOnClickListener(selectMenuOnClickListener);
		return btnSprite;
	}
	
	public void showSelectMenu(KeyListenScene pBaseScene, float x, float y, 
			boolean isAttackDone, boolean isMovedDone) {
		// 配置
		setCalcPosition(pBaseScene, x, y);
		
		// 攻撃
		IEntity attackMenuItem = mMenuRectangle.getChildByTag(
				SelectMenuType.SELECT_MENU_ATTACK_TYPE.getValue());
		if (attackMenuItem instanceof ButtonSprite) {
			((ButtonSprite) attackMenuItem).setEnabled(!isAttackDone);
			((ButtonSprite) attackMenuItem).setVisible(!isAttackDone); // 非活性ボタンがあればイラナイ
		}
		// 移動
		IEntity moveMenuItem = mMenuRectangle.getChildByTag(
				SelectMenuType.SELECT_MENU_MOVE_TYPE.getValue());
		if (moveMenuItem instanceof ButtonSprite) {
			((ButtonSprite) moveMenuItem).setEnabled(!isMovedDone);
			((ButtonSprite) moveMenuItem).setVisible(!isMovedDone); // 非活性ボタンがあればイラナイ
		}
		
		// 表示
		mMenuRectangle.setEnabled(true);
		mMenuRectangle.setVisible(true);
	}
	public void hideSelectMenu() {
		// 非表示
		mMenuRectangle.setEnabled(false);
		mMenuRectangle.setVisible(false);
	}
	
	public void setCalcPosition(KeyListenScene pBaseScene, float x, float y) {
		// 横は画面半分のどっち側にいるかで表示位置を垂直方向に反転させる
		if (x < pBaseScene.getWindowWidth() / 2) {
			x = x + 40;
		} else {
			x = x - mMenuRectangle.getWidth();
		}
		// 縦が画面外に入る場合は補正
		if ((y + mMenuRectangle.getHeight()) > pBaseScene.getWindowHeight()) {
			y = pBaseScene.getWindowHeight() - mMenuRectangle.getHeight();
		}
		mMenuRectangle.setX(x);
		mMenuRectangle.setY(y);
	}
}
