package com.bauble_app.bauble;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        String titleString = data.get(position).getTitle();
        title.setText(titleString);
        TextView author = (TextView) vi.findViewById(R.id.feed_listitem_author);
        String authorString = data.get(position).getAuthor();
        author.setText(authorString);
        TextView time = (TextView) vi.findViewById(R.id.feed_listitem_length);
        time.setText("00:" + data.get(position).getDuration().toString());
        TextView chains = (TextView) vi.findViewById(R.id.feed_listitem_chains);
        chains.setText(data.get(position).getChains().toString());
        TextView expire = (TextView) vi.findViewById(R.id.feed_listitem_expire);
        expire.setText(data.get(position).getExpireDate());
        TextView plays = (TextView) vi.findViewById(R.id.feed_listitem_plays);
        plays.setText(data.get(position).getPlays().toString());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl("gs://bauble-90a48.appspot.com");
        String imagePath = authorString + titleString.replace(" ", "");
        StorageReference pathReference = storageReference.child("thumbnails/" + imagePath + ".png");

        // ImageView in your Activity
        ImageView imageView = (ImageView) vi.findViewById(R.id.feed_listitem_picture);

        // Load the image using Glide
        Glide.with(context /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(imageView);

        // Set font
        title.setTypeface(tf);

        return vi;
    }
}
