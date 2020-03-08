package com.marcus.gasberg.wordlearnerassignment1;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

import java.util.ArrayList;
import java.util.List;


public class WordRepository implements IWordRepository {
    private WordDao wordDao;
    private WordDb database;
    private LiveData<List<Word>> words;
    private LiveData<Word> currentWord;

    WordRepository(Application application){
        database = WordDb.getInstance(application);
        wordDao = database.wordDao();
        words = wordDao.getAllWords();
    }

    @Override
    public LiveData<List<Word>> getAllWords(){
        return words;
    }

    @Override
    public LiveData<Word> getWord(final String name) {
        currentWord = wordDao.get(name);
        return currentWord;
    }

    public void insert(final Word word){
        WordDb.executorService.execute(new Runnable() {
            @Override
            public void run() {
                wordDao.insert(word);
            }
        });
    }

    @Override
    public void update(final Word word) {
        WordDb.executorService.execute(new Runnable() {
            @Override
            public void run() {
                wordDao.update(word);
            }
        });
    }
}
