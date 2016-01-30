package xyz.edmw.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;

import xyz.edmw.R;

public class NotificationAdapter extends UltimateViewAdapter<NotificationViewHolder> {
    private final Context context;
    private final List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
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
        return notifications.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        if (position < notifications.size()) {
            holder.setNotification(notifications.get(position));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
