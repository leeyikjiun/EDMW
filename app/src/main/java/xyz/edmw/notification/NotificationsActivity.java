package xyz.edmw.notification;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.itemTouchHelper.SimpleItemTouchHelperCallback;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.RecyclerViewActivity;
import xyz.edmw.rest.RestClient;

public class NotificationsActivity extends RecyclerViewActivity {
    @Override
    protected void loadItems() {
        Call<Notifications> call = RestClient.getService().getNotifications();
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(Response<Notifications> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Notifications notifications = response.body();
                    NotificationAdapter adapter = new NotificationAdapter(NotificationsActivity.this, notifications);
                    recyclerView.setAdapter(adapter);

                    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                    itemTouchHelper.attachToRecyclerView(recyclerView.mRecyclerView);
                } else {
                    Toast.makeText(NotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
