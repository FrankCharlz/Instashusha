package com.mj.instashusha_tigo.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mj.instashusha_tigo.R;
import com.mj.instashusha_tigo.utils.Media;
import com.mj.instashusha_tigo.utils.Sharer;
import com.mj.instashusha_tigo.utils.VideoThumbnailCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Frank on 1/9/2016.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {


    private final Context context;
    private List<File> items;

    public FileListAdapter(Context context, File[] items) {
        this.context = context;
        this.items = new ArrayList<>();
        Collections.addAll(this.items, items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_downloaded, parent, false);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            ((CardView) view).setRadius(0f);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.bottomMargin = 0;
            params.topMargin = 0;
            //view.setLayoutParams(params);
        }

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File currentItem = items.get(position);

        if (!Media.isImage(currentItem.getName())) {
            holder.fpic.setImageBitmap(VideoThumbnailCache.getBitmap(currentItem.getAbsolutePath()));
            holder.fPlayButton.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(context).load(currentItem).into(holder.fpic);
        }

        holder.name.setText(currentItem.getName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final int TAG_FPIC = 0x0023;
        private static final int TAG_FSHARE = 0xf9;
        private final TextView name;
        private final ImageView fpic, fshare, fPlayButton;


        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_filename);
            fpic = (ImageView) view.findViewById(R.id.item_picha);
            fshare = (ImageView) view.findViewById(R.id.item_share);
            fPlayButton = (ImageView) view.findViewById(R.id.item_play_button);

            fpic.setOnClickListener(this);
            fshare.setOnClickListener(this);

            fpic.setTag(TAG_FPIC);
            fshare.setTag(TAG_FSHARE);

        }

        @Override
        public void onClick(View view) {
            //uses tag and position to determine right method to call
            if (view.getTag().equals(TAG_FPIC)) {
                Media.openItem(context, items.get(getAdapterPosition()));
            } else if (view.getTag().equals(TAG_FSHARE)) {
                Sharer.share(context, items.get(getAdapterPosition()), false, null);
            }
        }
    }


}
