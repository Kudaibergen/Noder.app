package com.ka.noder.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ka.noder.api.NoderApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller {
    private static final String BASE_URL = "http://192.168.0.103:8080/tutorial7/api/";
    private static final Controller controller = new Controller();
    private static NoderApi api;

    private Controller() {}

    public static NoderApi getApi() {
        if (api == null) {
            controller.initRetrofit();
        }
        return api;
    }

    private void initRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(NoderApi.class);
    }
}