package com.marcus.gasberg.wordlearnerassignment1;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;


public class WordService extends IntentService {
    public static final String EXTRA_MESSENGER = "EXTRA_MESSENGER";

    public static final String WORD_ADDED = "WORD_ADDED";
    public static final String WORD_UPDATED = "WORD_UPDATED";
    public static final String ERROR = "ERROR";

    private static final String ACTION_INSERT = "ACTION_INSERT";
    private static final String ACTION_UPDATE = "ACTION_UPDATE";
    private static final String ACTION_SET_CURRENT_WORD = "ACTION_SET_CURRENT_WORD";

    private final String wordUrl = "https://owlbot.info/api/v4/dictionary/";
    private RequestQueue requestQueue;
    private Gson gson = new Gson();
    private WordDb db;
    private static List<Word> words;
    private static Word currentWord;
    private Messenger messenger;
    private Context applicationContext;

    public WordService() {
        super("WordService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        db = WordDb.getInstance(applicationContext);

        requestQueue = Volley.newRequestQueue(applicationContext);

        Notification notification = getNotification();
        startForeground(1234, notification);

        Messenger databaseSeederMessenger = new Messenger(
                new DatabaseIncomingHandler());

        Intent seedIntent = new Intent(this, WordDbSeederService.class);
        seedIntent.setAction(WordDbSeederService.ACTION_SEED);
        seedIntent.putExtra(WordDbSeederService.EXTRA_MESSENGER, databaseSeederMessenger);
        WordDbSeederService.enqueueWork(this, seedIntent);
    }

    public Word getCurrentWord(){
        return currentWord;
    }

    public List<Word> getWords(){
        return words;
    }

    public void updateWord(Context context, Word word) {
        Intent insertIntent = new Intent(context, WordService.class);
        insertIntent.putExtra("word", word);
        insertIntent.setAction(ACTION_UPDATE);
        context.startService(insertIntent);
    }

    public void setCurrentWord(Context context, String word){
        Intent insertIntent = new Intent(context, WordService.class);
        insertIntent.putExtra("word", word);
        insertIntent.setAction(ACTION_SET_CURRENT_WORD);
        context.startService(insertIntent);
    }

    public void createWord(Context context, String word){
        Intent insertIntent = new Intent(context, WordService.class);
        insertIntent.putExtra("word", word);
        insertIntent.setAction(ACTION_INSERT);
        context.startService(insertIntent);
    }

    private void fetchWordAndInsert(String word) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
            wordUrl + word,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ApiWord apiWord = gson.fromJson(response.toString(), ApiWord.class);
                    db.wordDao().insert(ModelHelpers.Convert(apiWord));
                    sendMessage(WORD_ADDED);
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    sendMessage(ERROR);
                }
            });
        requestQueue.add(request);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String action = intent.getAction();
        if(action == null){
            return;
        }

        switch (action){
            case ACTION_INSERT:
                handleInsert(intent);
                break;
            case ACTION_SET_CURRENT_WORD:
                handleSetCurrentWord(intent);
                break;
            case ACTION_UPDATE:
                handleUpdate(intent);
                break;
            default:
                break;
        }
    }

    private void handleUpdate(Intent intent) {
        if (intent.getExtras() == null) {
            return;
        }
        Word word = (Word)intent.getSerializableExtra("word");
        Word dbWord = db.wordDao().get(word.Name);
        if(dbWord == null){
            return;
        }

        db.wordDao().update(word);
        words = db.wordDao().getAllWords();
        sendMessage(WORD_UPDATED);
    }


    private void handleInsert(Intent intent) {
        String word = intent.getStringExtra("word");
        Word dbWord = db.wordDao().get(word);
        if(dbWord != null){
            return;
        }
        fetchWordAndInsert(word);
    }

    private void handleSetCurrentWord(Intent intent) {
        String word = intent.getStringExtra("word");
        currentWord = db.wordDao().get(word);
        sendMessage(WORD_UPDATED);
    }

    private void sendMessage(String msgType){
        Intent intent = new Intent();
        intent.setAction(msgType);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    public class LocalBinder extends Binder {
        WordService getService() {
            // Return this instance of WordService so clients can call public methods
            return WordService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        initClientProvidedMessenger(intent);
        return new LocalBinder();
    }

    private void initClientProvidedMessenger(Intent intent){
        if(intent.getExtras() != null){
            // Get Messenger provided by client to notify them of changes
            messenger = (Messenger)intent.getExtras().get(EXTRA_MESSENGER);
        }
    }

    class DatabaseIncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WordDbSeederService.MSG_DATABASE_SEEDED:
                    WordDb.executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            words = db.wordDao().getAllWords();
                            WordService.this.sendMessage(WORD_ADDED);
                        }
                    });
                break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    // https://stackoverflow.com/questions/47531742/startforeground-fail-after-upgrade-to-android-8-1
    private Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle(getText(R.string.app_name))
                .setPriority(PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE);

        return builder.build();
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        String name = getText(R.string.app_name).toString();
        String id = "channel_id";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(id, name, importance);

        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        } else {
            stopSelf();
        }
        return id;
    }
}
