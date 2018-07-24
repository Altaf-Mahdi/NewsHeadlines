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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.Log;
import android.view.View;

import java.io.File;

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

    public static void animateFab(final View view, boolean show) {
        if (show) {
            view.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            view.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            view.animate()
                    .scaleX(0)
                    .scaleY(0)
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
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
