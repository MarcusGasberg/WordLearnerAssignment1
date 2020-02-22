package com.marcus.gasberg.wordlearnerassignment1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class WordViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public WordViewModelFactory(Application application){
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WordViewModel(application);
    }
}
