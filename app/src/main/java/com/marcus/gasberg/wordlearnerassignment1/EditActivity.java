package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {
    TextView nameTxt;
    TextView pronunciationTxt;
    TextView notesTxt;
    TextView scoreTxt;
    SeekBar scoreBar;
    Button cancelBtn;
    Button okBtn;

    private int wordId;
    private WordViewModel viewModel;
    private LiveData<Word> currentWord;
    private String notes;
    private int rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();
        initViewModel();

        final Intent intent = getIntent();
        wordId = intent.getIntExtra("id", 0);


        currentWord = viewModel.getWord(wordId);

        currentWord.observe(this, new Observer<Word>() {
            @Override
            public void onChanged(@NonNull Word word) {
                bind(word);
            }
        });

        scoreBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    rating = progress;
                    scoreTxt.setText(String.valueOf(rating));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        notesTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notes = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(v.getContext(), WordListActivity.class);
                result.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Word update = currentWord.getValue();
                if(update != null){
                    update.Notes = notes;
                    update.Rating = rating;
                    viewModel.update(update);
                }

                startActivity(result);
            }
        });
    }

    private void initViewModel() {
        WordViewModelFactory viewModelFactory = new WordViewModelFactory(getApplication());
        viewModel = viewModelFactory.create(WordViewModel.class);
    }

    private void initView() {
        nameTxt = findViewById(R.id.name_txt);
        pronunciationTxt = findViewById(R.id.pronunciation_txt);
        notesTxt = findViewById(R.id.notes_txt);
        scoreTxt = findViewById(R.id.score_txt);
        scoreBar = findViewById(R.id.score_bar);
        cancelBtn = findViewById(R.id.cancel_btn);
        okBtn = findViewById(R.id.edit_btn);
    }

    private void bind(Word word){
        nameTxt.setText(word.Name);
        pronunciationTxt.setText(word.Pronunciation);
        notesTxt.setText(word.Notes);
        scoreTxt.setText(String.valueOf(word.Rating));
        scoreBar.setProgress(word.Rating);
    }
}
