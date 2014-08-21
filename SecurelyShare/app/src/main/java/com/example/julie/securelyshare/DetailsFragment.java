package com.example.julie.securelyshare;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public  class DetailsFragment extends Fragment {

  private MyDbxFiles mDbx;
    // Create a new instance of DetailsFragment, initialized to show the
    // text at 'index'.

    public static DetailsFragment newInstance(int index) {
        DetailsFragment f = new DetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }


    // We create the UI with a scrollview and text and return a reference to
    // the scoller which is then drawn to the screen

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            this.mDbx = new MyDbxFiles(getActivity());
        } catch (DbxException.Unauthorized unauthorized) {
            unauthorized.printStackTrace();
        }

        Toast.makeText(getActivity(), "DetailsFragment:onCreateView",
                Toast.LENGTH_LONG).show();
        //
        // if (container == null) {
        // // We have different layouts, and in one of them this
        // // fragment's containing frame doesn't exist. The fragment
        // // may still be created from its saved state, but there is
        // // no reason to try to create its view hierarchy because it
        // // won't be displayed. Note this is not needed -- we could
        // // just run the code below, where we would create and return
        // // the view hierarchy; it would just never be used.
        // return null;
        // }

        // If non-null, this is the parent view that the fragment's UI
        // should be attached to. The fragment should not add the view
        // itself, but this can be used to generate the LayoutParams of
        // the view.
        //

        DbxFile fileInfo = mDbx.
        doDecrypt(fileInfo);
        // programmatically create a scrollview and texview for the text in
        // the container/fragment layout. Set up the properties and add the
        // view.

        ScrollView scroller = new ScrollView(getActivity());
        TextView text = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getActivity()
                        .getResources().getDisplayMetrics());
        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);
        text.setText("decryption complete");
        return scroller;
    }


    private void doDecrypt(DbxPath path) throws IOException, MissingPwdException, GeneralSecurityException {
        //open a file input stream with given path
        DbxFile dbxIn = mDbx.getInFile(fileInfo);
        FileInputStream fis = dbxIn.getReadStream();

        File myPlaintextFile  = getFos(fileInfo.path.getName());
        FileOutputStream fos = new FileOutputStream(myPlaintextFile);
//      DbxFile myPlaintextFile  = getFos(fileInfo.path.getName());
//      FileOutputStream fos = myPlaintextFile.getWriteStream();
        FileCryptor.decryptFile(fis, fos);
        dbxIn.close();
        // start new intent to open
        Uri myUri = Uri.fromFile(myPlaintextFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(myUri);
        startActivity(intent);
    }

    private File getFos(String out) throws IOException {
        //sort out filemame for decrypted file
        out = out.substring(0, out.length() - 4);
        return new File(getActivity().getExternalCacheDir(),out);
    } //end getFos
}

