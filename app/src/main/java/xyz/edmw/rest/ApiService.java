package xyz.edmw.rest;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import xyz.edmw.thread.Thread;
import xyz.edmw.topic.Topic;

public interface ApiService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<Void> login(@Field("username") String username, @Field("password") String password, @Field("rememberme") boolean remember);

    @Multipart
    @POST("/create-content/text/")
    Call<Void> reply(@Part("securitytoken") String securityToken, @Part("channelid") int channelId, @Part("parentid") int parentId, @Part("text") String text);

    @GET("/forum/{forum}/page{page}")
    Call<List<Topic>> getThreads(@Path("forum") String forum, @Path("page") int page);

    @GET("/{path}/page{page}")
    Call<Thread> getThread(@Path("path") String path, @Path("page") int page);
}