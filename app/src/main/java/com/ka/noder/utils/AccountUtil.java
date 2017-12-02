package com.ka.noder.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.ka.noder.account.NoderAccount;

public class AccountUtil {
    private static final AccountUtil util = new AccountUtil();
    private static Account instance = null;

    private AccountUtil(){}

    public static Account getInstance(Context context) {
        if (instance == null){
            util.initAccount(context);
        }
        return instance;
    }

    private void initAccount(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(NoderAccount.TYPE);
        instance = accounts[0];
    }
}