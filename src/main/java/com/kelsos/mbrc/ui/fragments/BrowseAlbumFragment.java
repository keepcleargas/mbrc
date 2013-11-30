package com.kelsos.mbrc.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumEntryAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.*;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.AlbumSearchResults;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class BrowseAlbumFragment extends RoboSherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int GROUP_ID = 13;
    private static final int URL_LOADER = 2;
    private String mDefault;
    private SimpleCursorAdapter mAdapter;

    @Inject Bus bus;

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String album = ((AlbumEntry) getListView().getAdapter().getItem(position)).getAlbum();

                bus.post(new MessageEvent(ProtocolEventType.UserAction,
                        new UserAction(Protocol.LibraryQueueAlbum,
                                new Queue(mDefault, album))));
            }
        });
    }

    @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
        mDefault = action.getAction();
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        return inflater.inflate(R.layout.ui_fragment_library_simpl, container, false);
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_tracks);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object line = mAdapter.getItem(mi.position);
            final String qContext = Protocol.LibraryQueueAlbum;
            final String gSub = Protocol.LibraryAlbumTracks;
            String query = ((AlbumEntry) line).getAlbum();

            UserAction ua = null;
            switch (item.getItemId()) {
                case BrowseMenuItems.QUEUE_NEXT:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_next), query));
                    break;
                case BrowseMenuItems.QUEUE_LAST:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_last), query));
                    break;
                case BrowseMenuItems.PLAY_NOW:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_now), query));
                    break;
                case BrowseMenuItems.GET_SUB:
                    ua = new UserAction(gSub, query);
                    break;
            }

            if (ua != null) bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
            return true;
        } else {
            return false;
        }
    }

    @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
//        adapter = new AlbumEntryAdapter(getActivity(), R.layout.ui_list_dual, results.getList());
//        setListAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Album.URI();
        return new CursorLoader(getActivity(),baseUri,
                new String[] {Album.ALBUM_NAME, Artist.ARTIST_NAME}, null,null,null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.ui_list_dual,
                data,
                new String[] { Album.ALBUM_NAME, Artist.ARTIST_NAME },
                new int[] {R.id.line_one, R.id.line_two},
                0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }
}