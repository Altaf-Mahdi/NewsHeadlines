/*
 * Copyright (c) 2018 NewsHeadlines
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

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.Log;

import com.altafmahdi.newsheadlines.recyclerview.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class Utils {

    private static final String TAG = "Utils";
    private static final boolean DEBUG = false;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void runDownloadTask(Context context, String value, boolean search) {
        if (DEBUG) Log.i(TAG, "Download task running");
        DownloadTask task = new DownloadTask(context);
        String url;
        String apiKey = context.getResources().getString(R.string.api_key);

        if (!search) {
            if (DEBUG) Log.i(TAG, "Provider task");
            url = "https://newsapi.org/v2/top-headlines?sources=" + value +
                    "&apiKey=" + apiKey;
        } else {
            if (DEBUG) Log.i(TAG, "Search task");
            url = "https://newsapi.org/v2/top-headlines?q=" + value +
                    "&apiKey=" + apiKey;
        }

        task.execute(url);
    }

    public static void parseJsonData(List<Article> list, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("articles");

            if (list.size() > 0) {
                list.clear();
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonPart = jsonArray.getJSONObject(i);

                String link = jsonPart.getString("url");
                String image = jsonPart.getString("urlToImage");
                String title = jsonPart.getString("title");
                String description = jsonPart.getString("description");

                list.add(new Article(link, image, title, description));
            }

            for (int i = 0; i < list.size(); i++) {
                Article article = list.get(i);

                if (DEBUG) {
                    Log.i(TAG, "Title: " + article.getTitle());
                    Log.i(TAG, "Descripion: " + article.getDescription());
                    Log.i(TAG, "Url: " + article.getUrlLink());
                    Log.i(TAG, "Image: " + article.getImageUrlLink());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static CircularProgressDrawable startProgressDrawable(Context context) {
        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(context);
        circularProgressDrawable.setColorSchemeColors(refreshColors(context));
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        return circularProgressDrawable;
    }

    public static int[] refreshColors(Context context) {
        Resources res = context.getResources();
        int[] colors = {res.getColor(R.color.swipe_refresh_color_red),
                res.getColor(R.color.swipe_refresh_color_green),
                res.getColor(R.color.swipe_refresh_color_yellow),
                res.getColor(R.color.swipe_refresh_color_blue)};

        return colors;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
                if (DEBUG) Log.i(TAG, "Deleted cache");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    /*public static void openFragment(FragmentActivity activity, Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.container, fragment, "currentFragment");
        fragmentTransaction.addToBackStack("container");
        fragmentTransaction.commit();
    }*/

    /*public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/
}
