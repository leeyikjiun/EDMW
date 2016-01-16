package xyz.edmw.topic;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;
import xyz.edmw.sharedpreferences.MainSharedPreferences;

public class TopicViewHolder extends UltimateRecyclerviewViewHolder {
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

    public TopicViewHolder(Context context, View view, boolean isItem) {
        super(view);
        this.context = context;

        if (isItem) {
            ButterKnife.bind(this, view);
        }

    }

    public void setTopic(final Topic topic) {
        title.setText(topic.getTitle());
        startedBy.setText(topic.getStartedBy());
        lastPost.setText(Html.fromHtml(topic.getLastPost()));

        if(MainSharedPreferences.getLoadImageAutomatically()) {
            threadstarterAvatar.setVisibility(View.VISIBLE);
            Ion.with(threadstarterAvatar)
                    .load(topic.getThreadstarterAvatar());
        } else {
            threadstarterAvatar.setVisibility(View.GONE);
        }

        if(!topic.isSticky()) {
            stickyLabel.setVisibility(View.GONE);
        } else {
            stickyLabel.setVisibility(View.VISIBLE);
        }

        lastPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((MainActivity) context).onTopicSelected(topic, topic.getNumPages());
            }
        });
    }
}