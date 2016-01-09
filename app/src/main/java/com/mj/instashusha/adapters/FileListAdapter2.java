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
import com.mj.instashusha.utils.Item;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Frank on 1/9/2016.
 */
public class FileListAdapter2 extends RecyclerView.Adapter<FileListAdapter2.ViewHolder> {


    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<Item> items;
    private Clix clix;
    private int clicked_pos;

    public FileListAdapter2(Context context, File[] pics, File[] vids) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init(pics, vids);
    }

    private void init(File[] pics, File[] vids) {
        items = new ArrayList<>();

        for (File f : pics) {
            items.add(new Item(f));
        }

        for (File f : vids) {
            items.add(new Item(f));
        }

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
        Item current_item = items.get(position);

        String fname = current_item.name;
        if (!current_item.isImage) {
            fname = "Video : "+fname;
            holder.fpic.setImageBitmap(current_item.thumbnail);
        } else {
            fname = "Picha : "+fname;
            Picasso.with(context).load(Uri.fromFile(current_item.file)).into(holder.fpic);
        }

        clicked_pos = position;
        holder.name.setText(fname.substring(0, fname.length()-4));
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
