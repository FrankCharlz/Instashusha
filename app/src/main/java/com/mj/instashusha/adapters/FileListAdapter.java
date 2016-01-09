package com.mj.instashusha.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mj.instashusha.R;
import com.mj.instashusha.utils.DownloadedItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Frank on 1/9/2016.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {


    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<DownloadedItem> items;
    private Clix clix;
    private int clicked_pos;

    public FileListAdapter(Context context, File[] pics, File[] vids) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init(pics, vids);
    }

    private void init(File[] pics, File[] vids) {
        items = new ArrayList<>();

        for (File f : pics) {
            items.add(new DownloadedItem(f));
        }

        for (File f : vids) {
            items.add(new DownloadedItem(f));
        }

        Collections.sort(items, new Comparator<DownloadedItem>() {
            @Override
            public int compare(DownloadedItem a, DownloadedItem b) {
                return Long.valueOf(b.date).compareTo(a.date);
            }
        });

        clix = new Clix();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_downloaded, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DownloadedItem current_item = items.get(position);

        String fname = current_item.name;
        fname = fname.substring(fname.length()-10, fname.length()-4);
        if (!current_item.isImage) {
            fname = "Video : "+fname;
            holder.fpic.setImageBitmap(current_item.thumbnail);
        } else {
            fname = "Picha : "+fname;
            Picasso.with(context).load(Uri.fromFile(current_item.file)).into(holder.fpic);
        }

        clicked_pos = position;
        holder.name.setText(fname);
        holder.fshare.setOnClickListener(clix);
        holder.fpic.setOnClickListener(clix);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView fpic, fshare;

        public ViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.item_filename);
            fpic = (ImageView) view.findViewById(R.id.item_picha);
            fshare = (ImageView) view.findViewById(R.id.item_share);
        }
    }

    class Clix implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.item_share) {
                //shareItem(clicked_pos);
            } else {
                //openItem(clicked_pos);
            }

        }
    }
}
