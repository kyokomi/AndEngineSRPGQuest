package com.kyokomi.core.sprite;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.util.Log;
import android.util.SparseArray;

public class MenuRectangle extends Rectangle {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private OnClickListener mOnClickListener;

	private boolean mEnabled = true;
	private State mState;
	
	private MenuDirection mDirection;
	private final float margin = 10;
	private SparseArray<IAreaShape> mEntityList;
	
//	private Text mButtonText;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MenuRectangle(
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(0, 0, 0, 0, pVertexBufferObjectManager);
		
		this.setColor(Color.BLACK);
		this.setAlpha(0.8f);
//		this.mOnClickListener = pOnClickListener;
		
//		this.mButtonText = pText;
//		this.mButtonText.setPosition(pAddWidth / 2, pAddHeight / 2);
//		attachChild(mButtonText);
		
//		this.changeState(State.NORMAL);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isEnabled() {
		return this.mEnabled;
	}

	public void setEnabled(final boolean pEnabled) {
		this.mEnabled = pEnabled;

		if(this.mEnabled && this.mState == State.DISABLED) {
			this.changeState(State.NORMAL);
		} else if(!this.mEnabled) {
			this.changeState(State.DISABLED);
		}
	}

	public boolean isPressed() {
		return this.mState == State.PRESSED;
	}

	public State getState() {
		return this.mState;
	}

	public void setOnClickListener(final OnClickListener pOnClickListener) {
		this.mOnClickListener = pOnClickListener;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		Log.d("", "action = [" + pSceneTouchEvent.getAction() + "] state = [" + this.mState + "]" );
		
		if(!this.isEnabled()) {
			this.changeState(State.DISABLED);
		} else {
		
			if(pSceneTouchEvent.isActionDown()) {
				this.changeState(State.PRESSED);
			} else if(pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
				this.changeState(State.NORMAL);
			} else if(pSceneTouchEvent.isActionUp() && this.mState == State.NORMAL) {
				this.changeState(State.NORMAL);
			} else if(pSceneTouchEvent.isActionUp() && this.mState == State.PRESSED) {
				this.changeState(State.NORMAL);
				
				// TODO: 使う？
				if (this.mOnClickListener != null) {
					this.mOnClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			}
			
			int len = mEntityList.size();
			IAreaShape entity = null;
			for (int i = 0; i < len; i++) {
				
				entity = mEntityList.valueAt(i);
				// 触れている
				if (entity.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
					entity.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				// 触れていない
				} else {
					if (pSceneTouchEvent.isActionCancel()) {
						entity.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				}
//				Log.d("onAreaTouched", "entity Tag = " + entity.getTag());
			}
		}

		return true;
	}

	@Override
	public boolean contains(final float pX, final float pY) {
		if(!this.isVisible()) {
			return false;
		} else {
			return super.contains(pX, pY);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * メニューアイテム追加.
	 * @param pId 一意なID
	 * @param pEntity アイテム
	 */
	public void addMenuItem(Integer pId, IAreaShape pEntity) {
		if (mEntityList == null) {
			mEntityList = new SparseArray<IAreaShape>();
		}
		pEntity.setTag(pId);
		if (mEntityList.get(pId) == null) {
			mEntityList.put(pId, pEntity);
		} else {
			// TODO: すでに存在する場合上書きしてるけどどうする？
			mEntityList.setValueAt(pId, pEntity);
		}
	}
	
	public enum MenuDirection {
		MENU_DIRECTION_X(1),
		MENU_DIRECTION_Y(2),
		;
		
		private Integer value;
		
		private MenuDirection(Integer value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
	}
	
	// 横か縦か選べるように
	public void create(MenuDirection pDirection) {
		
		this.mDirection = pDirection;
		float x = margin;
		float y = margin;
		float width = 0;
		float height = 0;
		
		// 子と自分自身を一旦detach
		detachChildren();
		detachSelf();
		
		int len = mEntityList.size();
		IAreaShape entity = null;
		for (int i = 0; i < len; i++) {
			entity = mEntityList.valueAt(i);
			entity.setPosition(x, y);
			
			float marginWithWidth = entity.getWidth() + margin;
			float marginWithHeight = entity.getHeight() + margin;
			if (mDirection == MenuDirection.MENU_DIRECTION_X) {
				x += marginWithWidth;
				marginWithWidth = x;
				
				marginWithHeight += margin;
				
			} else if (mDirection == MenuDirection.MENU_DIRECTION_Y) {
				y += marginWithHeight;
				marginWithHeight = y;
				
				marginWithWidth += margin;
			}
			
			if (width < marginWithWidth) {
				width = marginWithWidth;
			}
			if (height < marginWithHeight) {
				height = marginWithHeight;
			}
			attachChild(entity);
		}		

		// 良い感じのサイズにする
		setSize(width, height);
		// 良い感じの位置に移動する
		setPosition(getX() - width / 2,  getY() - height / 2);
	}
	
	/**
	 * ボタンの状態変化に応じた画像変更とか.
	 * @param pState
	 */
	private void changeState(final State pState) {
		if(pState == this.mState) {
			return;
		}

		this.mState = pState;
//		this.mButtonText.setText(mState.getText());
//		this.setColor(mState.getColor());
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface OnClickListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onClick(final IAreaShape pIAreaShape, final float pTouchAreaLocalX, final float pTouchAreaLocalY);
	}

	public static enum State {
		// ===========================================================
		// Elements
		// ===========================================================

		NORMAL(0,   "NORMAL", Color.BLACK),
		PRESSED(1,  "PRESSED", Color.BLUE),
		DISABLED(2, "DISABLED", new Color(0.7f, 0.7f, 0.7f, 1));

		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private final int mTiledTextureRegionIndex;
		private final String mText;
		private final Color mColor;

		// ===========================================================
		// Constructors
		// ===========================================================

		private State(final int pTiledTextureRegionIndex, final String pText, final Color pColor) {
			this.mTiledTextureRegionIndex = pTiledTextureRegionIndex;
			this.mText = pText;
			this.mColor = pColor;
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public int getTiledTextureRegionIndex() {
			return this.mTiledTextureRegionIndex;
		}

		/**
		 * @return the mText
		 */
		public String getText() {
			return mText;
		}

		/**
		 * @return the mColor
		 */
		public Color getColor() {
			return mColor;
		}

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}

