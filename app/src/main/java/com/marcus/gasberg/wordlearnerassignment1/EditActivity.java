package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

public class EditActivity extends AppCompatActivity {
    TextView nameTxt;
    TextView pronunciationTxt;
    TextView notesTxt;
    TextView scoreTxt;
    SeekBar scoreBar;
    Button cancelBtn;
    Button okBtn;

    private Word currentWord;
    private WordService service;
    private String notes;
    private int rating;
    private String wordId;
    private boolean serviceBound;
    private Messenger messenger;
    private Observer<Word> wordObserver = new Observer<Word>() {
        @Override
        public void onChanged(Word word) {
            currentWord = word;
            bind(word);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();

        final Intent intent = getIntent();
        wordId = intent.getStringExtra("word");

        messenger = new Messenger(new EditActivity.IncomingHandler(this));

        if (savedInstanceState != null) {
            notes = savedInstanceState.getString("notes");
            rating = savedInstanceState.getInt("rating");
        }

        scoreBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rating = progress;
                scoreTxt.setText(String.valueOf(rating));
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
                Word update = currentWord;
                if(update != null){
                    update.Notes = notes;
                    update.Rating = rating;
                    service.update(update);
                }

                startActivity(result);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("rating", rating);
        outState.putString("notes", notes);
        super.onSaveInstanceState(outState);
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
        notesTxt.setText(notes);
        scoreTxt.setText(String.valueOf(rating));
        scoreBar.setProgress(rating);
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
