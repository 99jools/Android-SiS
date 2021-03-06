package com.example.jrs300.shareinsecret;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileChooserActivity extends ListActivity {
    ListView fcListView;
    DbxAccountManager fcDbxAcctMgr;
    DbxFileSystem fcDbxFileSystem;
    List<DbxFileInfo> fcFileInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        //get DbxFileSystem
        try {
            fcDbxAcctMgr = new DropboxSync(this.getApplicationContext()).getAccMgr();
            fcDbxFileSystem = DbxFileSystem.forAccount(fcDbxAcctMgr.getLinkedAccount());


            // Get the contents of the root folder. This will block until we can
            // sync metadata the first time.
            fcFileInfo = fcDbxFileSystem.listFolder(DbxPath.ROOT);

 /*           DbxFileInfo[] fn = new DbxFileInfo[fcFileInfo.size()];
        fn = fcFileInfo.toArray(new DbxFileInfo[fcFileInfo.size()]);
fcFileInfo.toArray(fn);
*/
            //convert to list only containing filenames
            List<String> fcFileNames = new ArrayList<String>();
            for (DbxFileInfo fi:fcFileInfo ) fcFileNames.add(fi.path.getName());

            setListAdapter( new ArrayAdapter<String>(
                    this,android.R.layout.simple_list_item_1, fcFileNames));


        } catch (DbxException unauthorized) {
            unauthorized.printStackTrace();
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //get the path of the selected file
        showToast("path is " +fcFileInfo.get(position).path);

        try {

            //decryption stuff - TEMPORARY !!
            DbxPath inPath = fcFileInfo.get(position).path;

            //open a file input stream with given path
            DbxFile myCiphertextFile = fcDbxFileSystem.open(inPath);
            FileInputStream fis = myCiphertextFile.getReadStream();


            //get file output stream
            String out = fcFileInfo.get(position).path.getName();
            out = out.substring(0, out.length()-4) + ".dec.txt";
            DbxPath outPath = new DbxPath(out);


/*
 * Writing decrypted file to dropbox is a temporary measure just for testing
 * THIS SHOUD NOT BE LEFT IN FINAL VERSION
 *
 */
            DbxFile myPlaintextFile;
            if (fcDbxFileSystem.exists(outPath))
                myPlaintextFile = fcDbxFileSystem.open(outPath);
            else myPlaintextFile = fcDbxFileSystem.create(outPath);

            FileOutputStream fos = myPlaintextFile.getWriteStream();


            //get the correct key
            KeyManagement decryptKey = new KeyManagement(inPath.toString());


            FileCryptor.decryptFile(fis, fos, decryptKey);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Need to add something to handle Failed or was cancelled by the user.
    }


    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
