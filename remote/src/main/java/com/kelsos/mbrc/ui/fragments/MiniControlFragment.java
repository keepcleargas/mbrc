package com.kelsos.mbrc.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;



public class MiniControlFragment extends BaseFragment {
    @InjectView (R.id.mc_track_cover) private ImageView trackCover;
    @InjectView (R.id.mc_track_artist) private TextView trackArtist;
    @InjectView (R.id.mc_track_title) private TextView trackTitle;
    @InjectView (R.id.mc_next_track) private ImageButton playNext;
    @InjectView (R.id.mc_play_pause) private ImageButton playPause;
    @InjectView (R.id.mc_prev_track) private ImageButton playPrevious;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_mini_control, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        playNext.setOnClickListener(playNextListener);
        playPause.setOnClickListener(playPauseListener);
        playPrevious.setOnClickListener(playPreviousListener);
        Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
        trackTitle.setTypeface(robotoLight);
    }

    @Subscribe public void handleCoverChange(CoverAvailable event) {
        if (trackCover == null) {
            return;
        }
        if (event.getIsAvailable()) {
            trackCover.setImageBitmap(event.getCover());
        } else {
            trackCover.setImageResource(R.drawable.ic_image_no_cover);
        }
    }

    @Subscribe public void handleTrackInfoChange(TrackInfoChange event) {
        trackArtist.setText(event.getArtist());
        trackTitle.setText(event.getTitle());
    }

    private ImageButton.OnClickListener playNextListener = new ImageButton.OnClickListener(){

        @Override public void onClick(View view) {
            getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYER_NEXT, true)));
        }
    };

    private ImageButton.OnClickListener playPauseListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View view) {
            getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYER_PLAY_PAUSE, true)));
        }
    };

    private ImageButton.OnClickListener playPreviousListener = new ImageButton.OnClickListener() {
        @Override public void onClick(View view) {
            getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYER_PREVIOUS, true)));
        }
    };

    @Subscribe public void handlePlayStateChange(PlayStateChange event) {
        switch (event.getState()) {
            case PLAYING:
                playPause.setImageResource(R.drawable.ic_action_pause);
                break;
            case PAUSED:
                playPause.setImageResource(R.drawable.ic_action_play);
                break;
            case STOPPED:
                playPause.setImageResource(R.drawable.ic_action_play);
                break;
            case UNDEFINED:
                break;
            default:
                playPause.setImageResource(R.drawable.ic_media_stop);
                break;
        }
    }

}
