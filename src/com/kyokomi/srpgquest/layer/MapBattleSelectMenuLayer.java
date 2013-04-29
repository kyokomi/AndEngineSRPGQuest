package com.kyokomi.srpgquest.layer;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.ButtonSprite;

import com.kyokomi.core.scene.KeyListenScene;
import com.kyokomi.core.sprite.MenuRectangle;
import com.kyokomi.srpgquest.constant.LayerZIndexType;

public class MapBattleSelectMenuLayer extends MenuRectangle {
	public enum SelectMenuType {
		SELECT_MENU_ATTACK_TYPE(1, "btn/attack_btn.gif", "btn/attack_btn_p.gif"),
		SELECT_MENU_MOVE_TYPE(2,   "btn/move_btn.gif",   "btn/move_btn_p.gif"),
		SELECT_MENU_WAIT_TYPE(3,   "btn/wait_btn.gif",   "btn/wait_btn_p.gif"),
		SELECT_MENU_CANCEL_TYPE(4, "btn/cancel_btn.gif", "btn/cancel_btn_p.gif"),
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
	
	private ButtonSprite.OnClickListener selectMenuOnClickListener;
	
	public MapBattleSelectMenuLayer(KeyListenScene pBaseScene, ButtonSprite.OnClickListener pSelectMenuOnClickListener) {
		super(pBaseScene.getBaseActivity().getVertexBufferObjectManager());
		this.selectMenuOnClickListener = pSelectMenuOnClickListener;
		createSelectMenuSprite(pBaseScene);
	}
	
	public void createSelectMenuSprite(KeyListenScene pBaseScene) {
		
		setZIndex(LayerZIndexType.POPUP_LAYER.getValue());
		
		// 各ボタン配置
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_ATTACK_TYPE);
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_MOVE_TYPE);
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_WAIT_TYPE);
		creatButtonWithAddMenu(pBaseScene, SelectMenuType.SELECT_MENU_CANCEL_TYPE);
		
		// 縦表示メニュー生成
		create(MenuDirection.MENU_DIRECTION_Y);
		pBaseScene.attachChild(this);
		pBaseScene.registerTouchArea(this);

		// 非表示にする
		hideSelectMenu();
	}
	
	private ButtonSprite creatButtonWithAddMenu(KeyListenScene pBaseScene, SelectMenuType pSelectMenuType) {
		ButtonSprite btnSprite = pBaseScene.getResourceButtonSprite(
				pSelectMenuType.getNormalFileName(), 
				pSelectMenuType.getPressedFileName());
		addMenuItem(pSelectMenuType.getValue(), btnSprite);
		btnSprite.setOnClickListener(selectMenuOnClickListener);
		return btnSprite;
	}
	
	public void showSelectMenu(KeyListenScene pBaseScene, float x, float y, 
			boolean isAttackDone, boolean isMovedDone) {
		// 配置
		setCalcPosition(pBaseScene, x, y);
		
		// 攻撃
		IEntity attackMenuItem = getChildByTag(SelectMenuType.SELECT_MENU_ATTACK_TYPE.getValue());
		if (attackMenuItem instanceof ButtonSprite) {
			((ButtonSprite) attackMenuItem).setEnabled(!isAttackDone);
			((ButtonSprite) attackMenuItem).setVisible(!isAttackDone); // 非活性ボタンがあればイラナイ
		}
		// 移動
		IEntity moveMenuItem = getChildByTag(SelectMenuType.SELECT_MENU_MOVE_TYPE.getValue());
		if (moveMenuItem instanceof ButtonSprite) {
			((ButtonSprite) moveMenuItem).setEnabled(!isMovedDone);
			((ButtonSprite) moveMenuItem).setVisible(!isMovedDone); // 非活性ボタンがあればイラナイ
		}
		
		// 表示
		setEnabled(true);
		setVisible(true);
	}
	public void hideSelectMenu() {
		// 非表示
		setEnabled(false);
		setVisible(false);
	}
	
	public void setCalcPosition(KeyListenScene pBaseScene, float x, float y) {
		// 横は画面半分のどっち側にいるかで表示位置を垂直方向に反転させる
		if (x < pBaseScene.getWindowWidth() / 2) {
			x = x + 40;
		} else {
			x = x - getWidth();
		}
		// 縦の下が画面外に入る場合は補正
		if ((y + getHeight()) > pBaseScene.getWindowHeight()) {
			y = pBaseScene.getWindowHeight() - getHeight();
		}
		// 縦の上が画面外に入る場合補正(上の1/4はステータス用)
		if (y < (pBaseScene.getWindowHeight() / 4)) {
			y = (pBaseScene.getWindowHeight() / 4);
		}
		setX(x);
		setY(y);
	}
}
