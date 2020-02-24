package com.marcus.gasberg.wordlearnerassignment1;

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

    private int wordId;

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
        wordId = intent.getIntExtra("id", 0);

        viewModelFactory = new WordViewModelFactory(getApplication());
        viewModel = viewModelFactory.create(WordViewModel.class);

        LiveData<Word> currentWord = viewModel.getWord(wordId);

        currentWord.observe(this, new Observer<Word>() {
            @Override
            public void onChanged(Word word) {
                if(word != null){
                    setWord(word);
                }
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

    private void setWord(Word word) {

        try{
            InputStream stream = getAssets().open("img/" + word.ImageName);
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            image.setImageBitmap(bmp);
        }catch (Exception e){
            e.printStackTrace();
        }

        nameTxt.setText(word.Name);
        pronunciationTxt.setText(word.Pronunciation);
        descriptionTxt.setText(word.Description);
        notesTxt.setText(word.Notes);
        scoreTxt.setText(String.valueOf(word.Rating));
    }
}
