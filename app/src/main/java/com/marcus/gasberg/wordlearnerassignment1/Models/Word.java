package com.marcus.gasberg.wordlearnerassignment1.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_table")
public class Word {
    public Word(){}

    public Word(@NonNull String name){
        Name = name;
    }

    @PrimaryKey
    @NonNull
    public String Name;
    public String Pronunciation;
    public String Description;
    public int Rating;
    public String ImagePath;
    public String Notes;
}


