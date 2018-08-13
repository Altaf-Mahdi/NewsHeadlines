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
package com.altafmahdi.newsheadlines.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.altafmahdi.newsheadlines.GlideApp;
import com.altafmahdi.newsheadlines.R;
import com.altafmahdi.newsheadlines.Utils;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Article> mArticleList;
    private OnItemClickListener mClickListener;

    public class MyViewHolder1 extends RecyclerView.ViewHolder {
        public TextView title, description;
        public ImageView image;

        MyViewHolder1(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

        void bind(final int item, final OnItemClickListener clickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(item);
                }
            });
        }
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder {
        public TextView title, description;
        public ImageView image;

        MyViewHolder2(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

        void bind(final int item, final OnItemClickListener clickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(item);
                }
            });
        }
    }

    public ArticlesAdapter(Context context, List<Article> articles,
                           OnItemClickListener clickListener) {
        mContext = context;
        mArticleList = articles;
        mClickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 1;
        if (position == 0) viewType = 0;
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        switch (viewType) {
            case 0:
                View itemViewMain = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_card_main, parent, false);
                return new MyViewHolder1(itemViewMain);
            case 1:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card,
                        parent, false);
                return new MyViewHolder2(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                MyViewHolder1 holder1 = (MyViewHolder1) holder;
                holder1.bind(position, mClickListener);

                GlideApp.with(mContext)
                        .load(article.getImageUrlLink())
                        .centerCrop()
                        .placeholder(Utils.startProgressDrawable(mContext))
                        .error(R.drawable.ic_earth)
                        .into(holder1.image);

                holder1.title.setText(article.getTitle());
                holder1.description.setText(article.getDescription());
                break;
            case 1:
                MyViewHolder2 holder2 = (MyViewHolder2) holder;
                holder2.bind(position, mClickListener);

                GlideApp.with(mContext)
                        .load(article.getImageUrlLink())
                        .centerCrop()
                        .placeholder(Utils.startProgressDrawable(mContext))
                        .error(R.drawable.ic_earth)
                        .into(holder2.image);

                holder2.title.setText(article.getTitle());
                holder2.description.setText(article.getDescription());
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnItemClickListener {
        void onItemClick(int item);
    }
}
