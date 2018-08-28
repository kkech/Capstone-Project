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
package com.kk.capstone.model;

import android.content.SharedPreferences;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class Contract {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    public static final String COLUMN_ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_CHANNEL = "channel";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_CHANNEL_KEY = "channelKey";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_ARTICLE = "article";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_DATE = "date";

    public static final String CHANNEL1_KEY = "key_channel1";
    public static final String CHANNEL2_KEY = "key_channel2";
    public static final String CHANNEL3_KEY = "key_channel3";
    public static final String CHANNEL4_KEY = "key_channel4";
    public static final String CHANNEL5_KEY = "key_channel5";

    public static final String[] NEWS_KEYS = {
            CHANNEL1_KEY, CHANNEL2_KEY, CHANNEL3_KEY, CHANNEL4_KEY, CHANNEL5_KEY
    };

    public static String findCurrentFollowers(SharedPreferences preferences) {

        Boolean isFollowing = false;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(COLUMN_CHANNEL_KEY).append(" IN  (");

        for (String key : NEWS_KEYS) {
            if (preferences.getBoolean(key, false)) {
                isFollowing = true;
                stringBuilder.append("'").append(key).append("'");
                stringBuilder.append(",");
            }
        }
        if(isFollowing.equals(false))
            return "1=2";
        stringBuilder.setLength(stringBuilder.length() - 1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}