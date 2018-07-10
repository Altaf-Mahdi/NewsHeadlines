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

public class Article {

    private String mUrlLink;
    private String mImageUrlLink;
    private String mTitle;
    private String mDescription;

    public Article(String urlLink, String imageUrlLink, String title, String description) {
        mUrlLink = urlLink;
        mImageUrlLink = imageUrlLink;
        mTitle = title;
        mDescription = description;
    }

    public String getUrlLink() {
        return mUrlLink;
    }

    public String getImageUrlLink() {
        return mImageUrlLink;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}
