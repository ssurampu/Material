package com.srids.tagit;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.Toast;

public class TagItWidgetIntentReceiver extends BroadcastReceiver {

	private static final String ACTION_HANDLE_QRCODE = "com.srids.tagit.action.HANDLE_QRCODE";
	private static final String ACTION_HANDLE_NOTES = "com.srids.tagit.action.HANDLE_NOTES";
	private static final String ACTION_HANDLE_CLIPBOARD = "com.srids.tagit.action.HANDLE_CLIPBOARD";

	@Override
	public void onReceive(Context context, Intent intent) {

	}
/*
	@Override
	public void onReceive(Context context, Intent intent) {
        <receiver android:name=".TagItWidgetIntentReceiver">
            <intent-filter>
                <action android:name="com.srids.tagit.action.HANDLE_QRCODE" />
                <action android:name="com.srids.tagit.action.HANDLE_NOTES" />
                <action android:name="com.srids.tagit.action.HANDLE_CLIPBOARD" />
            </intent-filter>
        </receiver>

		if(intent.getAction().equals(ACTION_HANDLE_QRCODE)) {
			Intent intent1 = new Intent(context, HelpActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);

		} else if(intent.getAction().equals(ACTION_HANDLE_NOTES)) {
			Intent intent1 = new Intent(context, HelpActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);

		} else if(intent.getAction().equals(ACTION_HANDLE_CLIPBOARD)) {
			Intent intent1 = new Intent(context, HelpActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);

		}
	}
	*/
}
