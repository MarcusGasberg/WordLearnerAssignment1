package com.marcus.gasberg.wordlearnerassignment1;

import android.content.Intent;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WordDbSeederService extends JobIntentService {
    private static final int NAME_INDEX = 0;
    private static final int PRONUNCIATION_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;

    private static final int JOB_ID = 2010;

    public static final String EXTRA_MESSENGER = "EXTRA_MESSENGER";
    public static final int MSG_DATABASE_SEEDED = 2001;
    public static final String ACTION_SEED = "ACTION_SEED";
    private Messenger messenger;

    public WordDbSeederService() {

    }
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WordDbSeederService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (ACTION_SEED.equals(action)) {
            seed();

            try{
                messenger = (Messenger)intent.getExtras().get(EXTRA_MESSENGER);
                Message msg = Message.obtain(null, MSG_DATABASE_SEEDED, 0, 0);
                messenger.send(msg);
            }catch (Exception e){
                Log.e("Messenger Error:", e.toString());
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toast("All work complete");
    }

    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(WordDbSeederService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seed(){
        Context context = getBaseContext();
        WordDao dao = WordDb.getInstance(context).wordDao();

        if(dao.getAllWords() == null){
            return;
        }

        InputStream csvStream = null;
        AssetManager assManager = context.getAssets();

        try {
            csvStream = assManager.open("animal_list.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
            String line = removeUTF8BOM(reader.readLine());

            while(line != null){
                String[] data = line.split(";");

                Word word = new Word(data[NAME_INDEX]);
                word.Description = data[DESCRIPTION_INDEX];
                String fileEnding = data[NAME_INDEX].toLowerCase().equals("camel") ? ".png" : ".jpg";
                word.ImagePath =  "img/" + data[NAME_INDEX].toLowerCase() + fileEnding;
                word.Pronunciation = data[PRONUNCIATION_INDEX];
                dao.insert(word);

                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if ( csvStream != null){
                    csvStream.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }
}
