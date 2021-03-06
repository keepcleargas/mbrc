package com.kelsos.mbrc.ui.fragments.profile;

import android.content.Intent;
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
import android.widget.GridView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumCursorAdapter;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.ui.activities.Profile;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseMenuItems;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistAlbumsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistAlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistAlbumsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        GridView.OnItemClickListener,
        PlaylistDialogFragment.onPlaylistSelectedListener,
        CreateNewPlaylistDialog.onPlaylistNameSelectedListener {

    private static final String ARTIST_ID = "artistId";
    private static final int URL_LOADER = 0x832d;
    private static final int GROUP_ID = 92;
    private AlbumCursorAdapter mAdapter;
    private GridView mGrid;
    private Album album;
    private long artistId;

    public ArtistAlbumsFragment() {
        // Required empty public constructor
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_tracks);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = mi != null ? mi.position : 0;
            album = new Album((Cursor) mAdapter.getItem(position));
            switch (item.getItemId()) {
                case BrowseMenuItems.GET_SUB:
                    showTracks(album);
                    break;
                case BrowseMenuItems.PLAYLIST:
                    final PlaylistDialogFragment dlFragment = new PlaylistDialogFragment();
                    dlFragment.setOnPlaylistSelectedListener(this);
                    dlFragment.show(getFragmentManager(), "playlist");
                default:
                    break;
            }
            return true;
        }

        return false;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param artistId Parameter 1.
     * @return A new instance of fragment ArtistAlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistAlbumsFragment newInstance(long artistId) {
        ArtistAlbumsFragment fragment = new ArtistAlbumsFragment();
        Bundle args = new Bundle();
        args.putLong(ARTIST_ID, artistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(mGrid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistId = getArguments().getLong(ARTIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        final View view = inflater.inflate(R.layout.ui_library_grid, container, false);
        if (view != null) {
            mGrid = (GridView) view.findViewById(R.id.mbrc_grid_view);
            mGrid.setOnItemClickListener(this);
        }
        return view;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = Uri.withAppendedPath(Album.CONTENT_ARTIST_URI, Uri.encode(String.valueOf(artistId)));
        return new CursorLoader(getActivity(), baseUri,
                new String[]{Album.ALBUM_NAME, Artist.ARTIST_NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new AlbumCursorAdapter(getActivity(), cursor, 0);
        mGrid.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        album = new Album((Cursor) mAdapter.getItem(position));
        showTracks(album);
    }

    @Override
    public void onPlaylistNameSelected(String name) {

    }

    @Override
    public void onPlaylistSelected(String hash) {

    }

    @Override
    public void onNewPlaylistSelected() {

    }

    private void showTracks(final Album album) {
        Intent intent = new Intent(getActivity(), Profile.class);
        intent.putExtra("name", album.getAlbumName());
        intent.putExtra("id", album.getId());
        intent.putExtra("type", "album");
        startActivity(intent);
    }
}
