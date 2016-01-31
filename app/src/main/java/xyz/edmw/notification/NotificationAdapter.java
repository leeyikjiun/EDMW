package xyz.edmw.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.rest.RestClient;

public class NotificationAdapter extends UltimateViewAdapter<NotificationViewHolder> {
    private final Context context;
    private final Notifications notifications;
    private final List<Notification> notificationList;

    public NotificationAdapter(Context context, Notifications notifications) {
        this.context = context;
        this.notifications = notifications;
        notificationList = notifications.getNotifications();
    }

    @Override
    public NotificationViewHolder getViewHolder(View view) {
        return new NotificationViewHolder(context, view, false);
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_notification, parent, false);
        return new NotificationViewHolder(context, view, true);
    }

    @Override
    public int getAdapterItemCount() {
        return notificationList.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        if (position < notificationList.size()) {
            holder.setNotification(notificationList.get(position));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onItemDismiss(final int position) {
        Notification notification = notificationList.get(position);
        Call<Void> call = RestClient.getService().dismissNotification(notification.getId(), notifications.getIdsOnPage(), notifications.getFilterParams(), notifications.getSecurityToken());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    notificationList.remove(position);
                    notifyItemRemoved(position);
                } else {
                    Toast.makeText(context, "Failed to dismiss notification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Failed to dismiss notification", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
