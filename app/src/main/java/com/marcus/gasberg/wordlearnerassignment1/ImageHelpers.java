package com.marcus.gasberg.wordlearnerassignment1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

class ImageHelpers {
    static Bitmap getBitmapFromAssets(Context context, String path){
        InputStream stream = null;
        Bitmap bmpResult = null;
        try{
            stream = context.getAssets().open(path);

            bmpResult = BitmapFactory.decodeStream(stream);
        } catch(IOException e){
            // File doesn't exists
        } finally {
            try {
                stream.close();
            }catch (Exception ignored){ }
        }
        return bmpResult;
    }

    static String getAnimalPath(String animalName){
        return "img/"+animalName.toLowerCase()+".jpg";
    }
}
