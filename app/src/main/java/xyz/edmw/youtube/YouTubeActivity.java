package xyz.edmw.youtube;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;

public class YouTubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener {
    @Bind(R.id.youtube_player_view)
    YouTubePlayerView playerView;

    // TODO replace with developer key
    private static final String DeveloperKey = null;
    private static final String ARG_VIDEO_ID = "arg_video_id";
    private String videoId;
    private YouTubePlayer player;
    private boolean isFullscreen;

    public static void startInstance(Context context, String videoId) {
        Intent intent = new Intent(context, YouTubeActivity.class);
        intent.putExtra(ARG_VIDEO_ID, videoId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        videoId = getIntent().getStringExtra(ARG_VIDEO_ID);
        playerView.initialize(DeveloperKey, this);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            player.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            this.player = player;
            player.setOnFullscreenListener(this);
            player.cueVideo(videoId);
            player.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        Toast.makeText(YouTubeActivity.this, "Failed to load YouTube video", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }
}
