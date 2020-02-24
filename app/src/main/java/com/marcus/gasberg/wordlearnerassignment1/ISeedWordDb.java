package com.marcus.gasberg.wordlearnerassignment1;

import android.content.Context;

import androidx.room.Database;

public interface ISeedWordDb {
    void seed(Context context, WordDb db);
}
