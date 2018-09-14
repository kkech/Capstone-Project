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

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.capstone.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Cursor mData;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mData.moveToPosition(position);

        System.out.println("[][[][] : " + System.currentTimeMillis());
        long minutes = Math.round((System.currentTimeMillis() - mData.getLong(2)) / (1000 * 60));
        String date = "\u2022 " + String.valueOf(minutes) + "m";

        holder.messageTextView.setText(mData.getString(1));
        holder.authorTextView.setText(mData.getString(0));
        holder.dateTextView.setText(date);
        holder.authorImageView.setImageResource(R.drawable.ic_person);
    }

    @Override
    public int getItemCount() {
        if (null == mData)
            return 0;
        return mData.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mData = newCursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView authorTextView;
//        TextView messageTextView;
//        TextView dateTextView;
//        ImageView authorImageView;

        @BindView(R.id.author_text_view) TextView authorTextView;
        @BindView(R.id.message_text_view) TextView messageTextView;
        @BindView(R.id.date_text_view) TextView dateTextView;
        @BindView(R.id.author_image_view) ImageView authorImageView;




        public ViewHolder(View layoutView) {
            super(layoutView);
            ButterKnife.bind(this, layoutView);


//            authorTextView = (TextView) layoutView.findViewById(R.id.author_text_view);
//            messageTextView = (TextView) layoutView.findViewById(R.id.message_text_view);
//            dateTextView = (TextView) layoutView.findViewById(R.id.date_text_view);
//            authorImageView = (ImageView) layoutView.findViewById(R.id.author_image_view);
        }
    }
}
