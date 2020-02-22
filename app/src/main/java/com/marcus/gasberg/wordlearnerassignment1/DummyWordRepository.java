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


public class DummyWordRepository implements IWordRepository {
    private MutableLiveData<List<Word>> words;
    private static final int NAME_INDEX = 0;
    private static final int PRONUNCIATION_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;


    public DummyWordRepository(Application application){
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
                String imageName = data[NAME_INDEX].toLowerCase() + ".jpg";
                csvWords.add(new Word(data[NAME_INDEX],
                        data[PRONUNCIATION_INDEX],
                        data[DESCRIPTION_INDEX],  imageName));
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

    @Override
    public LiveData<List<Word>> getAllWords(){
        return words;
    }

    @Override
    public Word getWord(int id) {
        return words.getValue().get(id);
    }

    public void insert(Word word){
        List<Word> currentWords = words.getValue();
        currentWords.add(word);
        words.setValue(currentWords);
    }
}
