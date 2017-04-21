package com.bauble_app.bauble;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.*;

/**
 * Created by ChrisLi on 4/20/17.
 */

public class FeedAdapter extends BaseAdapter {
    private Context context;
    private List<StoryObject> data;
    private static LayoutInflater inflater = null;
    private Typeface tf;

    public FeedAdapter(Context context, List<StoryObject> data) {
        // TODO Auto-generated constructor stub
        // Initialize font
        this.tf = Typeface.createFromAsset(context.getAssets(), "fonts/unused/Lato-Italic.ttf");
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.listitem_feed, null);
        }

        TextView title = (TextView) vi.findViewById(R.id.feed_listitem_title);
        title.setText(data.get(position).getTitle());
        // Set font
        title.setTypeface(tf);

        return vi;
    }
}
