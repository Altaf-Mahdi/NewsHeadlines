/*
 * Copyright (c) 2018 Altaf-Mahdi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.altafmahdi.newsheadlines;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.altafmahdi.newsheadlines.recyclerview.Article;
import com.altafmahdi.newsheadlines.recyclerview.ArticleUtils;
import com.altafmahdi.newsheadlines.recyclerview.ArticlesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;

    private Toolbar mToolBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFloatingActionButton;

    private ImageView mSadFaceImage;
    private TextView mNoInternetText;

    private ArticlesAdapter mArticlesAdapter;
    private List<Article> mArticles;

    private IntentReceiver mIntentReceiver;
    private IntentFilter mIntentFilter;

    private String mProvider;
    private String mToolBarTitle;
    private String mDownloadResult;

    private Preferences mPreferences;
    private Resources mRes;

    private DataBaseHelper mDataBaseHelper;

    private boolean mLastQueryWasSearch = false;
    private boolean mForceRunTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mRes = getResources();

        mDataBaseHelper = new DataBaseHelper(this);

        mPreferences = new Preferences(this);
        boolean firstRun = mPreferences.getBoolean("first_run");
        if (firstRun) {
            mPreferences.saveBoolean("first_run", false);
            mPreferences.saveString("provider", "bbc-news");
            mPreferences.saveBoolean("querySearch", false);
            mPreferences.saveString("toolbar_title",
                    mRes.getString(R.string.bbc_news_title));
            mForceRunTask = true;
        }

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Utils.refreshColors(this));
        mSwipeRefreshLayout.setOnRefreshListener(mSwipeRefreshListener);

        mFloatingActionButton = findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(mFabListener);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(mNavigationListener);
        navigationView.setItemIconTintList(null);

        mArticles = new ArrayList<>();
        mArticlesAdapter = new ArticlesAdapter(this, mArticles, mArticleClickListener);

        mIntentReceiver = new IntentReceiver();
        mIntentFilter = new
                IntentFilter("com.altafmahdi.newsheadlines.download.result");
        registerReceiver(mIntentReceiver, mIntentFilter);

        ArticleUtils.loadDataFromDataBase(mArticles, mDataBaseHelper);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mArticlesAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(mRecyclerScrollListener);

        mSadFaceImage = findViewById(R.id.sad_face_image);
        mNoInternetText = findViewById(R.id.no_internet_text);

        if (!Utils.isNetworkAvailable(this)) {
            mSadFaceImage.setVisibility(View.VISIBLE);
            mNoInternetText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setEnabled(true);

        mToolBar.setTitle(mPreferences.getString("toolbar_title"));
        if (Utils.isNetworkAvailable(this)) {
            String provider = mPreferences.getString("provider");
            String search = mPreferences.getString("search");
            if (mPreferences.getBoolean("querySearch")) {
                runTask(search, true);
            } else {
                runTask(provider, false);
            }
        }

        mRecyclerView.scrollToPosition(mPreferences.getInt("position"));
        mRecyclerView.scrollBy(0, - mPreferences.getInt("offset"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecyclerView != null) {
            View firstChild = mRecyclerView.getChildAt(0);
            int firstVisiblePosition = mRecyclerView.getChildAdapterPosition(firstChild);
            int offset = firstChild.getTop();
            mPreferences.saveInt("position", firstVisiblePosition);
            mPreferences.saveInt("offset", offset);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.deleteCache(getApplicationContext());
        ArticleUtils.saveDataToDataBase(mArticles, mDataBaseHelper);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferences.saveInt("position", 0);
        mPreferences.saveInt("offset", 0);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ActivityCompat.finishAffinity(MainActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPreferences.saveString("search", query);
                mPreferences.saveBoolean("querySearch", true);
                mPreferences.saveString("toolbar_title", query);
                mToolBar.setTitle(query);
                mLastQueryWasSearch = true;
                mForceRunTask = true;
                runTask(query, true);
                item.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private RecyclerView.OnScrollListener mRecyclerScrollListener =
            new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                mFloatingActionButton.hide();
            } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                mFloatingActionButton.show();
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mSwipeRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mForceRunTask = true;
            String provider = mPreferences.getString("provider");
            String search = mPreferences.getString("search");
            if (mLastQueryWasSearch) {
                runTask(search, true);
            } else {
                runTask(provider, false);
            }
        }
    };

    private FloatingActionButton.OnClickListener mFabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArticleUtils.parseJsonData(mArticles, mDownloadResult, true);
            Utils.runRecyclerViewAnimation(mRecyclerView);
            if (mFloatingActionButton.getVisibility() == View.VISIBLE) {
                mFloatingActionButton.hide();
            }
            mRecyclerView.scrollToPosition(0);
            mRecyclerView.scrollBy(0, 0);
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mNavigationListener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.bbc) {
                mProvider = "bbc-news";
                mToolBarTitle = mRes.getString(R.string.bbc_news_title);
            } else if (id == R.id.google_uk) {
                mProvider = "google-news-uk";
                mToolBarTitle = mRes.getString(R.string.google_uk_title);
            } else if (id == R.id.daily_mail) {
                mProvider = "daily-mail";
                mToolBarTitle = mRes.getString(R.string.daily_mail_title);
            } else if (id == R.id.mirror) {
                mProvider = "mirror";
                mToolBarTitle = mRes.getString(R.string.mirror_title);
            } else if (id == R.id.independent) {
                mProvider = "independent";
                mToolBarTitle = mRes.getString(R.string.independent_title);
            } else if (id == R.id.the_guardian_uk) {
                mProvider = "the-guardian-uk";
                mToolBarTitle = mRes.getString(R.string.the_guardian_title);
            } else if (id == R.id.cnn) {
                mProvider = "cnn";
                mToolBarTitle = mRes.getString(R.string.cnn_title);
            } else if (id == R.id.fox_news) {
                mProvider = "fox-news";
                mToolBarTitle = mRes.getString(R.string.fox_news_title);
            } else if (id == R.id.al_jazeera_english) {
                mProvider = "al-jazeera-english";
                mToolBarTitle = mRes.getString(R.string.al_jazeera_uk_title);
            } else if (id == R.id.national_geographic) {
                mProvider = "national-geographic";
                mToolBarTitle = mRes.getString(R.string.national_geographic_title);
            } else if (id == R.id.bbc_sport) {
                mProvider = "bbc-sport";
                mToolBarTitle = mRes.getString(R.string.bbc_sport_title);
            } else if (id == R.id.espn) {
                mProvider = "espn";
                mToolBarTitle = mRes.getString(R.string.espn_title);
            } else if (id == R.id.fox_sports) {
                mProvider = "fox-sports";
                mToolBarTitle = mRes.getString(R.string.fox_sports_title);
            } else if (id == R.id.engadget) {
                mProvider = "engadget";
                mToolBarTitle = mRes.getString(R.string.engadget_title);
            } else if (id == R.id.tech_radar) {
                mProvider = "techradar";
                mToolBarTitle = mRes.getString(R.string.techradar_title);
            } else if (id == R.id.tech_crunch) {
                mProvider = "techcrunch";
                mToolBarTitle = mRes.getString(R.string.techcrunch_title);
            } else if (id == R.id.the_verge) {
                mProvider = "the-verge";
                mToolBarTitle = mRes.getString(R.string.the_verge_title);
            } else if (id == R.id.crypto_coins_news) {
                mProvider = "crypto-coins-news";
                mToolBarTitle = mRes.getString(R.string.crypto_news_title);
            }

            mLastQueryWasSearch = false;
            mForceRunTask = true;
            mPreferences.saveBoolean("querySearch", false);
            mPreferences.saveString("provider", mProvider);
            mPreferences.saveString("toolbar_title", mToolBarTitle);
            mSwipeRefreshLayout.setRefreshing(true);
            mToolBar.setTitle(mToolBarTitle);
            mDrawerLayout.closeDrawer(GravityCompat.START);
            runTask(mProvider, false);
            return true;
        }
    };

    private ArticlesAdapter.OnItemClickListener mArticleClickListener =
            new ArticlesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item) {
            Article mArticle = mArticles.get(item);
            String url = mArticle.getUrlLink();

            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    };

    private void runTask(String provider, boolean search) {
        if (Utils.isNetworkAvailable(this)) {
            ArticleUtils.runDownloadTask(MainActivity.this, provider, search);
            mSadFaceImage.setVisibility(View.GONE);
            mNoInternetText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mLayoutManager.scrollToPosition(0);
            registerReceiver(mIntentReceiver, mIntentFilter);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this,
                    mRes.getString(R.string.no_wifi_data_connection_text),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class IntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mDownloadResult = intent.getStringExtra("download_result");
            if (DEBUG) Log.i(TAG,"News: " + mDownloadResult);
            ArticleUtils.parseJsonData(mArticles, mDownloadResult, mForceRunTask);
            if (ArticleUtils.hasDataChanged(mDataBaseHelper) && !mForceRunTask) {
                if (mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                }
            } else if (mForceRunTask) {
                Utils.runRecyclerViewAnimation(mRecyclerView);
                if (mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
            mForceRunTask = false;
            unregisterReceiver(this);
        }
    }
}
