package com.bauble_app.bauble;

import java.util.*;

/**
 * Created by ChrisLi on 4/20/17.
 */

public class StoryObject {
    private int durration;
    private List<StoryObject> chains;
    private StoryObject parent;
    private int expire;
    private int plays;
    private String title;
    private String author;
    private String content;

    public StoryObject(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }


}
