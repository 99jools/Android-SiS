package com.example.julie.securelyshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.sync.android.DbxFileInfo;

import java.util.ArrayList;

/**
 * Created by jrs300 on 06/08/14.
 * Based on code taken from  http://www.techrepublic.com/blog/software-engineer/android-coders-guide-to-browsing-external-storage/
 */

    public class CustomAdapter extends ArrayAdapter<DbxFileInfo> {
        private ArrayList<DbxFileInfo> items;
        private Context c = null;

//constructor
        public CustomAdapter(Context context, int textViewResourceId, ArrayList<DbxFileInfo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.c = context;
        }

        /**
         * Code invoked when container notifies data set of change.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_row, null);
            }
            TextView filename = null;
            ImageView fileicon = null;
            DbxFileInfo fileInfo = items.get(position);
            if (fileInfo != null) {
                filename = (TextView) v.findViewById(R.id.filename);
                fileicon = (ImageView) v.findViewById(R.id.fileicon);
            }
            if (filename != null) {
                if (position == 0) {
                    filename.setText(fileInfo.path.toString());

                } else if (position == 1) {
                    filename.setText(fileInfo.path.toString());
                } else {
                    filename.setText(fileInfo.path.getName());
                }
            }
            if (fileicon != null) {
                if (position == 0) {
                    fileicon.setImageResource(R.drawable.root);
                } else if (position == 1) {
                    fileicon.setImageResource(R.drawable.up);
                } else if (fileInfo.isFolder) {
                    fileicon.setImageResource(R.drawable.folder);
                } else {
                    fileicon.setImageResource(R.drawable.file);
                }
            }
            return v;
        }
    }
