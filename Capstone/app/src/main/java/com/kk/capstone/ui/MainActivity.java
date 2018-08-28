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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kk.capstone.R;
import com.kk.capstone.model.Contract;
import com.kk.capstone.model.Provider;
import com.kk.capstone.ui.preference.PreferenceActivity;
import com.kk.capstone.widget.NewsWidgetProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 0;

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    Adapter mAdapter;

    private FirebaseAnalytics mFirebaseAnalytics;

    public static Boolean hasSeenNews;

    static final String[] MESSAGES_PROJECTION = {
            Contract.COLUMN_CHANNEL,
            Contract.COLUMN_ARTICLE,
            Contract.COLUMN_DATE,
            Contract.COLUMN_CHANNEL_KEY
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(LOG, "Firebase Token : " + token);

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
            startActivity(startFollowingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.findCurrentFollowers(
                PreferenceManager.getDefaultSharedPreferences(this));
        Log.d(LOG, "Where clause is : " + selection);
        return new CursorLoader(this, Provider.Articles.CONTENT_URI,
                MESSAGES_PROJECTION, selection, null, Contract.COLUMN_DATE + " DESC");
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
