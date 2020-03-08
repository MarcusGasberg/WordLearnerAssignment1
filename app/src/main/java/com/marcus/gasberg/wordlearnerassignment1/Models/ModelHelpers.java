package com.marcus.gasberg.wordlearnerassignment1.Models;

public class ModelHelpers {

    public static Word Convert(ApiWord word){
        Word val = new Word(word.Word);
        val.Pronunciation = word.Pronunciation;
        val.Description = word.Definitions.get(0).Definition;
        val.ImagePath = word.Definitions.get(0).ImageUrl;

        return val;
    }
}
