package com.mj.instashusha;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.adapters.FilesListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FinisherActivity extends AppCompatActivity {

    private FilesListAdapter adapter;
    private ListView listViewFiles;
    final private  ArrayList<File> filesArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finisher);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();
        mAdView.loadAd(adRequest);

        File pics[] = new File(InstagramApp.getAppPhotoFolder()).listFiles();
        File vids[] = new File(InstagramApp.getAppVideoFolder()).listFiles();

        for (File f : pics) filesArray.add(f);
        for (File f : vids) filesArray.add(f);

        //sort by last modified...
        Collections.sort(filesArray, new Comparator<File>() {
            @Override
            public int compare(File f2, File f1) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        adapter = new FilesListAdapter(this, filesArray);
        listViewFiles = (ListView) findViewById(R.id.list_view_files);

        listViewFiles.setAdapter(adapter);
        listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InstagramApp.toast(getApplicationContext(), filesArray.get(i).getName());

                if (view.getId() == R.id.item_share) {
                    shareItem(i);
                } else {
                    openItem(i);
                }
            }
        });




    }

    private void openItem(int i) {
        String fpath = filesArray.get(i).getAbsolutePath();
        String type;
        if (fpath.endsWith(".mp4"))
            type = "video/*";
        else
            type = "image/*";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(filesArray.get(i)), type);
        startActivity(intent);
    }

    private void shareItem(int i) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        share.putExtra(Intent.EXTRA_TEXT, "Shared from @InstaShusha");

        String fpath = filesArray.get(i).getAbsolutePath();
        String type;

        if (fpath.endsWith(".mp4"))
            type = "video/*";
        else
            type = "image/*";

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(fpath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));

    }
}
