package xyz.edmw.post;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import xyz.edmw.thread.ThreadActivity;

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
    private PopupMenu popup;

    public PostViewHolder(Context context, View view, boolean isItem) {
        super(view);
        this.context = context;
        if (isItem) {
            ButterKnife.bind(this, view);
            popup = new PopupMenu(context, view);
        }
    }

    public void setPost(final Post post) {
        message.removeAllViews();
        author.setText(Html.fromHtml(post.getAuthor()));
        timestamp.setText(Html.fromHtml(post.getTimestamp()));
        postNum.setText(post.getPostNum());
        userTitle.setText(post.getUserTitle());

        Message message = new Message(context, this.message);
        message.setMessage(post.getMessage());

        if (MainSharedPreferences.getLoadImageAutomatically()) {
            authorAvatar.setVisibility(View.VISIBLE);
            Ion.with(authorAvatar).load(post.getAuthorAvatar());
        } else {
            authorAvatar.setVisibility(View.GONE);
        }

        postNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_quote:
                                String quote = String.format("[QUOTE=%s;n%s]%s[/QUOTE]", post.getAuthor(), post.getId(), post.getMessage());
                                ((ThreadActivity) context).setQuote(quote);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.post, popup.getMenu());
                popup.show();
            }
        });
    }
}
