package com.kyokomi.core.sprite;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class TextButton extends Rectangle {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private OnClickListener mOnClickListener;

	private boolean mEnabled = true;
	private State mState;
	private Text mButtonText;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TextButton(Text pText, float pX, float pY, float pAddWidth, float pAddHeight,
			VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		super(pX, pY, pText.getWidth() + pAddWidth, pText.getHeight() + pAddHeight, pVertexBufferObjectManager);
		this.setColor(Color.BLACK);
		this.setAlpha(0.8f);
		this.mOnClickListener = pOnClickListener;
		
		this.mButtonText = pText;
		this.mButtonText.setPosition(pAddWidth / 2, pAddHeight / 2);
		attachChild(mButtonText);
		
		this.changeState(State.NORMAL);
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
		if(!this.isEnabled()) {
			this.changeState(State.DISABLED);
		} else if(pSceneTouchEvent.isActionDown()) {
			this.changeState(State.PRESSED);
		} else if(pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
			this.changeState(State.NORMAL);
		} else if(pSceneTouchEvent.isActionUp() && this.mState == State.PRESSED) {
			this.changeState(State.NORMAL);

			if(this.mOnClickListener != null) {
				this.mOnClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
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
	 * ボタンの状態変化に応じた画像変更とか.
	 * @param pState
	 */
	private void changeState(final State pState) {
		if(pState == this.mState) {
			return;
		}

		this.mState = pState;
		this.mButtonText.setText(mState.getText());
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

		public void onClick(final TextButton pTextButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY);
	}

	public static enum State {
		// ===========================================================
		// Elements
		// ===========================================================

		NORMAL(0,   "NORMAL"),
		PRESSED(1,  "PRESSED"),
		DISABLED(2, "DISABLED");

		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private final int mTiledTextureRegionIndex;
		private final String mText;

		// ===========================================================
		// Constructors
		// ===========================================================

		private State(final int pTiledTextureRegionIndex, final String pText) {
			this.mTiledTextureRegionIndex = pTiledTextureRegionIndex;
			this.mText = pText;
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

