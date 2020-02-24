package com.marcus.gasberg.wordlearnerassignment1;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.room.Room.databaseBuilder;

/*
* Database pattern inspired from:
* https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
*/

@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordDb extends RoomDatabase {
    public abstract WordDao wordDao();
    private static volatile WordDb instance;

    private static final int NUM_THREADS = 4;
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(NUM_THREADS);

    static void execute(Runnable command){
        executorService.execute(command);
    }

    static WordDb getInstance(final Context context){
        if (instance == null) {
            synchronized (WordDb.class) {
                if (instance == null) {
                    instance = databaseBuilder(context.getApplicationContext(),
                        WordDb.class, "word_db")
                        .addCallback(new DummyWordDbSeeder(context))
                        .build();
                }
            }
        }
        return instance;
    }
}
