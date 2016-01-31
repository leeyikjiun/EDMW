package xyz.edmw.rest;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit2.ScalarsConverterFactory;
import xyz.edmw.MainApplication;

public class RestClient {
    public static final String baseUrl = "http://www.edmw.xyz";

    private static RestClient instance = new RestClient();
    private final ApiService service;

    public static ApiService getService() {
        return instance.service;
    }

    private RestClient() {
        Context context = MainApplication.getContext();

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(0, TimeUnit.MILLISECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.interceptors().add(interceptor);

        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, client)
                .build();
        Fresco.initialize(context, config);

        CookieStore cookieStore = new PersistentCookieStore(context);
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new ConverterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(ApiService.class);
    }
}
