package xyz.edmw.rest;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import xyz.edmw.Forum;
import xyz.edmw.thread.Thread;
import xyz.edmw.topic.TopicForm;

public interface ApiService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<Void> login(@Field("username") String username, @Field("password") String password, @Field("rememberme") boolean remember);

    @Multipart
    @POST("/create-content/text/")
    Call<Void> reply(@Part("securitytoken") String securityToken, @Part("channelid") int channelId, @Part("parentid") int parentId, @Part("text") String text);

    @GET("/forum/{forum}/page{page}")
    Call<Forum> getForum(@Path("forum") String forum, @Path("page") int page);

    @GET("/{path}/page{page}")
    Call<Thread> getThread(@Path("path") String path, @Path("page") int page);

    @GET("/new-content/3")
    Call<TopicForm> getTopic();

    @Multipart
    @POST("/create-content/text/")
    Call<Void> postTopic(@Part("securitytoken") String securityToken, @Part("parentid") int parentId, @Part("title") String title, @Part("text") String text);
}