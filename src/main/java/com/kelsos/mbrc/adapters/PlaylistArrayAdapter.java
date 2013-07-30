package com.kelsos.mbrc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.MusicTrack;

import java.util.ArrayList;

public class PlaylistArrayAdapter extends ArrayAdapter<MusicTrack> {
    private Context _context;
    private int _layoutResourceId;
    private ArrayList<MusicTrack> _nowPlayingList;
	private int playingTrackIndex;
    private Typeface robotoLight;

    public PlaylistArrayAdapter(Context context, int resource, ArrayList<MusicTrack> objects) {
        super(context, resource, objects);
        this._layoutResourceId = resource;
        this._context = context;
        this._nowPlayingList = objects;
        robotoLight = Typeface.createFromAsset(_context.getAssets(), "fonts/roboto_light.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TrackHolder holder;

        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) _context).getLayoutInflater();
            row = layoutInflater.inflate(_layoutResourceId, parent, false);

            holder = new TrackHolder();
            holder.title = (TextView) row.findViewById(R.id.trackTitle);
            holder.artist = (TextView) row.findViewById(R.id.trackArtist);
			holder.trackPlaying = (ImageView) row.findViewById(R.id.listview_item_image);
            holder.title.setTypeface(robotoLight);
            holder.artist.setTypeface(robotoLight);

            row.setTag(holder);
        } else {
            holder = (TrackHolder) row.getTag();
        }

        MusicTrack track = _nowPlayingList.get(position);
        holder.title.setText(track.getTitle());
        holder.artist.setText(track.getArtist());
		if(position==playingTrackIndex)
		{
			holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing);
		}
		else
		{
			holder.trackPlaying.setImageResource(android.R.color.transparent);
		}
		holder.trackPlaying.setOnClickListener(showContextMenu);

        return row;
    }

	private final View.OnClickListener showContextMenu = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			view.showContextMenu();
		}
	};

	public void setPlayingTrackIndex(int index) {
		this.playingTrackIndex = index;
	}

    public int getPlayingTrackIndex() {
        return this.playingTrackIndex;
    }

    static class TrackHolder {
        TextView title;
        TextView artist;
		ImageView trackPlaying;

    }

}
