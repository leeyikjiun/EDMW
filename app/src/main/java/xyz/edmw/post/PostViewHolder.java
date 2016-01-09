package xyz.edmw.post;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.Message;
import xyz.edmw.R;

public class PostViewHolder {
    @Bind(R.id.post_author)
    TextView author;
    @Bind(R.id.post_timestamp)
    TextView timestamp;
    @Bind(R.id.post_num)
    TextView postNum;
    @Bind(R.id.post_message)
    LinearLayout message;
    @Bind(R.id.post_avatar)
    ImageView authorAvatar;
    @Bind(R.id.post_user_title)
    TextView userTitle;

    private final Context context;

    public PostViewHolder(Context context, View view) {
        this.context = context;
        ButterKnife.bind(this, view);
    }

    public void setPost(Post post) {
        message.removeAllViews();
        author.setText(Html.fromHtml(post.getAuthor()));
        timestamp.setText(Html.fromHtml(post.getTimestamp()));
        postNum.setText(post.getPostNum());
        userTitle.setText(post.getUserTitle());

        Message message = new Message(context, this.message);
        message.setMessage(post.getMessage());

        Ion.with(authorAvatar).load(post.getAuthorAvatar());
    }
}
