package com.marcus.gasberg.wordlearnerassignment1;

import androidx.appcompat.app.AppCompatActivity;

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
    ImageView image;
    TextView nameTxt;
    TextView pronunciationTxt;
    TextView descriptionTxt;
    TextView notesTxt;
    TextView scoreTxt;

    Button cancelBtn;
    Button editBtn;

    WordViewModelFactory viewModelFactory;
    WordViewModel viewModel;

    Word currentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        image = findViewById(R.id.word_image);
        nameTxt = findViewById(R.id.name_txt);
        pronunciationTxt = findViewById(R.id.pronunciation_txt);
        descriptionTxt = findViewById(R.id.description_txt);
        notesTxt = findViewById(R.id.notes_txt);
        scoreTxt = findViewById(R.id.score_txt);

        cancelBtn = findViewById(R.id.cancel_btn);
        editBtn = findViewById(R.id.edit_btn);

        final Intent intent = getIntent();
        int wordId = intent.getIntExtra("index", -1);

        viewModelFactory = new WordViewModelFactory(getApplication());
        viewModel = viewModelFactory.create(WordViewModel.class);
        currentWord = viewModel.getWord(wordId);

        try{
            InputStream stream = getAssets().open("img/" + currentWord.ImageName);
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            image.setImageBitmap(bmp);
        }catch (Exception e){
            e.printStackTrace();
        }

        nameTxt.setText(currentWord.Name);
        pronunciationTxt.setText(currentWord.Pronunciation);
        descriptionTxt.setText(currentWord.Description);
        notesTxt.setText(currentWord.Notes);
        scoreTxt.setText(String.valueOf(currentWord.Rating));

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
