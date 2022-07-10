package com.ubi.android.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.ubi.android.R;
import com.ubi.android.utils.AppUtils;
import com.ubi.android.utils.Auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayVideoActivity extends AppCompatActivity implements
        YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.OnFullscreenListener {
    private static final String YOUTUBE_FRAGMENT_TAG = "youtube";
    VideoView videoplayer;
    FrameLayout youtubecontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        youtubecontainer = findViewById(R.id.youtubecontainer);
        videoplayer = findViewById(R.id.videoplayer);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String companyvideo = getIntent().getStringExtra("videourl");
        if (!TextUtils.isEmpty(companyvideo)) {
            String url = companyvideo;
            if (url.contains("youtube") || url.contains("youtu.be")) {
                String youtubeId = getYouTubeId(url);
                panToVideo(youtubeId);
                youtubecontainer.setVisibility(View.VISIBLE);
            } else {
                videoplayer.setVisibility(View.VISIBLE);
                videoplayer.setVideoPath(companyvideo);
            }
        }
    }

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "error";
        }
    }

    public void panToVideo(final String youtubeId) {
        popPlayerFromBackStack();
        YouTubePlayerFragment playerFragment = YouTubePlayerFragment
                .newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.youtubecontainer, playerFragment,
                        YOUTUBE_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        playerFragment.initialize(Auth.KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(youtubeId);
                        youTubePlayer
                                .setPlayerStateChangeListener(PlayVideoActivity.this);
                        youTubePlayer
                                .setOnFullscreenListener(PlayVideoActivity.this);
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
//                        youTubePlayer.setShowFullscreenButton(false);
                        youTubePlayer.pause();
//                        youTubePlayer.setFullscreen(true);
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult result) {
                        AppUtils.showalert(PlayVideoActivity.this, "The current video could not be loaded because it is not in a playable state.", false);
//                        result.getErrorDialog(PlayActivity.this, 0);
                    }
                });
    }

    public boolean popPlayerFromBackStack() {
        if (getFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG) != null) {
            getFragmentManager().popBackStack();
            return false;
        }
        return true;
    }

    @Override
    public void onAdStarted() {
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        showErrorToast(errorReason.toString());
    }

    private void showErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onLoaded(String arg0) {
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onVideoEnded() {
        // popPlayerFromBackStack();
        finish();
    }

    @Override
    public void onVideoStarted() {
    }

    @Override
    public void onFullscreen(boolean fullScreen) {
    }
}