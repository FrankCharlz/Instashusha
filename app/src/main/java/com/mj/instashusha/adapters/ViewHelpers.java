package com.mj.instashusha.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.mj.instashusha.MyApp;
import com.mj.instashusha.R;

import java.io.File;

/**
 * Created by Frank on 6/10/2016.
 *
 */
public class ViewHelpers {

    public static void showPopUpMenu(final Context context, final View view,
                                     final File file, final DeleteListener deleteListener) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.photo_clicked_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if ( item.getItemId() == R.id.photo_menu_item_delete_image) {
                    boolean res = file.delete();
                    MyApp.log("File deleted ? : "+res);
                    if (res) {
                        deleteListener.onDeleted();
                    }
                }
                return true;
            }
        });

        popup.show(); //showing popup menu


    }

    public interface DeleteListener {
        void onDeleted();
    }
}
