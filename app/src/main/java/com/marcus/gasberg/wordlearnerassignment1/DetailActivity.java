package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ImageView image;
    private TextView nameTxt;
    private TextView pronunciationTxt;
    private TextView descriptionTxt;
    private TextView notesTxt;
    private TextView scoreTxt;
    private Button cancelBtn;
    private Button editBtn;

    private Word currentWord;
    private Messenger messenger;
    private boolean serviceBound;
    private WordService service;
    private String wordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();

        final Intent intent = getIntent();
        wordId = intent.getStringExtra("word");

        messenger = new Messenger(new IncomingHandler(this));

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
                editIntent.putExtra("word", currentWord.Name);
                startActivity(editIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to the service
        Intent bindIntent = new Intent(this, WordService.class);
        bindIntent.putExtra(WordService.EXTRA_MESSENGER, messenger);
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            unbindService(connection);
            serviceBound = false;
        }
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

    private void bind(Word word) {
        nameTxt.setText(word.Name);
        pronunciationTxt.setText(word.Pronunciation);
        descriptionTxt.setText(word.Description);
        notesTxt.setText(word.Notes);
        scoreTxt.setText(String.valueOf(word.Rating));

        Bitmap bmp = ImageHelpers.getBitmapFromAssets(getApplicationContext(), word.ImagePath);
        if(bmp != null){
            image.setImageBitmap(bmp);
        }
    }

    private class IncomingHandler extends Handler {
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private Observer<Word> wordObserver = new Observer<Word>() {
        @Override
        public void onChanged(Word word) {
            currentWord = word;
            bind(word);
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            serviceBound = true;
            WordService.LocalBinder localBinder  = (WordService.LocalBinder)binder;
            service = localBinder.getService();
            service.getWord(wordId).observeForever(wordObserver);
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceBound = false;
            service.getWord(wordId).removeObserver(wordObserver);
        }
    };
}
