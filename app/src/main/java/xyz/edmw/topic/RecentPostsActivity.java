package xyz.edmw.topic;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.RecyclerViewActivity;
import xyz.edmw.rest.RestClient;

public class RecentPostsActivity extends RecyclerViewActivity {
    private static final String ARG_PATH = "arg_path";

    public static void startInstance(Context context, String path) {
        Intent intent = new Intent(context, RecentPostsActivity.class);
        intent.putExtra(ARG_PATH, path);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void loadItems() {
        String path = getIntent().getStringExtra(ARG_PATH);
        Call<RecentPosts> call = RestClient.getService().getRecentPosts(path);
        call.enqueue(new Callback<RecentPosts>() {
            @Override
            public void onResponse(Response<RecentPosts> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    RecentPosts recentPosts = response.body();
                    TopicAdapter adapter = new TopicAdapter(RecentPostsActivity.this, recentPosts);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(RecentPostsActivity.this, "Failed to load recent posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(RecentPostsActivity.this, "Failed to load recent posts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
