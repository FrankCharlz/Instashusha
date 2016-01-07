package com.mj.instashusha.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mj.instashusha.R;
import com.mj.instashusha.activities.DownloadedActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Frank on 12/21/2015.
 */

/****
 * This adapter is very sloooooow,, find time to optimize it
 */
public class FilesListAdapter extends BaseAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<Item> items;

    public FilesListAdapter(Context context, File[] pics, File[] vidz) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        init(pics, vidz);
    }

    private void init(File[] pics, File[] vidz) {
        items = new ArrayList<>();

        for (File f : pics) {
            items.add(new Item(f));
        }

        for (File f : vidz) {
            items.add(new Item(f));
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View cview, ViewGroup viewGroup) {
        View view = cview;
        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view =  inflater.inflate(R.layout.list_item_downloaded, null);

            holder.name = (TextView)view.findViewById(R.id.item_filename);
            holder.fpic = (ImageView) view.findViewById(R.id.item_picha);
            holder.fshare = (ImageView) view.findViewById(R.id.item_share);
            view.setTag(holder);
        } else {
            holder=(ViewHolder)view.getTag();
        }

        Item current_item = items.get(i);

        String fname = current_item.name;
        if (!current_item.isImage) {
            fname = "Video : "+fname;
            holder.fpic.setImageBitmap(current_item.thumbnail);
        } else {
            fname = "Picha : "+fname;
            Picasso.with(context).load(Uri.fromFile(current_item.file)).into(holder.fpic);
        }

        holder.name.setText(fname.substring(0, fname.length()-4));
        holder.fshare.setOnClickListener(new Clix(i));
        holder.fpic.setOnClickListener(new Clix(i));

        return view;
    }


    class Clix implements View.OnClickListener {
        private final int pos;

        public Clix(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.item_share) {
                shareItem(pos);
            } else {
                openItem(pos);
            }

        }
    }
    static class ViewHolder {
        ImageView fpic, fshare;
        TextView name;
    }

    private void openItem(int i) {
        String type;
        if (!items.get(i).isImage)
            type = "video/*";
        else
            type = "image/*";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(items.get(i).file), type);
        context.startActivity(intent);
    }

    private void shareItem(int i) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        share.putExtra(Intent.EXTRA_TEXT, "Shared from @InstaShusha");

        String type;
        if (!items.get(i).isImage)
            type = "video/*";
        else
            type = "image/*";

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        Uri uri = Uri.fromFile(items.get(i).file);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, "Share to"));

    }

    public class Item {
        public long date;
        public boolean isImage;
        public String name;
        public Bitmap thumbnail;
        File file;

        public Item(File f) {
            file = f;
            date = f.lastModified();
            name = f.getName();
            isImage = name.endsWith(".png");
            if (!isImage) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), 0); //if video load kabiisaa
            }
        }
    }



}
