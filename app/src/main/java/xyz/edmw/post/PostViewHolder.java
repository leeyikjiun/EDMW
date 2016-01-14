package xyz.edmw.post;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import xyz.edmw.sharedpreferences.MainSharedPreferences;

public class PostViewHolder extends RecyclerView.ViewHolder {
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
        super(view);
        ButterKnife.bind(this, view);
        this.context = context;
    }

    public void setPost(Post post) {
        message.removeAllViews();
        author.setText(Html.fromHtml(post.getAuthor()));
        timestamp.setText(Html.fromHtml(post.getTimestamp()));
        postNum.setText(post.getPostNum());
        userTitle.setText(post.getUserTitle());

        Message message = new Message(context, this.message);
        message.setMessage(post.getMessage());

        if(MainSharedPreferences.getLoadImageAutomatically()) {
            authorAvatar.setVisibility(View.VISIBLE);
            Ion.with(authorAvatar).load(post.getAuthorAvatar());
        } else {
            authorAvatar.setVisibility(View.GONE);
        }
    }
}
