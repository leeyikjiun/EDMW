package xyz.edmw.thread;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.R;

public class ThreadViewHolder {
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

    public ThreadViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public void setThread(Thread thread) {
        title.setText(Html.fromHtml(thread.getTitle()));
        startedBy.setText(thread.getStartedBy());
        lastPost.setText(Html.fromHtml(thread.getLastPost()));
        Ion.with(threadstarterAvatar)
                .load(thread.getThreadstarterAvatar());

        if(!thread.getIsSticky()) {
            stickyLabel.setVisibility(View.GONE);
        } else {
            stickyLabel.setVisibility(View.VISIBLE);
        }
    }
}
