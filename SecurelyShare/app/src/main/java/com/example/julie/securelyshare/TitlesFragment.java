package com.example.julie.securelyshare;


// Displays a list of items that are managed by an adapter similar to
// ListActivity. It provides several methods for managing a list view, such
// as the onListItemClick() callback to handle click events.

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxPath;

import java.util.ArrayList;
import java.util.List;


//adapted from  Fragment List code from Dartmouth (based on Android SDK Sampl COde
public class TitlesFragment extends ListFragment {
    boolean mDualPane;
    int mCurCheckPosition = 0;

    private MyDbxFiles mDbx;
    private DbxFileInfo mCurrentNode = null;
    private DbxFileInfo mRootNode = null;
    private List<DbxFileInfo> fcFileInfo;
    private ArrayList<DbxFileInfo> mFiles = new ArrayList<DbxFileInfo>();
    private CustomAdapter mAdapter = null;

    // onActivityCreated() is called when the activity's onCreate() method
    // has returned.

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new CustomAdapter(getActivity(), R.layout.list_row, mFiles);
        setListAdapter(mAdapter);

      //get DbxFileSystem and root folders
        try {
            this.mDbx = new MyDbxFiles(getActivity());
            fcFileInfo = mDbx.listRoot();
            refreshFileList();
        } catch (DbxException unauthorized) {
            unauthorized.printStackTrace();
        }

      /*
      Check that the second pane to put the details in in visible on
      the screen.
         */
        View detailsFrame = getActivity().findViewById(R.id.right);
        mDualPane = (detailsFrame != null) && (detailsFrame.getVisibility() == View.VISIBLE);
        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showDetails(mCurCheckPosition, true);
        } else {
            // We also highlight in uni-pane just for fun
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            getListView().setItemChecked(mCurCheckPosition, true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Toast.makeText(getActivity(), "onSaveInstanceState",
                Toast.LENGTH_LONG).show();

        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        DbxFileInfo fileInfo = (DbxFileInfo) parent.getItemAtPosition(position);
        try{
            if (position == 1) {
                if (mCurrentNode.compareTo(mRootNode)!=0) {
                    DbxPath p = fileInfo.path.getParent();
                    mCurrentNode = mDbx.getFileInfo(p);
                }
                refreshFileList();
            } else {
                if (fileInfo.isFolder) {
                    mCurrentNode = fileInfo;
                    refreshFileList();
                } else          Toast.makeText(getActivity(), "do decrypt",
                        Toast.LENGTH_LONG).show();


                    // doDecrypt(fileInfo);
            }

        } catch (DbxException e1) {
            e1.printStackTrace();
        }

 //       boolean displayable = doDecrypt(fileInfo);
        showDetails(position, true);
    }

    // Helper function to show the details of a selected item, either by
    // displaying a fragment in-place in the current UI, or starting a whole
    // new activity in which it is displayed.

    private void showDetails(int position, boolean displayable) {
        mCurCheckPosition = position;

        // If landscape we can display both fragments else need to
        // start a second activity to show the details fragment
        if (mDualPane) {
            getListView().setItemChecked(position, true);
            getListView().setBackgroundColor(Color.BLUE);
            // Check what fragment is currently shown, replace if needed.
            FragmentDetails details = (FragmentDetails) getFragmentManager()
                    .findFragmentById(R.id.right);
            if (details == null ) {
                // Make new fragment to show this selection.
     //           details = FragmentDetails.newInstance(position);
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.replace(R.id.left, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            // That is: if this is a single-pane (e.g., portrait mode on a
            // phone) then fire DetailsActivity to display the details
            // fragment

            // Create an intent for starting the DetailsActivity
            Intent intent = new Intent();
            // explicitly set the activity context and class
            // associated with the intent (context, class)
            intent.setClass(getActivity(), DetailsActivity.class);
            // pass the current position
            intent.putExtra("position", position);
            intent.putExtra("displayable", "This is the data to be displayed");
            startActivity(intent);
        }
    }
    private void refreshFileList() throws DbxException {
        if (mRootNode == null) mRootNode = mDbx.getFileInfo(mDbx.getRoot());
        if (mCurrentNode == null) mCurrentNode = mRootNode;
        DbxFileInfo mLastNode = mCurrentNode;
        fcFileInfo = mDbx.listFolder(mCurrentNode.path);
        mFiles.clear();
        mFiles.add(mRootNode);
        mFiles.add(mLastNode);
        mFiles.addAll(fcFileInfo);
        mAdapter.notifyDataSetChanged();
    }

 /*   private boolean doDecrypt(DbxFileInfo fileInfo)  {
        File myPlaintextFile;
        Boolean displayable;
        /* work out how we are goint to show output
         * If file is type .txt and size <2GB show as editText
         * I >2GB or type .pdf (or any other) - offer the user the chance to write plaintext
         * to external cache and open using the app of their choice
         * NB we will clear the cache in

         Log.e("size ", " "+ fileInfo.size);

        if  ( (fileInfo.size <  1800000) && (fileInfo.path.getName().endsWith(".txt"))) {
            displayable = true;
        } else  displayable = false;

        //open a file input stream with given path
        if (displayable) try {
            DbxFile dbxIn = mDbx.getInFile(fileInfo);
            FileInputStream fis = dbxIn.getReadStream();

            myPlaintextFile = getFos(fileInfo.path.getName());
            FileOutputStream fos = new FileOutputStream(myPlaintextFile);
            //decrypt file
            FileCryptor.decryptFile(fis, fos);
            dbxIn.close();
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeystoreAccessException e) {
            e.printStackTrace();
        }
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
*/

}
