package xyz.edmw.post;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.Message;
import xyz.edmw.R;

public class PostViewHolder {
    @Bind(R.id.post_author)
    TextView author;
    @Bind(R.id.post_num)
    TextView postNum;
    @Bind(R.id.post_message)
    LinearLayout message;

    private final Context context;

    public PostViewHolder(Context context, View view) {
        this.context = context;
        ButterKnife.bind(this, view);
    }

    public void setPost(Post post) {
        message.removeAllViews();
        author.setText(Html.fromHtml(post.getAuthor() + " " + post.getTimestamp()));
        postNum.setText(post.getPostNum());
        Message message = new Message(context, this.message);
        message.setMessage(post.getMessage());
    }
}
