package xyz.edmw.rest.noembed;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import retrofit.Retrofit;

public class RestClient {
    public static final String baseUrl = "http://noembed.com";

    private static RestClient instance = new RestClient();
    private final ApiService service;

    public static ApiService getService() {
        return instance.service;
    }

    private RestClient() {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(0, TimeUnit.MILLISECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.interceptors().add(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new ConverterFactory())
                .client(client)
                .build();
        service = retrofit.create(ApiService.class);
    }
}
