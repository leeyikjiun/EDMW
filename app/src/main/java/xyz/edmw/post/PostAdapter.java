package xyz.edmw.post;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.Message;
import xyz.edmw.R;
import xyz.edmw.generic.GenericMap;
import xyz.edmw.sharedpreferences.MySharedPreferences;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    GenericMap<Integer, Post> posts;
    Context context;

    public PostAdapter(Context context, GenericMap<Integer, Post> posts){
        this.posts = posts;
        this.context = context;
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {
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

        PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_post, viewGroup, false);
        PostViewHolder view = new PostViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(PostViewHolder postViewHolder, int position) {
        postViewHolder.message.removeAllViews();
        postViewHolder.author.setText(Html.fromHtml(posts.getValue(position).getAuthor()));
        postViewHolder.timestamp.setText(Html.fromHtml(posts.getValue(position).getTimestamp()));
        postViewHolder.postNum.setText(posts.getValue(position).getPostNum());
        postViewHolder.userTitle.setText(posts.getValue(position).getUserTitle());

        Message message = new Message(context, postViewHolder.message);
        message.setMessage(posts.getValue(position).getMessage());

        if(MySharedPreferences.getLoadImageAutomatically()) {
            postViewHolder.authorAvatar.setVisibility(View.VISIBLE);
            Ion.with(postViewHolder.authorAvatar).load(posts.getValue(position).getAuthorAvatar());
        } else {
            postViewHolder.authorAvatar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}