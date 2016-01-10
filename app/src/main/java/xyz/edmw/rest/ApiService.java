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
import xyz.edmw.post.Post;
import xyz.edmw.thread.Thread;

public interface ApiService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<Void> login(@Field("username") String username, @Field("password") String password, @Field("rememberme") boolean remember);

    @Multipart
    @POST("/create-content/text/")
    Call<Void> reply(@Part("channelid") int channelId, @Part("parentid") int parentId, @Part("text") String text);

    @GET("/forum/{forum}/page{page}")
    Call<List<Thread>> getThreads(@Path("forum") String forum, @Path("page") int page);

    @GET("/{path}/page{page}")
    Call<List<Post>> getPosts(@Path("path") String path, @Path("page") int page);
}