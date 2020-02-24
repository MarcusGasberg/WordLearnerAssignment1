package com.marcus.gasberg.wordlearnerassignment1;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


public class WordRepository implements IWordRepository {
    private LiveData<List<Word>> words;
    private WordDao wordDao;
    private WordDb database;

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
    public LiveData<Word> getWord(final int id) {
        return wordDao.get(id);
    }

    public void insert(final Word word){
        WordDb.execute(new Runnable() {
            @Override
            public void run() {
                wordDao.insert(word);
            }
        });
    }

    @Override
    public void update(final Word word) {
        WordDb.execute(new Runnable() {
            @Override
            public void run() {
                wordDao.update(word);
            }
        });
    }
}
