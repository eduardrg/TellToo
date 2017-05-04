package com.bauble_app.bauble;
import java.util.*;

/**
 * Created by ChrisLi on 5/4/17.
 */

public class StorySingleton {
    private static final StorySingleton ourInstance = new StorySingleton();

    public static StorySingleton getInstance() {
        return ourInstance;
    }

    List<StoryObject> storyList;

    private StorySingleton() {
        storyList = new ArrayList<StoryObject>();
    }

    public void addStory(StoryObject story) {
        storyList.add(story);
    }


}
