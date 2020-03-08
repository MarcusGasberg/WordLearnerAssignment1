package com.marcus.gasberg.wordlearnerassignment1.Models;

public class Definitions{

    public Definitions(String type,
                       String definition,
                       String example,
                       String imageUrl,
                       String emoji) {
        Type = type;
        Definition = definition;
        Example = example;
        ImageUrl = imageUrl;
        Emoji = emoji;
    }


    public String Type;
    public String Definition;
    public String Example;
    public String ImageUrl;
    public String Emoji;
}
