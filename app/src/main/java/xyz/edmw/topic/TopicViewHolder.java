package xyz.edmw.topic;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;
import xyz.edmw.notification.Notification;
import xyz.edmw.settings.MainSharedPreferences;
import xyz.edmw.thread.ThreadActivity;

import static com.facebook.imagepipeline.request.ImageRequest.RequestLevel;

public class TopicViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
    @Bind(R.id.card_view)
    CardView cardView;
    @Bind(R.id.thread_title)
    TextView title;
    @Bind(R.id.thread_started_by)
    TextView startedBy;
    @Bind(R.id.thread_last_post)
    TextView lastPost;
    @Bind(R.id.threadstarter_avatar)
    SimpleDraweeView threadstarterAvatar;
    @Bind(R.id.sticky_label)
    TextView stickyLabel;
    @Bind(R.id.goto_first_unread)
    ImageButton gotoFirstUnread;
    @Bind(R.id.goto_last_post)
    ImageButton gotoLastPost;

    private final Context context;
    private final MainSharedPreferences preferences;
    private Topic topic;

    public TopicViewHolder(Context context, View view, boolean isItem) {
        super(view);
        this.context = context;
        preferences = new MainSharedPreferences(context);

        if (isItem) {
            ButterKnife.bind(this, view);
        }
    }

    public void setTopic(final Topic topic) {
        this.topic = topic;
        cardView.setOnClickListener(this);
        title.setText(topic.getTitle());
        startedBy.setText(topic.getStartedBy());
        lastPost.setText(Html.fromHtml(topic.getLastPost()));
        String avatar = topic.getThreadstarterAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            setAvatar(avatar);
        } else {
            threadstarterAvatar.setVisibility(View.GONE);
        }

        int visibility = topic.isSticky() ? View.VISIBLE : View.GONE;
        stickyLabel.setVisibility(visibility);

        /*if (TextUtils.isEmpty(topic.getFirstUnread())) {
            gotoFirstUnread.setVisibility(View.GONE);
        } else {
            Log.d("firstUnread", topic.getFirstUnread());
            gotoFirstUnread.setVisibility(View.VISIBLE);
            gotoFirstUnread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThreadActivity.startInstance(context, topic.getTitle(), topic.getFirstUnread());
                }
            });
        }*/
        gotoLastPost.setOnClickListener(this);
    }

    private void setAvatar(String source) {
        RequestLevel level = preferences.canDownloadImage() ? RequestLevel.FULL_FETCH : RequestLevel.DISK_CACHE;
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(source))
                .setLowestPermittedRequestLevel(level)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        threadstarterAvatar.setController(controller);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(cardView)) {
            String lastRead = preferences.getLastRead(topic.getId());
            if (TextUtils.isEmpty(lastRead)) {
                ThreadActivity.startInstance(context, topic, 1);
                return;
            }

            String path = "/node/" + lastRead;
            Notification lastReadInstance = new Notification.Builder()
                    .id(topic.getId())
                    .path(path)
                    .title(topic.getTitle())
                    .build();

            Toast.makeText(context, "Resuming last read", Toast.LENGTH_SHORT).show();
            ThreadActivity.startInstance(context, lastReadInstance);
        } else if (v.equals(gotoLastPost)) {
            ThreadActivity.startInstance(context, topic.getTitle(), topic.getLastPostPath());
        }
    }
}
