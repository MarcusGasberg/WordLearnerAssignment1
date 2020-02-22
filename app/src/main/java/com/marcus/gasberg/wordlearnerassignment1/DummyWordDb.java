package com.marcus.gasberg.wordlearnerassignment1;

import android.app.Application;
import android.content.res.AssetManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DummyWordDb {
    private static DummyWordDb ourInstance;
    private static MutableLiveData<List<Word>> words;
    private static final int NAME_INDEX = 0;
    private static final int PRONUNCIATION_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;

    public static DummyWordDb getInstance(Application application) {
        if(ourInstance == null){
            ourInstance = new DummyWordDb(application);
        }
        return ourInstance;
    }

    private DummyWordDb(Application application) {
        words =  new MutableLiveData<>();
        AssetManager assManager = application.getAssets();
        InputStream csvStream = null;

        try {
            csvStream = assManager.open("animal_list.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
            ArrayList<Word> csvWords = new ArrayList<>();
            String line;

            while((line = reader.readLine()) != null){
                String[] data = line.split(";");

                int id = csvWords.size();
                Word wordToAdd = new Word(id, data[NAME_INDEX]);
                wordToAdd.Description = data[DESCRIPTION_INDEX];
                wordToAdd.ImageName = data[NAME_INDEX].toLowerCase() + ".jpg";
                wordToAdd.Pronunciation = data[PRONUNCIATION_INDEX];

                csvWords.add(wordToAdd);
            }

            this.words.setValue(csvWords);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                csvStream.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public LiveData<List<Word>> getWords(){
        return words;
    }

    public void setWords(List<Word> words){
        this.words.setValue(words);
    }
}
