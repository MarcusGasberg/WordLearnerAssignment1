package com.marcus.gasberg.wordlearnerassignment1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.marcus.gasberg.wordlearnerassignment1.Models.Word;

import java.util.List;

@Dao
public interface WordDao {
    @Insert
    void insert(Word word);

    @Update
    void update(Word word);

    @Delete
    void delete(Word word);

    @Query("DELETE FROM word_table")
    void deleteAll();

    @Query("SELECT * FROM word_table")
    LiveData<List<Word>> getAllWords();

    @Query("SELECT * FROM word_table WHERE Name=:name")
    LiveData<Word> get(String name);
}
