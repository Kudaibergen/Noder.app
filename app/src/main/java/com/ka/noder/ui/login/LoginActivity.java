package com.ka.noder.ui.login;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ka.noder.R;
import com.ka.noder.account.NoderAccount;
import com.ka.noder.api.ServerAuthenticate;

public class LoginActivity extends AccountAuthenticatorActivity {
    public static final String EXTRA_ACCOUNT_TYPE = "com.ka.noder.EXTRA_ACCOUNT_TYPE";
    public static final String EXTRA_TOKEN_TYPE = "com.ka.noder.EXTRA_TOKEN_TYPE";

    private UserAuthTask mAuthTask;
    private EditText loginView;
    private EditText passwordView;
    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginView = (EditText) findViewById(R.id.login);
        passwordView = (EditText) findViewById(R.id.password);
        signInBtn = (Button) findViewById(R.id.btn_sign_in);
    }

    public void onTokenReceived(Bundle result) {
        Log.e("TAG_LogAct", "onTokenReceived, Токен получен");

        String login = result.getString(AccountManager.KEY_ACCOUNT_NAME);
        String password = result.getString(NoderAccount.KEY_PASSWORD);
        String token = result.getString(AccountManager.KEY_AUTHTOKEN);

        AccountManager manager = AccountManager.get(this);
        final NoderAccount account = new NoderAccount(login);

        if (manager.addAccountExplicitly(account, password, new Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            manager.setAuthToken(account, account.type, token);

            SharedPreferences preferences = getSharedPreferences("Noder_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isRegistered", true);
            editor.apply();

            Log.e("TAG_LogAct", "onTokenReceived, Аккаунт добавлен, token = " + token);
        } else {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_already_exists));
            Log.e("TAG_LogAct", "onTokenReceived, Аккаунт уже существует");
        }
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        finish();
    }

    public void click(View view) {
        if (mAuthTask != null){
            Log.e("TAG_LogAct", "Click, mAuthTask not null");
            return;
        }
        Log.e("TAG_LogAct", "Click, mAuthTask null");

        loginView.setError(null);
        passwordView.setError(null);

        String login = loginView.getText().toString();
        String password = passwordView.getText().toString();

        if (!isValidate(login, password)) {
            return;
        }

        mAuthTask = new UserAuthTask();

        Log.e("TAG_LogAct", "Click, Регистрация (добавление аккаунта), получение токена, задача mAuthTask начинается");
        mAuthTask.execute(login, password);
    }

    private boolean isValidate(String login, String password){
        return isLoginValid(login) && isPasswordValid(password);
    }

    private boolean isLoginValid(String login) {
        if (login.length() > 3) {
            return true;
        } else {
            loginView.setError("Больше трех символов");
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.length() > 4) {
            return true;
        } else {
            passwordView.setError("Больше четырех символов");
            return false;
        }
    }

    private class UserAuthTask extends AsyncTask<String, Void, Bundle>{

        @Override
        protected Bundle doInBackground(String... params) {
            String login = params[0];
            String password = params[1];
            String token = ServerAuthenticate.userSignIn(LoginActivity.this, login, password, NoderAccount.TOKEN_TYPE_FULL_ACCESS);

            final Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, login);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, NoderAccount.TYPE);
            bundle.putString(AccountManager.KEY_AUTHTOKEN, token);
            bundle.putString(NoderAccount.KEY_PASSWORD, password);

            Log.e("TAG_LogAct", "mAuthTask, получение токена");

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle res) {
            super.onPostExecute(res);
            signInBtn.setClickable(true);
            onTokenReceived(res);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            signInBtn.setClickable(false);
        }
    }
}