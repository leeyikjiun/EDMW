package xyz.edmw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.rest.RestClient;
import xyz.edmw.settings.MainSharedPreferences;

public class LoginActivity extends AppCompatActivity {
    private static final String tag = "LoginActivity";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.username)
    AutoCompleteTextView username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.user_sign_in_button)
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(new MainSharedPreferences(this).getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void attemptLogin() {
        username.setError(null);
        password.setError(null);

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if (TextUtils.isEmpty(username)) {
            this.username.setError(getString(R.string.error_field_required));
            this.username.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            this.password.setError(getString(R.string.error_field_required));
            this.password.requestFocus();
            return;
        }


        Call<Boolean> call = RestClient.getService().login(username, password, "on");
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Response<Boolean> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body()) {
                    Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                    //MainActivity.onLogin();
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "You have entered an invalid username or password. Please enter the correct details and try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Unknown error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}