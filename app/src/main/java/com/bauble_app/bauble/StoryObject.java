package com.bauble_app.bauble;

import java.util.*;

/**
 * Created by ChrisLi on 4/20/17.
 */

public class StoryObject implements Comparable<StoryObject>{
    private List<String> children; // Somewhat hacky way of storing story references
    private StoryObject parent;
    private Long durration;
    private Long chains;
    private int expire;
    private String expireDate;
    private Long plays;
    private String title;
    private String author;
    private String content;

    public StoryObject(String title) {
        this.title = title;
    }

    public StoryObject(String title, String author, long durration, long chains, String expireDate, long plays) {
        this.title = title;
        this.author = author;
        this.durration = durration;
        this.chains = chains;
        this.expireDate = expireDate;
        this.plays = plays;
        children = new ArrayList<String>();
    }

    public String getTitle() {
        return this.title;
    }
    public String getAuthor() { return this.author; }
    public Long getDurration() { return this.durration; }
    public Long getChains() { return this.chains; }
    public String getExpireDate() { return this.expireDate; }
    public Long getPlays() { return this.plays; }

    public void addChildStory(StoryObject story) {
        this.children.add(story);
    }

    public String toString() {
        return title + author + durration + chains + expireDate + plays;
    }

    public int compareTo(StoryObject other) {
//        if(equal(other)) {
//            return 0;
//        }
        return this.title.compareTo(other.title);
    }

    private boolean equal(StoryObject other) {
        return this.title.equals(other.title);
    }

    public boolean equals(StoryObject other) {
        return this.title.equals(other.title);
    }


}
