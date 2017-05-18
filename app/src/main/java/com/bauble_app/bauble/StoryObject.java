package com.bauble_app.bauble;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ChrisLi on 4/20/17.
 */

public class StoryObject implements Comparable<StoryObject>{

    private String access;
    private String audio;
    private String author;
    private Long chains;
    private List<String> children;  // Somewhat hacky way of storing story references
    private String cover;
    private String created;
    private Long duration;
    private int expire;
    private String expiration;
    private String location;
    private Long plays;
    private int price;
    private String title;
    private StoryObject parent;

    private String uniqueId; // used to store the id of story as on Firebase


    // Constructor that sets all attributes to 0 or null
    // Required for writing to Firebase Database
    public StoryObject() {
        this.access = null;
        this.audio = null;
        this.author = null;
        this.chains = 0L;
        this.children = null;
        this.cover = null;
        this.created = null;
        this.duration = 0L;
        this.expiration = null;
        this.location = null;
        this.plays = 0L;
        this.price = 0;
        this.title = null;
        this.parent = null;

        this.uniqueId = null;
    }

    // Constructor that sets all
    // attributes
    public StoryObject(String access, String audio, String author, Long chain,
                       List<String> children, String cover, String created, Long
                               duration, String
                               expiration, String location, Long play, int
                               price, String title, com.bauble_app.bauble.StoryObject parent) {
        this.access = access;
        this.audio = audio;
        this.author = author;
        this.chains = chain;
        this.children = children;
        this.cover = cover;
        this.created = created;
        this.duration = duration;
        this.expiration = expiration;
        this.location = location;
        this.plays = play;
        this.price = price;
        this.title = title;
        this.parent = parent;
    }

    // Constructor that sets minimal attributes for testing/dev
    // A StoryObject instantiated by this constructor has placeholder or
    // missing attributes for anything other than audio, author, cover, or title
    public StoryObject(String audio, String author, String cover, String
            title) {
        String access = "just_me";

        // audio is a parameter

        // author is a parameter

        Long chain = 0L;

        List<String> children = new ArrayList<>();

        // cover is a parameter

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.US);
        String created = dateFormat.format(new Date());

        Long duration = 0L;

        String expiration = "";
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(created));
            c.add(Calendar.DATE, 7);  // set to expire a week after created
            expiration = dateFormat.format(c.getTime());
        } catch (ParseException e) {
            // do nothing
        }

        String location = "Seattle";

        Long play = 0L;

        int price = 99;

        // title is a parameter

        com.bauble_app.bauble.StoryObject parent = null;

        this.access = access;
        this.audio = audio;
        this.author = author;
        this.chains = chain;
        this.children = children;
        this.cover = cover;
        this.created = created;
        this.duration = duration;
        this.expiration = expiration;
        this.location = location;
        this.plays = play;
        this.price = price;
        this.title = title;
        this.parent = parent;
    }

    // TODO: still needed or just for testing?
    // Constructor for setting a story with just
    public StoryObject(String title) {
        this();
        setTitle(title);
    }

    // Getters
    public String getAccess() {
        return this.access;
    }

    public String getAudio() {
        return this.audio;
    }

    public String getCover() {
        return this.cover;
    }

    public String getCreated() {
        return this.created;
    }

    public StoryObject(String title, String author, long duration, long chains, String expiration, long plays) {
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.chains = chains;
        this.expiration = expiration;
        this.plays = plays;
        this.children = new ArrayList<String>();
    }

    public String getTitle() {
        return this.title;
    }
    public String getAuthor() { return this.author; }
    public Long getChains() { return this.chains; }
    public String getExpireDate() { return this.expiration; }

    public void addChildStory(String storyRef) {
        this.children.add(storyRef);
    }

    public String toString() {
        return title + author + this.duration + this.chains + this.expiration + this.plays;
    }

    public int compareTo(StoryObject other) {
//        if(equal(other)) {
//            return 0;
//        }
        return this.title.compareTo(other.title);
    }

    // currently not used for anthing
    private boolean equal(StoryObject other) {
        return this.title.equals(other.title);
    }

    public boolean equals(StoryObject other) {
        return this.title.equals(other.title);
    }

    public List<String> getChildren() {
        return this.children;
    }

    public Long getDuration() {
        return duration;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getLocation() {
        return location;
    }

    public Long getPlays() {
        return plays;
    }

    public int getPrice() {
        return price;
    }

    public com.bauble_app.bauble.StoryObject getParent() {
        return parent;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    // Setters
    public void setAccess(String access) {
        this.access = access;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setChains(Long chains) {
        this.chains = chains;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPlays(Long plays) {
        this.plays = plays;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setParent(com.bauble_app.bauble.StoryObject parent) {
        this.parent = parent;
    }

    public void setUniqueId(String uniqueId) {
        if (uniqueId == null) {
            throw new IllegalArgumentException();
        }
        this.uniqueId = uniqueId;
    }
}
