package com.mj.instashusha.adapters;

import android.content.Context;
import android.content.Intent;
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
    private final ArrayList<DownloadedActivity.Item> items;
    private final LayoutInflater inflater;

    public FilesListAdapter(Context context, ArrayList<DownloadedActivity.Item> items) {
        this.context = context;
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        DownloadedActivity.Item current_item = items.get(i);

        String fname = current_item.name;
        if (!current_item.isImage) {
            fname = "Video : "+fname;
        } else {
            fname = "Picha : "+fname;
        }


        holder.fpic.setImageBitmap(current_item.thumbnail);
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
        int vpos;
    }

    private void openItem(int i) {
        String type;
        if (!items.get(i).isImage)
            type = "video/*";
        else
            type = "image/*";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(items.get(i).uri, type);
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
        Uri uri = items.get(i).uri;

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, "Share to"));

    }


}
