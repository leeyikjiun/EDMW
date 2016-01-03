package xyz.edmw.rest;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.Retrofit;

public class RestClient {
    public static final String baseUrl = "http://www.edmw.xyz";

    private static RestClient instance = new RestClient();
    private final ApiService service;

    public static ApiService getService() {
        return instance.service;
    }

    private RestClient() {
        OkHttpClient client = new OkHttpClient();
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
