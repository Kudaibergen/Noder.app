package com.ka.noder.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.ka.noder.R;

public class AuthTokenLoader extends AsyncTaskLoader<String> {
//    private final String mObtainTokenUrl;
//    private final String mLogin;
//    private final String mPassword;
    private String mAuthToken;

    public AuthTokenLoader(Context context, String login, String password) {
        super(context);
//        mObtainTokenUrl = context.getString(R.string.noder_obtain_token_url, )
    }

    @Override
    public String loadInBackground() {
        return null;
    }
}