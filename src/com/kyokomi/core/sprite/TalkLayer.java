package com.kyokomi.core.sprite;

import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.SparseArray;

import com.kyokomi.core.dto.PlayerTalkDto;
import com.kyokomi.core.dto.PlayerTalkDto.TalkDirection;
import com.kyokomi.core.scene.KeyListenScene;

/**
 * 会話レイヤー.
 * @author kyokomi
 *
 */
public class TalkLayer extends Rectangle {

	private KeyListenScene baseScene;
	
	// テキスト背景
	private Rectangle textBackground;
	
	// 顔関連
	private TiledSprite faceSprite;
	
	// テキスト関連
	private Integer textMaxLength;
	private Font font;
	private Text nameText;
	private Text talkText;
	
	// 会話関連
	private SparseArray<TiledSprite> faces;
	private List<PlayerTalkDto> talks;
	private Integer talkIndex;
	
	public TalkLayer(KeyListenScene baseScene) {
		super(0, 0, baseScene.getWindowWidth(), baseScene.getWindowHeight(), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
		this.setColor(Color.TRANSPARENT);
		
		initTalkLayer(baseScene);
	}
	
	public TalkLayer(KeyListenScene baseScene, float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		this.setColor(Color.TRANSPARENT);
		
		initTalkLayer(baseScene);
	}

	private void initTalkLayer(KeyListenScene baseScene) {
		this.baseScene = baseScene;
		
		// フォント初期化
		initFont();
	}
	
	private void initFont() {
		Texture texture = new BitmapTextureAtlas(
				baseScene.getBaseActivity().getTextureManager(), 512, 512, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = new Font(baseScene.getBaseActivity().getFontManager(), 
				texture, Typeface.DEFAULT, 16, true, Color.WHITE);
		
		// EngineのTextureManagerにフォントTextureを読み込み
		baseScene.getBaseActivity().getTextureManager().loadTexture(texture);
		baseScene.getBaseActivity().getFontManager().loadFont(font);
	}
	
	public void initTalk(SparseArray<TiledSprite> faces, List<PlayerTalkDto> talks) {
		// 初期化
		this.talkIndex = 0;
		this.textMaxLength = 0;
		
		// 会話する顔を登録
		this.faces = faces;
		
		// 会話内容を登録
		this.talks = talks;
		
		// 設定された会話内容を元に最大テキストサイズで初期化してTextを用意する
		textMaxLength = getMaxLength(talks);
		talkText = new Text(16, 16, font, 
				getSizeToStr("-", textMaxLength), 
				new TextOptions(HorizontalAlign.LEFT), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
		nameText = new Text(16, 16, font, 
				getSizeToStr("-", textMaxLength), 
				new TextOptions(HorizontalAlign.LEFT), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
		nameText.setColor(Color.GREEN);
		
		// 会話表示
		nextTalk();
	}
	
	public void show() {
		setVisible(true);
	}
	public void hide() {
		setVisible(false);
	}
	
	public void resetTalk() {
		this.talkIndex = 0;
	}
	public boolean nextTalk() {
		// これ以上会話がないときはfalseを返却
		if (talks.size() <= talkIndex) {
			return false;
		}
		
		// とりあえず表示
		show();
		
		// 表示対象の顔画像取得
		PlayerTalkDto playerTalk = talks.get(talkIndex);
		talkIndex++;
		TiledSprite nextFaceSprite = faces.get(playerTalk.getPlayerId());
		
		// 高さは顔の画像をベースに共通
		float basePositionY = baseScene.getWindowHeight() - nextFaceSprite.getHeight();
		
		// 会話ウィンドウの背景を作成。すでに作成済みのものと同じサイズのものを作ろうとした場合は作らない
		if (createTextBackground(nextFaceSprite.getHeight(), basePositionY)) {
			// 新しく作ったので改めてテキストを追加する
			textBackground.attachChild(talkText);
			textBackground.attachChild(nameText);
		}
		
		// 別の顔がセットされていたら削除して新しい顔に入れ替える
		if (textBackground.getChildByTag(nextFaceSprite.getTag()) == null) {
			if (faceSprite != null) {
				faceSprite.detachSelf();
				faceSprite = null;
			}
			faceSprite = nextFaceSprite;
			textBackground.attachChild(faceSprite);
		}
		
		// 名前の表示
		nameText.setText("【" +playerTalk.getName() + "】");
		// テキストを表示
		talkText.setText(playerTalk.getTalk());
		
		// 顔の表示方向を設定
		if (playerTalk.getTalkDirection() == TalkDirection.TALK_DIRECT_LEFT) {
			faceSprite.setFlippedHorizontal(false);
			faceSprite.setPosition(0, 0);
			nameText.setPosition(faceSprite.getWidth(), 0);
			talkText.setPosition(faceSprite.getWidth(), nameText.getHeight());
		} else if (playerTalk.getTalkDirection() == TalkDirection.TALK_DIRECT_RIGHT) {
			faceSprite.setFlippedHorizontal(true);
			faceSprite.setPosition(getWidth() - faceSprite.getWidth(), 0);
			nameText.setPosition(0, 0);
			talkText.setPosition(0, nameText.getHeight());
		}
		
		// 顔のスプライトを設定
		faceSprite.setCurrentTileIndex(playerTalk.getCurrentTileIndex());
		
		// TODO: アニメーション効果入れる?
		
		return true;
	}
	
	private boolean createTextBackground(float height, float y) {
		boolean isCreated = false;
		
		// 会話ウィンドウの顔とテキストの背景を作成
		// 高さが変わってなければそのまま使う
		if (textBackground != null && (textBackground.getY() != y || textBackground.getHeight() != height)) {
			// 前のやつを削除
			textBackground.detachChildren();
			textBackground.detachSelf();
			textBackground = null;
		}
		
		if (textBackground == null) {
			// 作成
			textBackground = new Rectangle(
					0, y, 
					baseScene.getWindowWidth(), height, 
					baseScene.getBaseActivity().getVertexBufferObjectManager());
			textBackground.setColor(Color.BLACK);
			textBackground.setAlpha(0.8f);
			attachChild(textBackground);
			
			isCreated = true;
		}
		return isCreated;
	}
	
//	private void addFace(int id, TiledSprite faceSprite) {
//		if (faces == null) {
//			faces = new SparseArray<TiledSprite>();
//		}
//		// 未登録なら追加、基本的に同じIDを使った上書きはしない
//		if (faces.indexOfKey(id) < 0) {
//			faces.put(id, faceSprite);			
//		}
//	}
	
	/**
	 * 引数のリスト内の文字列の最大文字数を取得.
	 * @param strings 文字列リスト
	 * @return 最大文字数
	 */
	private int getMaxLength(List<PlayerTalkDto> PlayerTalkDtos) {
		int maxLength = 0;
		// 会話テキストに応じたTextサイズを作成
		for (PlayerTalkDto PlayerTalkDto : PlayerTalkDtos) {
			if (maxLength < PlayerTalkDto.getTalk().length()) {
				maxLength = PlayerTalkDto.getTalk().length();
			}
		}
		return maxLength;
	}
	
	private String getSizeToStr(String str, int size) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buffer.append(str);
		}
		return buffer.toString();
	}
}
