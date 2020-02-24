package com.marcus.gasberg.wordlearnerassignment1;

import androidx.lifecycle.LiveData;

import java.util.List;

interface IWordRepository {
    LiveData<List<Word>> getAllWords();
    LiveData<Word> getWord(int id);
    void insert(Word word);
    void update(Word word);
}
