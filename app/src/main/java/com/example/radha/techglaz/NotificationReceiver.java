package com.example.radha.techglaz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri pdfUri = intent.getData();
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.putExtra("pdfUri", pdfUri.toString());
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(activityIntent);
            Log.d("NotiReeiver","Inside notification receiver");
        }
    }
