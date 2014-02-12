package com.tbradbury.tetheringwidget;

import java.util.Arrays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class TetheringWidgetProvider extends AppWidgetProvider {
	private static final String DATA_BUTTON = "DATA_BUTTON";
	private static final String TETHERING_BUTTON  = "TETHERING_BUTTON";
	private static final String[] ALL_BUTTONS = {DATA_BUTTON, TETHERING_BUTTON};

	public TetheringWidgetProvider() {
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		logv("onUpdate called");
		for (int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.tethering_widget);
			ComponentName cn = new ComponentName(context, TetheringWidgetProvider.class);

			rv.setOnClickPendingIntent(R.id.data_network, getPendingSelfIntent(context, DATA_BUTTON));
			rv.setOnClickPendingIntent(R.id.tethering, getPendingSelfIntent(context, TETHERING_BUTTON));
			appWidgetManager.updateAppWidget(cn, rv);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		logv("onReceive called with action " + intent.getAction());
		if (Arrays.asList(ALL_BUTTONS).contains(intent.getAction())) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.tethering_widget);
			ComponentName watchWidget = new ComponentName(context, TetheringWidgetProvider.class);

			if (intent.getAction().equals(DATA_BUTTON)) {
				buttonData(context, remoteViews);
			} else if (intent.getAction().equals(TETHERING_BUTTON)) {
				buttonTethering(context, remoteViews);
			}

			appWidgetManager.updateAppWidget(watchWidget, remoteViews);
		}
	}

	private void buttonData(Context context, RemoteViews rv) {
		logv("Data button pressed");
		Intent intent=new Intent();
		intent.setClassName("com.android.phone","com.android.phone.Settings");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		
		// Tries to actually change the settings but requires permissions you can't get.
/*		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Method method = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
			method.setAccessible(true);
			method.invoke(cm, true);
		} catch (Exception e) {
			loge(e.toString());
			e.printStackTrace(); 
		}*/
	}

	private void buttonTethering(Context context, RemoteViews rv) {
		logv("Tethering button pressed");
		Intent tetherSettings = new Intent();
        tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
        tetherSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(tetherSettings);
	}

	private PendingIntent getPendingSelfIntent(Context context, String action) {
		Intent intent = new Intent(context, getClass());
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}
	private void logv(String s) {
		Log.v(this.getClass().getSimpleName(), s);
	}
	@SuppressWarnings("unused")
	private void loge(String s) {
		Log.e(this.getClass().getSimpleName(), s);
	}
}
