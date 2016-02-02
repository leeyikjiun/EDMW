package xyz.edmw.rest;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;
import xyz.edmw.Forum;
import xyz.edmw.notification.Notifications;
import xyz.edmw.subscription.Subscriptions;
import xyz.edmw.thread.Thread;
import xyz.edmw.topic.TopicForm;

public class ConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        if (type == Thread.class) {
            return new ThreadResponseBodyConverter();
        } else if (type == Forum.class) {
            return new ForumResponseBodyConverter();
        } else if (type == TopicForm.class) {
            return new TopicFormResponseBodyConverter();
        } else if (type == Boolean.class) {
            return new LoginResponseBodyConverter();
        } else if (type == Notifications.class) {
            return new NotificationsResponseBodyConverter();
        } else if (type == Subscriptions.class) {
            return new SubscriptionsResponseBodyConverter();
        } else {
            return super.fromResponseBody(type, annotations);
        }
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return super.toRequestBody(type, annotations);
    }
}
