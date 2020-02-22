package com.marcus.gasberg.wordlearnerassignment1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private LiveData<List<Word>> words;
    private final IWordRepository wordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new DummyWordRepository(application);
        words = wordRepository.getAllWords();
    }

    public LiveData<List<Word>> getWords(){
        return words;
    }

    public Word getWord(int id){
        return wordRepository.getWord(id);
    }

    public void insertWord(Word word){
        wordRepository.insert(word);
    }

    public void update(Word word) { wordRepository.update(word);}
}
