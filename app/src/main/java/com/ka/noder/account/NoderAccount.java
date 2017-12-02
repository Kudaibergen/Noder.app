package com.ka.noder.account;

import android.accounts.Account;
import android.util.Log;

public class NoderAccount extends Account {
    public static final String TYPE = "com.ka.noder";
    public static final String TOKEN_TYPE_FULL_ACCESS = "com.ka.noder.TOKEN_TYPE_FULL_ACCESS";
    public static final String KEY_PASSWORD = "com.ka.noder.KEY_PASSWORD";

    public NoderAccount(String name) {
        super(name, TYPE);
        Log.e("TAG_Account", "NoderAccount constructor : " + hashCode());
    }
}