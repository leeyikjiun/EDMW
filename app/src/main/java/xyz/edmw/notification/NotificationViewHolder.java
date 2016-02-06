package xyz.edmw.notification;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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
    SimpleDraweeView authorAvatar;
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

        setAvatar(user.getAvatar());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadActivity.startInstance(context, notification);
            }
        });
    }

    private void setAvatar(String source) {
        ImageRequest.RequestLevel level = preferences.canDownloadImage() ? ImageRequest.RequestLevel.FULL_FETCH : ImageRequest.RequestLevel.DISK_CACHE;
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(source))
                .setLowestPermittedRequestLevel(level)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        authorAvatar.setController(controller);
    }
}
