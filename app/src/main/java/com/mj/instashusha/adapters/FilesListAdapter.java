package com.mj.instashusha.adapters;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Frank on 12/21/2015.
 */
public class FilesListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<File> fileArrayList;
    private final LayoutInflater inflater;

    public FilesListAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.fileArrayList = files;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return fileArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileArrayList.get(i);
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
            view.setTag(holder);
        } else {
            holder=(ViewHolder)view.getTag();
        }

        String fname = fileArrayList.get(i).getName();
        if (fname.endsWith(".mp4")) {
            fname = "Video : "+fname;
            holder.fpic.setImageBitmap(ThumbnailUtils.createVideoThumbnail(fileArrayList.get(i).getAbsolutePath(), 0));
        } else {
            fname = "Picha : "+fname;
            Picasso.with(context).load(Uri.fromFile(fileArrayList.get(i))).into(holder.fpic);
        }

        holder.name.setText(fname.substring(0, fname.length()-4));

        return view;
    }

    static class ViewHolder {
        ImageView fpic;
        TextView name;
        int vpos;
    }
}
