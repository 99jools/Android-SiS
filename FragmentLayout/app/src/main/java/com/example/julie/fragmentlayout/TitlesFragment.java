package com.example.julie.fragmentlayout;


// Displays a list of items that are managed by an adapter similar to
// ListActivity. It provides several methods for managing a list view, such
// as the onListItemClick() callback to handle click events.

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TitlesFragment extends ListFragment {
    boolean mDualPane;
    int mCurCheckPosition = 0;

    // onActivityCreated() is called when the activity's onCreate() method
    // has returned.

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // You can use getActivity(), which returns the activity associated
        // with a fragment.
        // The activity is a context (since Activity extends Context) .

        Toast.makeText(getActivity(), "TitlesFragment:onActivityCreated",
                Toast.LENGTH_LONG).show();

        // Populate list with our static array of titles in list in the
        // Shakespeare class
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1,
                Shakespeare.TITLES));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        // R.id.details relates to the res/layout-land/fragment_layout.xml
        // This is first created when the phone is switched to landscape
        // mode

        View detailsFrame = getActivity().findViewById(R.id.details);

        Toast.makeText(getActivity(), "detailsFrame " + detailsFrame,
                Toast.LENGTH_LONG).show();

        // Check that a view exists and is visible
        // A view is visible (0) on the screen; the default value.
        // It can also be invisible and hidden, as if the view had not been
        // added.
        //
        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        Toast.makeText(getActivity(), "mDualPane " + mDualPane,
                Toast.LENGTH_LONG).show();

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected
            // item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
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

    // If the user clicks on an item in the list (e.g., Henry V then the
    // onListItemClick() method is called. It calls a helper function in
    // this case.

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Toast.makeText(getActivity(),
                "onListItemClick position is" + position, Toast.LENGTH_LONG)
                .show();

        showDetails(position);
    }

    // Helper function to show the details of a selected item, either by
    // displaying a fragment in-place in the current UI, or starting a whole
    // new activity in which it is displayed.

    void showDetails(int index) {
        mCurCheckPosition = index;

        // The basic design is mutli-pane (landscape on the phone) allows us
        // to display both fragments (titles and details) with in the same
        // activity; that is FragmentLayout -- one activity with two
        // fragments.
        // Else, it's single-pane (portrait on the phone) and we fire
        // another activity to render the details fragment - two activities
        // each with its own fragment .
        //
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            // We keep highlighted the current selection
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment) getFragmentManager()
                    .findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.

                details = DetailsFragment.newInstance(index);

                Toast.makeText(getActivity(),
                        "showDetails dual-pane: create and relplace fragment",
                        Toast.LENGTH_LONG).show();

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.replace(R.id.details, details);
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
            intent.putExtra("index", index);

            startActivity(intent);
        }
    }
}
