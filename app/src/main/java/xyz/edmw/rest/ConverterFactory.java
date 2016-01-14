package xyz.edmw.rest;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;
import xyz.edmw.thread.Thread;

public class ConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        if (type == Thread.class) {
            return new ThreadResponseBodyConverter();
        } else if (type.toString().equals("java.util.List<xyz.edmw.topic.Topic>")) {
            return new TopicsResponseBodyConverter();
        } else {
            return super.fromResponseBody(type, annotations);
        }
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return super.toRequestBody(type, annotations);
    }
}
