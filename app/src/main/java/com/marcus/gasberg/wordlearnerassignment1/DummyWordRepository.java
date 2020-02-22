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
    private LiveData<List<Word>> words;
    private DummyWordDb database;

    public DummyWordRepository(Application application){
        database = DummyWordDb.getInstance(application);
        words = database.getWords();
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
        database.setWords(currentWords);
    }

    @Override
    public void update(Word word) {
        List<Word> currentWords = words.getValue();
        currentWords.set(word.Id, word);
        database.setWords(currentWords);
    }
}
