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
import com.mj.instashusha.utils.Media;
import com.mj.instashusha.utils.Sharer;
import com.mj.instashusha.utils.VideoThumbnailCache;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Frank on 1/9/2016.
 *
 *
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {


    private final Context context;
    private File[] items;

    public FileListAdapter(Context context, File[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_downloaded, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File currentItem = items[position];

        if (!Media.isImage(currentItem.getName())) {
            holder.fpic.setImageBitmap(VideoThumbnailCache.getBitmap(currentItem.getAbsolutePath()));
        } else {
            Picasso.with(context).load(currentItem).into(holder.fpic);
        }

        holder.name.setText(currentItem.getName());

    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final int TAG_FPIC = 0x0023;
        private static final int TAG_FSHARE = 0xf9;
        private final TextView name;
        private final ImageView fpic, fshare;


        public ViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.item_filename);
            fpic = (ImageView) view.findViewById(R.id.item_picha);
            fshare = (ImageView) view.findViewById(R.id.item_share);

            fpic.setOnClickListener(this);
            fshare.setOnClickListener(this);

            fpic.setTag(TAG_FPIC);
            fshare.setTag(TAG_FSHARE);

        }

        @Override
        public void onClick(View view) {
            //uses tag and position to determine right method to call
            if (view.getTag().equals(TAG_FPIC)) {
                Media.openItem(context, items[getAdapterPosition()]);
            }
            else if (view.getTag().equals(TAG_FSHARE)) {
                Sharer.share(context, items[getAdapterPosition()]);
            }
        }
    }


}
