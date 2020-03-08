package com.marcus.gasberg.wordlearnerassignment1;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

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
        WordDb.executorService.execute(new Runnable() {
            @Override
            public void run() {
                WordDao dao = WordDb.getInstance(context).wordDao();
                dao.deleteAll();

                InputStream csvStream = null;
                AssetManager assManager = context.getAssets();

                try {
                    csvStream = assManager.open("animal_list.csv");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
                    String line = removeUTF8BOM(reader.readLine());

                    while(line != null){
                        String[] data = line.split(";");

                        Word word = new Word(data[NAME_INDEX]);
                        word.Description = data[DESCRIPTION_INDEX];
                        String fileEnding = data[NAME_INDEX].toLowerCase().equals("camel") ? ".png" : ".jpg";
                        word.ImagePath =  "img/" + data[NAME_INDEX].toLowerCase() + fileEnding;
                        word.Pronunciation = data[PRONUNCIATION_INDEX];
                        dao.insert(word);

                        line = reader.readLine();
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

    private static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }
}
