package xyz.edmw.rest;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

import retrofit.Retrofit;
import xyz.edmw.MainApplication;

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

        CookieStore cookieStore = new PersistentCookieStore(MainApplication.getContext());
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new ConverterFactory())
                .client(client)
                .build();
        service = retrofit.create(ApiService.class);
    }
}
