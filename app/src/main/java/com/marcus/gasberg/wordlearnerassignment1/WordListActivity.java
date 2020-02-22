package com.marcus.gasberg.wordlearnerassignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class WordListActivity extends AppCompatActivity {
    private RecyclerView wordRecycler;
    private WordViewModelFactory viewModelFactory;
    private WordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordRecycler = findViewById(R.id.word_recycler_view);
        final WordListAdapter adapter = new WordListAdapter(this);
        wordRecycler.setAdapter(adapter);
        wordRecycler.setLayoutManager(new LinearLayoutManager(this));

        viewModelFactory = new WordViewModelFactory(getApplication());
        viewModel = new ViewModelProvider(this, viewModelFactory).get(WordViewModel.class);

        viewModel.getWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                adapter.setWords(words);
            }
        });
    }
}
