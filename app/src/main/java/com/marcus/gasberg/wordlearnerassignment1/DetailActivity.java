package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class DetailActivity extends AppCompatActivity {
    private static final int EDIT_REQUEST = 3000;
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
    private DetailsBroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();

        final Intent intent = getIntent();
        wordId = intent.getStringExtra("word");

        br = new DetailsBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WordService.WORD_UPDATED);
        filter.addAction(WordService.ERROR);
        LocalBroadcastManager.getInstance(this).registerReceiver(br, filter);


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
                startActivityForResult(editIntent, EDIT_REQUEST);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to the service
        Intent bindIntent = new Intent(this, WordService.class);
        bindIntent.putExtra(WordService.EXTRA_MESSENGER, messenger);
        getApplicationContext().bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            getApplicationContext().unbindService(connection);
            serviceBound = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DetailActivity.EDIT_REQUEST){
            setResult(RESULT_OK);
            finish();
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

    private class DetailsBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "DetailsBroadcastReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null){
                handleMessageReceived(action);
            }
        }
    }

    private void handleMessageReceived(String action) {
        switch (action) {
            case WordService.WORD_UPDATED:
                currentWord = service.getCurrentWord();
                bind(currentWord);
                break;
            case WordService.ERROR:
                Toast.makeText(getApplicationContext(),
                        "Something went wrong while fetching word!",
                        Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
    }


    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            serviceBound = true;
            WordService.LocalBinder localBinder  = (WordService.LocalBinder)binder;
            service = localBinder.getService();
            service.setCurrentWord(getApplicationContext(), wordId);
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceBound = false;
        }
    };
}
