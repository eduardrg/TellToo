package com.bauble_app.bauble;
import java.util.*;

/**
 * Created by ChrisLi on 5/4/17.
 */

public class StorySingleton {
    private static final StorySingleton ourInstance = new StorySingleton();

    public List<StoryObject> storyList; // BAD STYLE TODO: make get list method
    private int viewStory;

    public static StorySingleton getInstance() {
        return ourInstance;
    }

    private StorySingleton() {
        storyList = new ArrayList<StoryObject>();
        viewStory = 0;
    }

    public void addStory(StoryObject story) {
        storyList.add(story);
    }

    public StoryObject getViewStory() {
        return this.storyList.get(viewStory);
    }

    public StoryObject getStory(int index) {
        if (index >= this.storyList.size()) {
            return null;
        }
        return this.storyList.get(index);
    }

    public int getViewIndex() {
        return this.viewStory;
    }

    public void setViewStory(int index) {
        this.viewStory = index;
    }

    // return story to view form list of stories loaded
    public int getViewStoryIndex() {
        return this.viewStory;
    }

    public boolean containsStory(StoryObject story) {
        for (int i = 0; i < storyList.size(); i++) {
            if (storyList.get(i).equals(story)) {
                return true;
            }
        }
        return false;
    }

}
