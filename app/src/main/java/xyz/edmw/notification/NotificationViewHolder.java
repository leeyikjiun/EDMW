package xyz.edmw.notification;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;
import xyz.edmw.User;
import xyz.edmw.settings.MainSharedPreferences;
import xyz.edmw.thread.ThreadActivity;

public class NotificationViewHolder extends UltimateRecyclerviewViewHolder {
    @Bind(R.id.card_view)
    CardView cardView;
    @Bind(R.id.notification_author)
    TextView author;
    @Bind(R.id.notification_title)
    TextView title;
    @Bind(R.id.notification_postDate)
    TextView postDate;
    @Bind(R.id.notification_avatar)
    ImageView authorAvatar;
    @Bind(R.id.notification_type)
    TextView userTitle;

    private final Context context;
    private MainSharedPreferences preferences;
    private String id;

    public NotificationViewHolder(Context context, View view, boolean isItem) {
        super(view);
        this.context = context;

        if (isItem) {
            ButterKnife.bind(this, view);
            preferences = new MainSharedPreferences(context);
        }
    }

    public void setNotification(final Notification notification) {
        id = notification.getId();
        User user = notification.getUser();
        author.setText(user.getName());
        userTitle.setText(notification.getType());
        postDate.setText(notification.getPostDate());
        title.setText(notification.getTitle());

        if (preferences.canDownloadImage()) {
            authorAvatar.setVisibility(View.VISIBLE);
            Ion.with(authorAvatar).load(user.getAvatar());
        } else {
            authorAvatar.setVisibility(View.GONE);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadActivity.startInstance(context, notification);
            }
        });
    }
}
