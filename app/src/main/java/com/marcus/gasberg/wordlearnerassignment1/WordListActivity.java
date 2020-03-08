package com.marcus.gasberg.wordlearnerassignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

import java.util.List;

public class WordListActivity extends AppCompatActivity {
    private RecyclerView wordRecycler;
    private Button exitBtn;
    private WordListAdapter adapter;
    private Button addBtn;
    private TextView searchTxt;

    private Messenger messenger = null;
    private WordService service;
    private boolean serviceBound = false;
    private Context context;
    private Observer<List<Word>> wordsObserver = new Observer<List<Word>>() {
        @Override
        public void onChanged(List<Word> words) {
            adapter.setWords(words);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initRecycler();
        context = this;
        messenger = new Messenger(new IncomingHandler(context));

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exitIntent = new Intent(Intent.ACTION_MAIN);
                exitIntent.addCategory(Intent.CATEGORY_HOME);
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = searchTxt.getText().toString();
                word = word.substring(0,1).toUpperCase() + word.substring(1);
                service.insertWord(word);
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

    private void initView() {
        wordRecycler = findViewById(R.id.word_recycler_view);
        exitBtn = findViewById(R.id.exit_btn);
        addBtn = findViewById(R.id.add_btn);
        searchTxt = findViewById(R.id.search_txt);
    }

    private void initRecycler() {
        adapter = new WordListAdapter(this);
        wordRecycler.setAdapter(adapter);
        wordRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    class IncomingHandler extends Handler {
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WordService.MSG_ERROR:
                    Toast.makeText(applicationContext,
                            "Something went wrong while fetching word!",
                            Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            serviceBound = true;
            WordService.LocalBinder localBinder  = (WordService.LocalBinder)binder;
            service = localBinder.getService();
            service.getWords().observeForever(wordsObserver);
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceBound = false;
            service.getWords().removeObserver(wordsObserver);
        }
    };

}
