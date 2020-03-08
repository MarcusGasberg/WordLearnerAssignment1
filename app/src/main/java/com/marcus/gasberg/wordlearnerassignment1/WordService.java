package com.marcus.gasberg.wordlearnerassignment1;

import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.marcus.gasberg.wordlearnerassignment1.Models.ApiWord;
import com.marcus.gasberg.wordlearnerassignment1.Models.ModelHelpers;
import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

import org.json.JSONObject;

import java.util.List;

public class WordService extends IntentService {
    public static final String EXTRA_MESSENGER = "extra_messenger";
    public static final int MSG_ERROR = 1000;
    public static final int MSG_NEW_WORD = 1001;

    private IWordRepository wordRepository;
    private final String wordUrl = "https://owlbot.info/api/v4/dictionary/";
    private RequestQueue requestQueue;
    private Messenger messenger;
    private Gson gson = new Gson();

    public WordService() {
        super("WordService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wordRepository = new WordRepository(getApplication());
        requestQueue  = Volley.newRequestQueue(getApplication());
    }

    LiveData<List<Word>> getWords(){
        return wordRepository.getAllWords();
    }

    LiveData<Word> getWord(String word){
        return wordRepository.getWord(word);
    }

    public void insertWord(final String word){
        getWord(word).observeForever(new Observer<Word>() {
            @Override
            public void onChanged(Word dbWord) {
                if(dbWord != null){
                    return;
                }
                fetchWordAndInsert(word);
            }
        });

    }

    private void fetchWordAndInsert(String word) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
            wordUrl + word,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ApiWord apiWord = gson.fromJson(response.toString(), ApiWord.class);
                    wordRepository.insert(ModelHelpers.Convert(apiWord));
                    try{
                        Message msg = Message.obtain(null, MSG_NEW_WORD, 0, 0);
                        messenger.send(msg);
                    }catch (Exception e){
                        Log.e("Messenger Error:", e.toString());
                    }
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    try{
                        Message msg = Message.obtain(null, MSG_ERROR, 0, 0);
                        messenger.send(msg);
                    } catch (Exception e){
                        Log.e("Messenger Error:", e.toString());
                    }
                }
            });
        requestQueue.add(request);
    }

    void update(Word word) { wordRepository.update(word);}

    public class LocalBinder extends Binder {
        WordService getService() {
            // Return this instance of WordService so clients can call public methods
            return WordService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        if(intent.getExtras() != null){
            // Get Messenger provided by client to notify them of changes
            messenger = (Messenger)intent.getExtras().get(EXTRA_MESSENGER);
        }
        return new LocalBinder();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
