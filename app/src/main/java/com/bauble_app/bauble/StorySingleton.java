package com.bauble_app.bauble;
import java.util.*;

/**
 * Created by ChrisLi on 5/4/17.
 */

public class StorySingleton {
    private static final StorySingleton ourInstance = new StorySingleton();
    private Map<String, StoryObject> storyMap; // story key to story object
    private Map<String, StoryObject> userOwnedStories; // local collection for demo purposes
    private List<String> userOwnedKeys;
    private List<String> mKeys;
    private String viewKey;
    private String graphKey;
    private String userName;

    private int donationProgress;

    public static StorySingleton getInstance() {
        return ourInstance;
    }

    private StorySingleton() {
        storyMap = new LinkedHashMap<String, StoryObject>();
        userOwnedStories = new LinkedHashMap<String, StoryObject>();
        userOwnedKeys = new  ArrayList<String>();
        mKeys = new ArrayList<String>();
        viewKey = "";
        graphKey = "";
    }

    public void addOwnedStory(String key, StoryObject story) {
        this.userOwnedStories.put(key, story);
    }

    public void addOwnedKey(String key) {
        this.userOwnedKeys.add(key);
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

    public void setDonationProgress(int i) {
        this.donationProgress = i;
    }

    public int getDonationProgress() {
        return this.donationProgress;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // TODO: Invesitigate whether this object is still needed
    // owned stories now caculated with user keys
    public Map<String, StoryObject> getOwnedStoriesMap() {
        return this.userOwnedStories;
    }

    public List<String> getOwnedKeys() {
        return this.userOwnedKeys;
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

    public void putStory(StoryObject so) {
        if (so != null) {
            String key = so.grabUniqueId();
            this.storyMap.put(key, so);
            if (!this.mKeys.contains(key)) {
                this.mKeys.add(key);
            }
        }
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

    public List<String> getKeys() {
        return this.mKeys;
    }

    public String getGraphKey() {
        return graphKey;
    }

    public void setGraphKey(String graphKey) {
        this.graphKey = graphKey;
    }

    public boolean isEmpty() {
        return this.storyMap.isEmpty();
    }
}
