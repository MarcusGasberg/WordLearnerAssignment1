package com.marcus.gasberg.wordlearnerassignment1;

import androidx.lifecycle.LiveData;

import java.util.List;

interface IWordRepository {
    LiveData<List<Word>> getAllWords();
    Word getWord(int id);
    void insert(Word word);
}
