package xyz.edmw.youtube;

import android.content.Context;
import android.net.Uri;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.rest.noembed.RestClient;

public class YouTubeThumbnailViewHolder implements Callback<YouTubeVideo>, View.OnClickListener {
    @Bind(R.id.youtube_title)
    TextView title;
    @Bind(R.id.youtube_thumbnail)
    SimpleDraweeView thumbnail;
    @Bind(R.id.youtube_play)
    ImageView play;

    private static final LruCache<String, YouTubeVideo> videoIdMap = new LruCache<>(15);
    private final Context context;
    private String videoId;

    public YouTubeThumbnailViewHolder(Context context, View view) {
        this.context = context;
        ButterKnife.bind(this, view);
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;

        YouTubeVideo video = videoIdMap.get(videoId);
        if (video != null) {
            setVideo(video);
            return;
        }
        String url = "https://www.youtube.com/watch?v=" + videoId;
        Call<YouTubeVideo> call = RestClient.getService().getYouTubeVideo(url);
        call.enqueue(this);
    }

    private void setVideo(YouTubeVideo video) {
        title.setText(video.getTitle());
        thumbnail.setImageURI(Uri.parse(video.getThumbnail()));
        play.setOnClickListener(this);
    }

    private void onError() {
        title.setText("Not Found");
        play.setVisibility(View.GONE);
        title.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) title.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;

    }

    @Override
    public void onResponse(Response<YouTubeVideo> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            YouTubeVideo video = response.body();
            if (video != null) {
                videoIdMap.put(videoId, video);
                setVideo(video);
                return;
            }
        }
        onError();
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
        onError();
    }

    @Override
    public void onClick(View v) {
        YouTubeActivity.startInstance(context, videoId);
    }
}
