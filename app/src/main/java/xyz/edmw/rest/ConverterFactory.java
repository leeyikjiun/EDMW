package xyz.edmw.rest;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

public class ConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        switch (type.toString()) {
            case "xyz.edmw.generic.GenericMap<java.lang.Integer, xyz.edmw.thread.Thread>":
                return new ThreadsResponseBodyConverter();
            case "xyz.edmw.generic.GenericMap<java.lang.Integer, xyz.edmw.post.Post>":
                return new PostsResponseBodyConverter();
        }
        return super.fromResponseBody(type, annotations);
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return super.toRequestBody(type, annotations);
    }
}
