package xyz.edmw.subscription;

import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.RecyclerViewActivity;
import xyz.edmw.rest.RestClient;
import xyz.edmw.topic.TopicAdapter;

public class SubscriptionsActivity extends RecyclerViewActivity {
    @Override
    protected void loadItems() {
        Call<Subscriptions> call = RestClient.getService().getSubscriptions();
        call.enqueue(new Callback<Subscriptions>() {
            @Override
            public void onResponse(Response<Subscriptions> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Subscriptions subscriptions = response.body();
                    TopicAdapter adapter = new TopicAdapter(SubscriptionsActivity.this, subscriptions.getTopics());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(SubscriptionsActivity.this, "Failed to load subscriptions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(SubscriptionsActivity.this, "Failed to load subscriptions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
