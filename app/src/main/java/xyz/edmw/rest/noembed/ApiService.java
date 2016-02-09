package xyz.edmw.rest.noembed;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import xyz.edmw.youtube.YouTubeVideo;

public interface ApiService {
    @GET("/embed")
    Call<YouTubeVideo> getYouTubeVideo(@Query("url") String url);
}
