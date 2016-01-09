package xyz.edmw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.rest.RestClient;

public class LoginFragment extends Fragment {
    @Bind(R.id.username)
    AutoCompleteTextView username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.user_sign_in_button)
    Button signIn;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = RestClient.getService()
                        .login(username.getText().toString(), password.getText().toString(), true);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        ((MainActivity) getContext()).onLogin();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
    }
}
