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

package com.kk.capstone.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kk.capstone.R;
import com.kk.capstone.model.Contract;
import com.kk.capstone.model.Provider;
import com.kk.capstone.ui.preference.PreferenceActivity;
import com.kk.capstone.widget.NewsWidgetProvider;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 0;

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    Adapter mAdapter;

    private FirebaseAnalytics mFirebaseAnalytics;

    public static Boolean hasSeenNews;

    public static final String CHANNEL1_KEY = "key_channel1";
    public static final String CHANNEL2_KEY = "key_channel2";
    public static final String CHANNEL3_KEY = "key_channel3";
    public static final String CHANNEL4_KEY = "key_channel4";
    public static final String CHANNEL5_KEY = "key_channel5";

    static final String[] MESSAGES_PROJECTION = {
            Contract.Entry.COLUMN_CHANNEL,
            Contract.Entry.COLUMN_ARTICLE,
            Contract.Entry.COLUMN_DATE,
            Contract.Entry.COLUMN_CHANNEL_KEY
    };

    public static final String[] NEWS_KEYS = {
            CHANNEL1_KEY, CHANNEL2_KEY, CHANNEL3_KEY, CHANNEL4_KEY, CHANNEL5_KEY
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        String token = FirebaseInstanceId.getInstance().getToken();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "uniqKey");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, token);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        hasSeenNews = true;
        updateWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasSeenNews = true;
        updateWidget();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hasSeenNews = false;
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NewsWidgetProvider.class));
        NewsWidgetProvider.updateAppWidget(this,appWidgetManager,appWidgetIds,0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_following_preferences) {
            Intent startFollowingActivity = new Intent(this, PreferenceActivity.class);
            startActivity(startFollowingActivity,makeSceneTransitionAnimation(this).toBundle());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = findCurrentFollowers(PreferenceManager.getDefaultSharedPreferences(this));
        return new CursorLoader(this, Contract.Entry.CONTENT_URI,MESSAGES_PROJECTION, selection, null, Contract.Entry.COLUMN_DATE + " DESC");
    }

    public static String findCurrentFollowers(SharedPreferences preferences) {
        Boolean isFollowing = false;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Contract.Entry.COLUMN_CHANNEL_KEY).append(" IN  (");

        for (String key : NEWS_KEYS) {
            if (preferences.getBoolean(key, false)) {
                isFollowing = true;
                stringBuffer.append("'").append(key).append("'");
                stringBuffer.append(",");
            }
        }
        if(isFollowing.equals(false))
            return "1=2";
        stringBuffer.setLength(stringBuffer.length() - 1);
        stringBuffer.append(")");
        return stringBuffer.toString();
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
