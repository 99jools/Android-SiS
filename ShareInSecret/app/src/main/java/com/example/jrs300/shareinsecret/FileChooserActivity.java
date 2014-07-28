package com.example.jrs300.shareinsecret;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.util.ArrayList;
import java.util.List;

public class FileChooserActivity extends ListActivity {
    ListView fcListView;
    DbxAccountManager fcDbxAcctMgr;
    DbxFileSystem fcDbxFileSystem;
    String  classes[] = {"ChooserActivity", "example1", "example2", "example3"};

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
            List<DbxFileInfo> fcFileInfo = fcDbxFileSystem.listFolder(DbxPath.ROOT);

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

        /*
        need to edit this so that it chooses a file not a class
        try {
            Class ourClass = Class.forName("com.example.jrs300.shareinsecret." +classes[position]);    Intent ourIntent = new Intent(this, ourClass);
            startActivity(ourIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
*/
    }
}
