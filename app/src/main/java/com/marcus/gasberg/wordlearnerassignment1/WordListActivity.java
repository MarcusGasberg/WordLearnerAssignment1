package com.marcus.gasberg.wordlearnerassignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class WordListActivity extends AppCompatActivity {
    private RecyclerView wordRecycler;
    private Button exitBtn;
    private WordViewModelFactory viewModelFactory;
    private WordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordRecycler = findViewById(R.id.word_recycler_view);
        exitBtn = findViewById(R.id.exit_btn);

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

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exitIntent = new Intent(Intent.ACTION_MAIN);
                exitIntent.addCategory(Intent.CATEGORY_HOME);
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
            }
        });
    }
}
