package com.srids.tagit;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class TagItWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_HANDLE_NOTES = "com.srids.tagit.action.HANDLE_NOTES";
    private static final String ACTION_HANDLE_SMS = "com.srids.tagit.action.HANDLE_SMS";
    private static final String ACTION_HANDLE_QRCODE = "com.srids.tagit.action.HANDLE_QRCODE";
    private static final String ACTION_HANDLE_CLIPBOARD = "com.srids.tagit.action.HANDLE_CLIPBOARD";
    private static final String ACTION_HANDLE_MAINACTIVITY = "com.srids.tagit.action.HANDLE_MAINACTIVITY";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        for(int i = 0; i < appWidgetIds.length; i++) {
            int appid = appWidgetIds[i];

            Intent intent = new Intent(context, TagItWidgetProvider.class);
            intent.setAction( ACTION_HANDLE_NOTES);
            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout1);
            views.setOnClickPendingIntent(R.id.widget_text1, pendingIntent);

            intent = new Intent(context, TagItWidgetProvider.class);
            intent.setAction(ACTION_HANDLE_SMS);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_text2, pendingIntent);

            intent = new Intent(context, TagItWidgetProvider.class);
            intent.setAction(ACTION_HANDLE_CLIPBOARD);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_text3, pendingIntent);

            intent = new Intent(context, TagItWidgetProvider.class);
            intent.setAction(ACTION_HANDLE_MAINACTIVITY);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_image1, pendingIntent);

            appWidgetManager.updateAppWidget(appid, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(ACTION_HANDLE_NOTES)) {
           // Toast.makeText(context, "qr code", Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent(context, AddNotes.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);

        } else if(intent.getAction().equals(ACTION_HANDLE_SMS)) {
            //Toast.makeText(context, "notes", Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent(context, SMSActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);

        } else if(intent.getAction().equals(ACTION_HANDLE_CLIPBOARD)) {
            //Toast.makeText(context, "clipboard", Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent(context, ClibBoardQRHandle.class);
            intent1.putExtra("HANDLE", "CLIPBOARD");
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);

        } else if(intent.getAction().equals(ACTION_HANDLE_MAINACTIVITY)) {
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
        super.onReceive(context, intent);
    }
}
