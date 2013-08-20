package com.kyokomi.srpgquest;

import android.app.Notification;
import android.support.v4.app.NotificationCompat.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * CGM用Intent。
 * 
 * 本クラスの以下、メソッドは
 * インテント サービスのスレッドで実行することから、
 * UI スレッドをブロックするリスクがなく、自由にネットワークの呼び出しができます。
 * 
 * @author m00206-yokomichi
 *
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
    
    public GCMIntentService() {
        super(MainActivity.SENDER_ID);
    }
    
	/**
	 * デバイスが登録または登録解除を試みたが、エラーが返されたときに呼び出されます。
	 * 
	 * 通常は、( errorId によって返された ) エラーを評価し、問題の解決を試みることしかできません。
	 */
	@Override
	protected void onError(Context arg0, String arg1) {
		Log.i(TAG, "onError registrationId:" + arg1);
	}

	/**
	 * サーバが GCM にメッセージを送信したときに呼び出され、
	 * GCM はそれをデバイスに配信します。
	 * 
	 * メッセージがペイロードを持っている場合は、
	 * そのコンテンツはインテントのエキストラとして利用可能となります。
	 */
	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.i(TAG, "onMessage registrationId:" + arg1);
		
		// メッセージ受信
	    String message = arg1.getStringExtra("message");

	    Intent intent = new Intent();
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
	    
	    Builder builder = new Builder(arg0);
	    builder.setSmallIcon(R.drawable.ic_launcher);
	    builder.setContentTitle("お知らせです");
	    builder.setContentText(message);
	    builder.setContentIntent(contentIntent);
	    builder.setAutoCancel(true);
	    Notification notification = builder.getNotification();
		    
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    notificationManager.notify(R.string.app_name, notification);
	}

	/**
	 * 登録のインテントを受信した後で呼び出され、
	 * GCM により割り当てられた登録 ID がそのデバイス / アプリケーションのペアにパラメータとして渡されます。
	 * 
	 * 通常は regId をサーバに送信すべきであり、
	 * そうすることでサーバはこのデバイスにメッセージを送信することができるようになります。
	 */
	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// 登録完了
        Log.i(TAG, "onRegisted registrationId:" + arg1);
	}

	/**
	 *  GCM からデバイスが登録解除された後で呼び出されます。
	 *  
	 *  通常は regId をサーバに送信すべきであり、
	 *  そうすることでサーバはデバイスを登録解除することができます。
	 */
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "onUnregistered registrationId:" + arg1);
	}
	
	/**
	 * デバイスが登録または登録解除を試みたが、GCM サーバが利用できないときに呼び出されます。
	 * 
	 * GCM ライブラリは、このメソッドがオーバーライドされ、
	 * false を返す場合を除き、指数関数的なバックアップを使用してこの動作をリトライします。
	 * 
	 * このメソッドはオプションで、ユーザにメッセージを表示し、
	 * キャンセルまたはリトライを試みさせたい場合のみオーバーライドすべきです。
	 */
	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}

}
