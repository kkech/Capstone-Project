/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.kk.capstone.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kk.capstone.ui.MainActivity;
import com.kk.capstone.widget.NewsWidgetProvider;
import com.kk.capstone.R;
import com.kk.capstone.model.Contract;
import com.kk.capstone.model.Provider;

import java.util.Map;

public class FirebaseMessageService extends FirebaseMessagingService {

    private static String LOG = FirebaseMessageService.class.getSimpleName();

    private static final String JSON_AUTHOR_KEY = Contract.COLUMN_CHANNEL;
    private static final String JSON_KEY_AUTHOR_KEY = Contract.COLUMN_CHANNEL_KEY;
    private static final String JSON_MESSAGE_KEY = Contract.COLUMN_ARTICLE;
    private static final String JSON_DATE_KEY = Contract.COLUMN_DATE;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG, "Message from: " + remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            Log.d(LOG, "Message data : " + data);
            sendNotification(data);
            insertNews(data);
            if(MainActivity.hasSeenNews.equals(false))
                updateWidget();
        }
    }

    private void insertNews(final Map<String, String> data) {
        AsyncTask<Void, Void, Void> insertNewsTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids){
                ContentValues newMessage = new ContentValues();
                newMessage.put(Contract.COLUMN_CHANNEL, data.get(JSON_AUTHOR_KEY));
                newMessage.put(Contract.COLUMN_ARTICLE, data.get(JSON_MESSAGE_KEY).trim());
                newMessage.put(Contract.COLUMN_DATE, data.get(JSON_DATE_KEY));
                newMessage.put(Contract.COLUMN_CHANNEL_KEY, data.get(JSON_KEY_AUTHOR_KEY));
                getContentResolver().insert(Provider.Articles.CONTENT_URI, newMessage);
                return null;
            }
        };
        insertNewsTask.execute();
    }

    private void sendNotification(Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String author = data.get(JSON_AUTHOR_KEY);
        String message = data.get(JSON_MESSAGE_KEY);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.baseline_drafts_black_36)
                .setContentTitle("New article from : " + author)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NewsWidgetProvider.class));
        NewsWidgetProvider.updateAppWidget(this,appWidgetManager,appWidgetIds,1);
    }
}