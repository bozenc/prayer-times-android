/*
 * Copyright (c) 2013-2017 Metin Kale
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

package com.metinkale.prayer.times.alarm.sounds;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.crashlytics.android.Crashlytics;
import com.metinkale.prayer.App;
import com.metinkale.prayer.times.alarm.Alarm;

import java.io.IOException;

import lombok.Cleanup;
import lombok.Getter;

public class UserSound extends Sound {

    @Getter
    private final Uri uri;

    public static UserSound create(Uri uri) {
        UserSound sound = (UserSound) Sounds.getSound(uri.hashCode());
        if (sound == null) {
            sound = new UserSound(uri);
        }
        return sound;
    }

    private UserSound(Uri uri) {
        this.uri = uri;
    }

    @Override
    public String getName() {
        String result = null;
        if (uri.getScheme().equals("content")) {
            @Cleanup Cursor cursor = App.get().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public String getShortName() {
        return getName();
    }

    @Override
    public MediaPlayer createMediaPlayer(Alarm alarm) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(App.get(), getUri());
        } catch (IOException e) {
            Crashlytics.logException(e);
            return null;
        }
        return mp;
    }

    @Override
    public boolean isDownloaded() {
        return true;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getId() {
        return uri.hashCode();
    }
}
