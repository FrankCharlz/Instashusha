package com.mj.instashusha.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.adapters.FilesListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DownloadedActivity extends AppCompatActivity {

    final private  ArrayList<File> filesArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finisher);

        AdView mAdView = (AdView) findViewById(R.id.adView_activity_finisher);
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings);
        btnMenu.setOnClickListener(new MenuClick(this));

        File pics[] = new File(InstagramApp.PHOTO_FOLDER_PATH).listFiles();
        File vids[] = new File(InstagramApp.VIDEO_FOLDER_PATH).listFiles();

        Collections.addAll(filesArray, pics);
        Collections.addAll(filesArray, vids);


        ArrayList<Item> items = new ArrayList<>();

        for (File f : pics) {
            items.add(new Item(f));
        }

        for (File f : vids) {
            items.add(new Item(f));
        }

        //sort by date modified..
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item0, Item item1) {
                return Long.valueOf(item1.date).compareTo(item0.date);
            }
        });



        FilesListAdapter adapter = new FilesListAdapter(this, items);
        ListView listViewFiles = (ListView) findViewById(R.id.list_view_files);
        listViewFiles.setAdapter(adapter);
    }

    public class Item {
        public long date;
        public boolean isImage;
        public String name;
        public Uri uri;
        public Bitmap thumbnail;

        public Item(File f) {
            date = f.lastModified();
            name = f.getName();
            isImage = name.endsWith(".png");
            uri = Uri.fromFile(f);
            thumbnail = (isImage)
                    ? BitmapFactory.decodeFile(f.getAbsolutePath()) // if image
                    : ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), 0); //if video
        }
    }

}
