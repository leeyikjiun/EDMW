package xyz.edmw.rest;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import xyz.edmw.Forum;
import xyz.edmw.notification.Notifications;
import xyz.edmw.thread.Thread;
import xyz.edmw.topic.TopicForm;

public interface ApiService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<Boolean> login(@Field("username") String username, @Field("password") String password, @Field("rememberme") String remember);

    @GET("/auth/logout")
    Call<Void> logout();

    @Multipart
    @POST("/create-content/text/")
    Call<Void> reply(@Part("securitytoken") String securityToken, @Part("channelid") int channelId, @Part("parentid") int parentId, @Part("text") String text);

    @GET("/forum/{forum}/page{page}")
    Call<Forum> getForum(@Path("forum") String forum, @Path("page") int page);

    @GET("/{path}")
    Call<Thread> getThread(@Path("path") String path);

    @GET("/{path}/page{page}")
    Call<Thread> getThread(@Path("path") String path, @Path("page") int page);

    @GET("/new-content/3")
    Call<TopicForm> getTopic();

    @Multipart
    @POST("/create-content/text/")
    Call<Void> postTopic(@Part("securitytoken") String securityToken, @Part("parentid") int parentId, @Part("title") String title, @Part("text") String text);

    @GET("/privatemessage/notification/364/1")
    Call<Notifications> getNotifications();

    @Multipart
    @POST("/ajax/api/notification/dismissNotification")
    Call<Void> dismissNotification(@Part("readIds") String id, @Part("idsOnPage") Map<String, String> idsOnPage, @Part("filterParams") Map<String, String> filterParams, @Part("securitytoken") String securityToken);

    @Multipart
    @POST("/ajax/api/follow/{action}")
    Call<Void> follow(@Path("action") String action, @Part("follow_item") String id, @Part("type") String type, @Part("securitytoken") String securityToken);
}