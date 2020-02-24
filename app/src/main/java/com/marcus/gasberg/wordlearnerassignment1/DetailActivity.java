package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class DetailActivity extends AppCompatActivity {
    private ImageView image;
    private TextView nameTxt;
    private TextView pronunciationTxt;
    private TextView descriptionTxt;
    private TextView notesTxt;
    private TextView scoreTxt;
    private Button cancelBtn;
    private Button editBtn;

    private WordViewModelFactory viewModelFactory;
    private WordViewModel viewModel;

    private int wordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        initViewModel();

        final Intent intent = getIntent();
        wordId = intent.getIntExtra("id", 0);

        LiveData<Word> currentWord = viewModel.getWord(wordId);

        currentWord.observe(this, new Observer<Word>() {
            @Override
            public void onChanged(@NonNull Word word) {
                bind(word);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(v.getContext(), EditActivity.class);
                editIntent.putExtra("id", wordId);
                startActivity(editIntent);
            }
        });
    }

    private void initViews() {
        image = findViewById(R.id.word_image);
        nameTxt = findViewById(R.id.name_txt);
        pronunciationTxt = findViewById(R.id.pronunciation_txt);
        descriptionTxt = findViewById(R.id.description_txt);
        notesTxt = findViewById(R.id.notes_txt);
        scoreTxt = findViewById(R.id.score_txt);
        cancelBtn = findViewById(R.id.cancel_btn);
        editBtn = findViewById(R.id.edit_btn);
    }

    private void initViewModel() {
        viewModelFactory = new WordViewModelFactory(getApplication());
        viewModel = viewModelFactory.create(WordViewModel.class);
    }

    private void bind(Word word) {
        nameTxt.setText(word.Name);
        pronunciationTxt.setText(word.Pronunciation);
        descriptionTxt.setText(word.Description);
        notesTxt.setText(word.Notes);
        scoreTxt.setText(String.valueOf(word.Rating));

        String path = ImageHelpers.getAnimalPath(word.Name);
        Bitmap bmp = ImageHelpers.getBitmapFromAssets(getApplicationContext(), path);
        image.setImageBitmap(bmp);
    }
}
