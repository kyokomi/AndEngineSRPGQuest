package com.kyokomi.gamebase;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * 全画面を描画範囲として持つクラス.
 * ゲーム起動中は常に1つのSceneクラスのインスタンスが最前面に表示されている状態。
 * 
 * 以下のような処理を行う。
 * <li>オブジェクトを追加したり</li>
 * <li>それ自体をタッチのリスナーとして登録したり</li>
 * <li>毎フレーム呼び出してオブジェクトの位置やスコアを更新するアップデートハンドラーを登録したり</li>
 * 
 * @author kyokomi
 *
 */
public class MainScene extends Scene {

	/**
	 * Sceneを管理するActivityのインスタンスを保持.
	 * アプリの場合のContextと同じように利用できる。
	 */
	private BaseGameActivity mBaseGameActivity;
	
	/**
	 * コンストラクタ.
	 * @param baseGameActivity Sceneを管理するActivity
	 */
	public MainScene(BaseGameActivity baseGameActivity) {
		this.mBaseGameActivity = baseGameActivity;
		init();
	}
	
	/**
	 * イニシャライズ.
	 */
	public void init() {
		// 画像リソースが格納されている場所を指定
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// オブジェクトのサイズを指定（480 * 800が収まる2のべき乗）
		BitmapTextureAtlas bta = new BitmapTextureAtlas(
				mBaseGameActivity.getTextureManager(), 
				512, 1024, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA); // アルファ値の設定が可能なタイプ
		// 範囲をメモリ上に読み込み
		mBaseGameActivity.getTextureManager().loadTexture(bta);
		// メモリ上に読み込んだ範囲に画像を読み込み。座標(0,0)
		ITextureRegion btr = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bta, mBaseGameActivity, "main_bg.png", 0, 0);
		// Spriteをインスタンス化。座標（0,0）
		Sprite bg = new Sprite(0, 0, btr, mBaseGameActivity.getVertexBufferObjectManager());
		// Spriteのアルファ値取り扱いを設定
		bg.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// 画面に配置
		attachChild(bg);
	}
	
	
}
