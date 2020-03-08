package com.marcus.gasberg.wordlearnerassignment1;

import androidx.lifecycle.LiveData;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

import java.util.List;

interface IWordRepository {
    LiveData<List<Word>> getAllWords();
    LiveData<Word> getWord(String word);
    void insert(Word word);
    void update(Word word);
}
