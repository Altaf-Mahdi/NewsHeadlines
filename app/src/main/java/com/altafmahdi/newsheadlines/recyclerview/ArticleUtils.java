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
package com.altafmahdi.newsheadlines.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.altafmahdi.newsheadlines.DataBaseHelper;
import com.altafmahdi.newsheadlines.DownloadTask;
import com.altafmahdi.newsheadlines.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ArticleUtils {

    private static final String TAG = "ArticleUtils";
    private static final boolean DEBUG = true;

    private static String mCompareLink;

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

    public static void parseJsonData(List<Article> list, String result, boolean forceUpdate) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("articles");

            if (forceUpdate) {
                if (list.size() > 0) {
                    list.clear();
                }
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonPart = jsonArray.getJSONObject(i);

                if (i == 0) {
                    mCompareLink = jsonPart.getString("url");
                }

                String link = jsonPart.getString("url");
                String image = jsonPart.getString("urlToImage");
                String title = jsonPart.getString("title");
                String description = jsonPart.getString("description");

                if (forceUpdate) {
                    list.add(new Article(link, image, title, description));
                }
            }

            if (DEBUG) Log.i(TAG,"Compare link: " + mCompareLink);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasDataChanged(DataBaseHelper dataBaseHelper) {
        Cursor cursor = dataBaseHelper.getData(String.valueOf(1));
        String oldLink = "";
        if (cursor.moveToFirst()) {
            oldLink = cursor.getString(1);
        }

        if (DEBUG) Log.i(TAG,"Old link: " + oldLink);

        return !oldLink.equals(mCompareLink);
    }

    public static void saveDataToDataBase(List<Article> list, DataBaseHelper dataBaseHelper) {
        dataBaseHelper.deleteDataBase();
        for (int i = 0; i < list.size(); i++) {
            Article article = list.get(i);
            String link = article.getUrlLink();
            String image = article.getImageUrlLink();
            String title = article.getTitle();
            String description = article.getDescription();

            dataBaseHelper.insertData(link, image, title, description);
        }
        dataBaseHelper.close();
    }

    public static void loadDataFromDataBase(List<Article> list, DataBaseHelper dataBaseHelper) {
        Cursor cursor = dataBaseHelper.getAllData();
        while (cursor.moveToNext()) {
            String link = cursor.getString(1);
            String image = cursor.getString(2);
            String title = cursor.getString(3);
            String description = cursor.getString(4);

            list.add(new Article(link, image, title, description));
        }
    }
}
