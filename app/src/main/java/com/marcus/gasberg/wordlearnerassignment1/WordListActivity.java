package com.marcus.gasberg.wordlearnerassignment1;

import androidx.appcompat.app.AppCompatActivity;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
    private Context context;
    private WordService service;
    private boolean serviceBound;
    private WordListBroadcastReceiver br;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initRecycler();
        context = this;

        br = new WordListBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WordService.WORD_ADDED);
        filter.addAction(WordService.WORD_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(br, filter);

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
                service.createWord(context, word);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to the service
        Intent bindIntent = new Intent(this, WordService.class);
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

    private class WordListBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "WordListBroadcastReceiver";
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
            case WordService.WORD_ADDED:
            case WordService.WORD_UPDATED:
                List<Word> words = service.getWords();
                adapter.setWords(words);
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
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceBound = false;
        }
    };

}
