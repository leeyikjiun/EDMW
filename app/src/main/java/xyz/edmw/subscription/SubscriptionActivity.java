package xyz.edmw.subscription;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.rest.RestClient;
import xyz.edmw.settings.MainSharedPreferences;
import xyz.edmw.topic.TopicAdapter;

public class SubscriptionActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    UltimateRecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(new MainSharedPreferences(this).getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDividerDecoration(getApplicationContext());
        recyclerView.setLayoutManager(llm);

        Call<Subscriptions> call = RestClient.getService().getSubscriptions();
        call.enqueue(new Callback<Subscriptions>() {
            @Override
            public void onResponse(Response<Subscriptions> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Subscriptions subscriptions = response.body();
                    TopicAdapter adapter = new TopicAdapter(SubscriptionActivity.this, subscriptions.getTopics());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(SubscriptionActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(SubscriptionActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
