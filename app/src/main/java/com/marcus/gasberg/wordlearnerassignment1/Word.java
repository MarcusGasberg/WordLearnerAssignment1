package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.NonNull;

public class Word {
    public Word(@NonNull String name){
        Name = name;
    }

    public Word(@NonNull String name, String pronunciation, String description){
        Name = name;
        Pronunciation = pronunciation;
        Description = description;
    }
    public Word(@NonNull String name, String pronunciation, String description, String imageName){
        Name = name;
        Pronunciation = pronunciation;
        Description = description;
        ImageName = imageName;
    }

    public String Name;
    public String Pronunciation;
    public String Description;
    public double Rating;
    public String ImageName;
    public String Notes;
}
