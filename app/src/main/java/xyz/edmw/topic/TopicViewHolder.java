package xyz.edmw.topic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;
import xyz.edmw.settings.DownloadImage;
import xyz.edmw.settings.MainSharedPreferences;
import xyz.edmw.thread.ThreadActivity;

public class TopicViewHolder extends UltimateRecyclerviewViewHolder {
    @Bind(R.id.card_view)
    CardView cardView;
    @Bind(R.id.thread_title)
    TextView title;
    @Bind(R.id.thread_started_by)
    TextView startedBy;
    @Bind(R.id.thread_last_post)
    TextView lastPost;
    @Bind(R.id.threadstarter_avatar)
    ImageView threadstarterAvatar;
    @Bind(R.id.sticky_label)
    TextView stickyLabel;

    private final Context context;
    private final MainSharedPreferences preferences;

    public TopicViewHolder(Context context, View view, boolean isItem) {
        super(view);
        this.context = context;
        preferences = new MainSharedPreferences(context);

        if (isItem) {
            ButterKnife.bind(this, view);
        }

    }

    public void setTopic(final Topic topic) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadActivity.startInstance(context, topic, 1);
            }
        });
        title.setText(topic.getTitle());
        startedBy.setText(topic.getStartedBy());
        lastPost.setText(Html.fromHtml(topic.getLastPost()));

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        DownloadImage downloadImage = preferences.getDownloadImage();
        switch (downloadImage) {
            case Never:
                threadstarterAvatar.setVisibility(View.GONE);
                break;
            case Wifi:
                if (info == null || info.getType() != ConnectivityManager.TYPE_WIFI) {
                    threadstarterAvatar.setVisibility(View.GONE);
                    break;
                }
            case Always:
                threadstarterAvatar.setVisibility(View.VISIBLE);
                Ion.with(threadstarterAvatar).load(topic.getThreadstarterAvatar());
        }

        if(!topic.isSticky()) {
            stickyLabel.setVisibility(View.GONE);
        } else {
            stickyLabel.setVisibility(View.VISIBLE);
        }

        lastPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadActivity.startInstance(context, topic, topic.getNumPages());
            }
        });
    }
}
