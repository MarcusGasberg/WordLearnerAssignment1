package com.marcus.gasberg.wordlearnerassignment1;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_table")
public class Word {
    public Word(){}

    public Word(@NonNull String name){
        Name = name;
    }

    @PrimaryKey(autoGenerate = true)
    public int Id;

    String Name;
    String Pronunciation;
    String Description;
    int Rating;
    String ImageName;
    String Notes;
}
