package com.ka.noder.api;

import android.content.Context;

public class ServerAuthenticate {

    public static String userSignIn(Context context, String login, String password, String authTokenType) {
        // Сервер пока не умеет генерить токен
        return "some_token_noder";
    }
}