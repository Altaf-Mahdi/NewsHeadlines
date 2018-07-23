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
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Preferences {

    private static final String NEWS_PREFS = "news_prefs";

    private SharedPreferences prefs;

    public Preferences(Context context) {
        prefs = context.getSharedPreferences(NEWS_PREFS,
                Context.MODE_PRIVATE);
    }

    public void saveString(String name, String value) {
        prefs.edit().putString(name, value).apply();
    }

    public String getString(String name) {
        return prefs.getString(name, "No value defined");
    }

    public void saveBoolean(String name, boolean value) {
        prefs.edit().putBoolean(name, value).apply();
    }

    public boolean getBoolean(String name) {
        return prefs.getBoolean(name, true);
    }

    public void saveStringSet(String name, Set<String> list) {
        prefs.edit().putStringSet(name, list).apply();
    }

    public Set<String> getStringSet(String name) {
        return prefs.getStringSet(name, new HashSet<String>());
    }

    public void saveInt(String name, int value) {
        prefs.edit().putInt(name, value).apply();
    }

    public int getInt(String name) {
        return prefs.getInt(name, 0);
    }

    public void saveLong(String name, long value) {
        prefs.edit().putLong(name, value).apply();
    }

    public long getLong(String name) {
        return prefs.getLong(name, 0);
    }
}
