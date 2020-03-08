package com.marcus.gasberg.wordlearnerassignment1.Models;

import java.util.ArrayList;

public class ApiWord {

    public ApiWord(String word,
                   ArrayList<Definitions> definitions,
                   String pronunciation){
        Word = word;
        Definitions = definitions;
        Pronunciation = pronunciation;
    }

    public String Word;
    public ArrayList<Definitions> Definitions;
    public String Pronunciation;
}
