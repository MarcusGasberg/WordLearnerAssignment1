package com.marcus.gasberg.wordlearnerassignment1;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DummyWordDbSeeder extends RoomDatabase.Callback {
    private static final int NAME_INDEX = 0;
    private static final int PRONUNCIATION_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private Context context;

    DummyWordDbSeeder(Context context){
        this.context = context;
    }


    @Override
    public void onOpen(@NonNull SupportSQLiteDatabase db) {
        super.onOpen(db);
        WordDb.execute(new Runnable() {
            @Override
            public void run() {
                InputStream csvStream = null;
                AssetManager assManager = context.getAssets();

                try {
                    csvStream = assManager.open("animal_list.csv");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
                    String line;

                    while((line = reader.readLine()) != null){
                        String[] data = line.split(";");

                        Word word = new Word(data[NAME_INDEX]);
                        word.Description = data[DESCRIPTION_INDEX];
                        word.ImageName = data[NAME_INDEX].toLowerCase() + ".jpg";
                        word.Pronunciation = data[PRONUNCIATION_INDEX];

                        WordDb.getInstance(context).wordDao().insert(word);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if ( csvStream != null){
                            csvStream.close();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
