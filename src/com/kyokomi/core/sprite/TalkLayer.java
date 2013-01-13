package com.kyokomi.core.sprite;

import java.util.ArrayList;
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
	private Text talkText;
	
	// 会話関連
	private SparseArray<TiledSprite> faces;
	private List<PlayerTalk> talks;
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
				texture, Typeface.DEFAULT_BOLD, 18, true, Color.WHITE);
		
		// EngineのTextureManagerにフォントTextureを読み込み
		baseScene.getBaseActivity().getTextureManager().loadTexture(texture);
		baseScene.getBaseActivity().getFontManager().loadFont(font);
	}
	
	public void initTalk(SparseArray<TiledSprite> faces, List<PlayerTalk> talks) {
		// 初期化
		talkIndex = 0;
		textMaxLength = 0;
		
		// 会話する顔を登録
		setFaces(faces);
		
		// TODO: test用
		if (talks == null) {
			talks = new ArrayList<PlayerTalk>();
			talks.add(new PlayerTalk(1, 
					"これは、ゲームであっても、遊びではない。"));
			talks.add(new PlayerTalk(1, 
					"ああああああああああ\nああああああああああ\nあああああああああ\nあああああああああああああああ。"));
			talks.add(new PlayerTalk(1, 
					"言っとくが俺はソロだ。\n１日２日オレンジになるくらいどおって事ないぞ。"));
			talks.add(new PlayerTalk(1, 
					"レベルなんてタダの数字だよ。\nこの世界での強さは、単なる幻想に過ぎない。\nそんなものよりもっと大事なものがある。"));
			talks.add(new PlayerTalk(1, 
					"なんでや！！\n何でディアベルハンを見殺しにしたんや！"));
		}
		
		// 会話内容を登録
		setTalks(talks);
		
		// 会話表示
		nextTalk();
	}
	
	public void show() {
		setVisible(true);
	}
	public void hide() {
		setVisible(false);
	}
	
	public boolean nextTalk() {
		// これ以上会話がないときはfalseを返却
		if (talks.size() <= talkIndex) {
			return false;
		}
		
		show();
		
		// 表示対象の顔画像取得
		PlayerTalk playerTalk = talks.get(talkIndex);
		talkIndex++;
		TiledSprite nextFaceSprite = faces.get(playerTalk.getPlayerId());
		
		// 高さは顔の画像をベースに共通
		float basePositionY = baseScene.getWindowHeight() - nextFaceSprite.getHeight();
		
		// 会話ウィンドウの顔とテキストの背景を作成
		// 高さが変わってなければそのまま使う
		if (textBackground != null && textBackground.getY() != basePositionY) {
			// 前のやつを削除
			textBackground.detachChildren();
			textBackground.detachSelf();
			textBackground = null;
		}
		
		if (textBackground == null) {
			// 作成
			textBackground = new Rectangle(
					0, basePositionY, 
					baseScene.getWindowWidth(), nextFaceSprite.getHeight(), 
					baseScene.getBaseActivity().getVertexBufferObjectManager());
			textBackground.setColor(Color.BLACK);
			textBackground.setAlpha(0.8f);
			attachChild(textBackground);
			
			textBackground.attachChild(talkText);
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
		faceSprite.setPosition(0, 0);
		
		// テキストを表示
		talkText.setText(playerTalk.getTalk());
		talkText.setPosition(faceSprite.getWidth(), 0);

		// TODO: アニメーション効果入れる?
		
		return true;
	}
	
	private void setFaces(SparseArray<TiledSprite> faces) {
		this.faces = faces;
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
	 
	private void setTalks(List<PlayerTalk> talks) {
		this.talks = talks;
		
		// 最大文字数でテキストウィンドウを作成
		textMaxLength = getMaxLength(talks);
		talkText = new Text(16, 16, font, 
				getSizeToStr("-", textMaxLength), 
				new TextOptions(HorizontalAlign.LEFT), 
				baseScene.getBaseActivity().getVertexBufferObjectManager());
	}
	
	/**
	 * 引数のリスト内の文字列の最大文字数を取得.
	 * @param strings 文字列リスト
	 * @return 最大文字数
	 */
	private int getMaxLength(List<PlayerTalk> playerTalks) {
		int maxLength = 0;
		// 会話テキストに応じたTextサイズを作成
		for (PlayerTalk playerTalk : playerTalks) {
			if (maxLength < playerTalk.getTalk().length()) {
				maxLength = playerTalk.getTalk().length();
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
	
	/**
	 * プレイヤー会話内容クラス.
	 * @author kyokomi
	 *
	 */
	public class PlayerTalk {
		private Integer playerId;
		private String talk;
		
		public PlayerTalk(Integer playerId, String talk) {
			this.playerId = playerId;
			this.talk = talk;
		}
		/**
		 * @return the playerId
		 */
		public Integer getPlayerId() {
			return playerId;
		}
		/**
		 * @param playerId the playerId to set
		 */
		public void setPlayerId(Integer playerId) {
			this.playerId = playerId;
		}
		/**
		 * @return the talk
		 */
		public String getTalk() {
			return talk;
		}
		/**
		 * @param talk the talk to set
		 */
		public void setTalk(String talk) {
			this.talk = talk;
		}
	}
}
