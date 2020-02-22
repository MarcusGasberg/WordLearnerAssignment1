package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.NonNull;

public class Word {
    public Word(@NonNull int id, @NonNull String name){
        Id = id;
        Name = name;
    }

    public int Id;
    public String Name;
    public String Pronunciation;
    public String Description;
    public int Rating;
    public String ImageName;
    public String Notes;
}
