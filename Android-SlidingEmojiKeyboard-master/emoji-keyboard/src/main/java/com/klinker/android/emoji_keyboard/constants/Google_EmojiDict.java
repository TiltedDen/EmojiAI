package com.klinker.android.emoji_keyboard.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Google_EmojiDict {
    private Map<String, Integer> emojiDict = new HashMap<String, Integer>();
    private ArrayList<Integer> transIconIds;
    private ArrayList<Integer> thingsIconIds;
    private ArrayList<Integer> peopleIconIds;
    private ArrayList<Integer> otherIconIds;
    private ArrayList<Integer> natureIconIds;

    public Google_EmojiDict(){
        setDict();
    }

    public Map<String, Integer> get_dict(){
        return emojiDict;
    }

    private void setDict(){
        EmojiIcons icons = getPreferedIconSet();
        natureIconIds = icons.getNatureIconIds();
        otherIconIds = icons.getOtherIconIds();
        peopleIconIds = icons.getPeopleIconIds();
        thingsIconIds = icons.getThingsIconIds();
        transIconIds = icons.getTransIconIds();

        for (int i = 0; i < EmojiTexts.transEmojiTexts.length; i++){
            emojiDict.put(EmojiTexts.transEmojiTexts[i], transIconIds.get(i));
        }
        for (int i = 0; i < EmojiTexts.thingsEmojiTexts.length; i++){
            emojiDict.put(EmojiTexts.thingsEmojiTexts[i], thingsIconIds.get(i));
        }
        for (int i = 0; i < EmojiTexts.peopleEmojiTexts.length; i++){
            emojiDict.put(EmojiTexts.peopleEmojiTexts[i], peopleIconIds.get(i));
        }
        for (int i = 0; i < EmojiTexts.otherEmojiTexts.length; i++){
            emojiDict.put(EmojiTexts.otherEmojiTexts[i], otherIconIds.get(i));
        }
        for (int i = 0; i < EmojiTexts.natureEmojiTexts.length; i++){
            emojiDict.put(EmojiTexts.natureEmojiTexts[i], natureIconIds.get(i));
        }
    }

    private EmojiIcons getPreferedIconSet() {
        return new Google_EmojiIcons();
    }
}
