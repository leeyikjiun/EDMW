package xyz.edmw.rest.noembed;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;
import xyz.edmw.youtube.YouTubeVideo;

public class ConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        if (type == YouTubeVideo.class) {
            return new YouTubeVideoResponseBodyConverter();
        } else {
            return super.fromResponseBody(type, annotations);
        }
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return super.toRequestBody(type, annotations);
    }
}
