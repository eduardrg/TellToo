package com.bauble_app.bauble;
import java.util.*;

/**
 * Created by ChrisLi on 5/4/17.
 */

public class StorySingleton {
    private static final StorySingleton ourInstance = new StorySingleton();
    private Map<String, StoryObject> storyMap;
    private ArrayList<String> mKeys;
    private String viewKey;

    public static StorySingleton getInstance() {
        return ourInstance;
    }

    private StorySingleton() {
        storyMap = new LinkedHashMap<String, StoryObject>();
        mKeys = new ArrayList<String>();
        viewKey = "";
    }

    public void addStory(StoryObject story) {
        mKeys.add(story.grabUniqueId());
        storyMap.put(story.grabUniqueId(), story);
    }

    public StoryObject getViewStory() {
        if (this.storyMap.containsKey(this.viewKey)) {
            return this.storyMap.get(this.viewKey);
        }
        return null;
    }

    public StoryObject getStory(int index) {
        if (index >= this.mKeys.size()) {
            return null;
        } else {
            String key = this.mKeys.get(index);
            return this.getStory(key);
        }
    }

    public StoryObject getStory(String key) {
        if (this.storyMap.containsKey(key)) {
            return this.storyMap.get(key);
        }
        return null;
    }

    public String getViewKey() {
        return this.viewKey;
    }

    public void setViewStory(int index) {
        this.viewKey = this.mKeys.get(index);
    }

    public void setViewKey(String key) {
        this.viewKey = key;
    }

    // return story to view form list of stories loaded
    public int getViewStoryIndex() {
        return this.mKeys.indexOf(viewKey);
    }

    public boolean containsStory(StoryObject story) {
        return this.storyMap.containsKey(story.grabUniqueId());
    }

    public Map<String, StoryObject> getStoryMap() {
        return storyMap;
    }

    public ArrayList<String> getKeys() {
        return this.mKeys;
    }
}
