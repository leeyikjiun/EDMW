package xyz.edmw.rest.noembed;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Converter;
import xyz.edmw.youtube.YouTubeVideo;

public class YouTubeVideoResponseBodyConverter implements Converter<ResponseBody, YouTubeVideo> {
    @Override
    public YouTubeVideo convert(ResponseBody value) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(value.string()).getAsJsonObject();
        if (jsonObject.get("error") != null) {
            return null;
        }
        String title = jsonObject.get("title").getAsString();
        String thumbnail = jsonObject.get("thumbnail_url").getAsString();
        return new YouTubeVideo(title, thumbnail);
    }
}
