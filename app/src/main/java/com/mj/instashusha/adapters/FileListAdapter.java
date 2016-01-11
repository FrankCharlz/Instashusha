package com.mj.instashusha.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mj.instashusha.R;
import com.mj.instashusha.utils.Sharer;
import com.mj.instashusha.utils.VideoThumbnailCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Frank on 1/9/2016.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {


    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<File> items;

    public FileListAdapter(Context context, ArrayList<File> items) {
        this.context = context;
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_downloaded, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File current_item = items.get(position);
        String fname = current_item.getName();
        boolean isImage = fname.endsWith("png");

        fname = fname.substring(fname.length()-10, fname.length()-5);
        if (!isImage) {
            fname = "Video : "+fname;
            holder.fpic.setImageBitmap(VideoThumbnailCache.getBitmap(current_item.getAbsolutePath()));
        } else {
            fname = "Picha : "+fname;
            Picasso.with(context).load(Uri.fromFile(current_item)).into(holder.fpic);
        }

        holder.name.setText(fname);

        Clix clix = new Clix(current_item);
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

        private final File file;

        public Clix(File file) {
            this.file = file;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.item_share) {
                Sharer.share(context, file);
            } else {
                openItem(file);
            }

        }
    }

    private void openItem(File file) {
        String mime;
        if (file.getName().endsWith("png"))
            mime = "image/*";
        else
            mime = "video/*";
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), mime);
        context.startActivity(intent);
    }
}
