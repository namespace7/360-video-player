package com.namespace.application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class Videostream extends AppCompatActivity {
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private View loading;
    private DataSource.Factory dataSourceFactory;
    private ImageButton changeQuality;
    private TrackSelector trackSelector;
    private  MappingTrackSelector selector;
    private  TrackSelection.Factory videoTrackSelectionFactory;
    String space= "\n";
    Uri URL = Uri.parse("https://bitmovin.com/player-content/playhouse-vr/m3u8s/105560.m3u8");
    final private String AD_TAG_URI = "http://192.168.0.107/ad_sample.mp4";
    private DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //will hide the status bar
        setContentView(R.layout.activity_videostream);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //will rotate the screen
        Bundle b = getIntent().getExtras();
        loading = findViewById(R.id.loading);
        changeQuality = findViewById(R.id.change_quality);
        videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        selector = (MappingTrackSelector) trackSelector;
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.setUseController(true);//set to true or false to see controllers
        simpleExoPlayerView.requestFocus();
        simpleExoPlayerView.setPlayer(player);
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "StreamTest"), bandwidthMeter);

            play_stream(bandwidthMeter);


        player.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d("_____________________ ", String.valueOf(space));
            }
            @Override
            public void onLoadingChanged(boolean isLoading) {
            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                        loading.setVisibility(View.GONE);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        loading.setVisibility(View.VISIBLE);
                        break;
                }
                if (playbackState == Player.STATE_IDLE ||
                        !playWhenReady) {
                    simpleExoPlayerView.setKeepScreenOn(false);
                } else if(playbackState ==Player.STATE_ENDED) {
                    player.release();
                }
                else{
                    // This prevents the screen from getting dim/lock
                    simpleExoPlayerView.setKeepScreenOn(true);
                }
            }
            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }
            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();
                player.stop();
                player.clearVideoSurface();
                AlertDialog dialog;
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Videostream.this);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Unable to connect or Fetch the Host");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mIntent = getIntent();
                        finish();
                        startActivity(mIntent);
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  dialog.dismiss();
                        finish();
                    }
                });
                dialog = alertDialog.create();
                dialog.show();
            }
            @Override
            public void onPositionDiscontinuity(int reason) {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            public void onSeekProcessed() {
            }

        });
        player.seekTo(0);
        player.setPlayWhenReady(true); //run file/link when ready to play.
    }


    private void play_stream(DefaultBandwidthMeter bandwidthMeter) {

        MediaSource videoSource = new HlsMediaSource(URL, dataSourceFactory, 1, null, null);
        player.prepare(videoSource);
        changeQuality.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                change_Quality();
            }
        });
    }
    public void play_ads(DefaultBandwidthMeter bandwidthMeter){
        MediaSource AdSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(AD_TAG_URI));
        player.prepare(AdSource);
    }

    private void change_Quality() {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = selector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            int rendererIndex = 0;
            Pair<AlertDialog, TrackSelectionView> dialogPair =
                    TrackSelectionView.getDialog(Videostream.this, "Available Quality", (DefaultTrackSelector) selector, rendererIndex);
            dialogPair.first.show();
        }
    }

    private void hideSystemUi() {
        simpleExoPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
        player.seekTo(0);
        player.release();
        player.clearVideoSurface();
        player.setPlayWhenReady(true); //run file/link when ready to play.
        //  play_stream(bandwidthMeter);
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.seekTo(0);
        player.release();
        player.clearVideoSurface();

    }

}

