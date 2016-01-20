package xyz.edmw.post;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;

import xyz.edmw.R;

public class PostAdapter extends UltimateViewAdapter<PostViewHolder> {
    private final Context context;
    private final List<Post> posts;

    public PostAdapter(Context context, List<Post> posts){
        this.posts = posts;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getAdapterItemCount() {
        return posts.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public PostViewHolder getViewHolder(View view) {
        return new PostViewHolder(context, view, false);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false);
        return new PostViewHolder(context, view, true);
    }

    @Override
    public void onBindViewHolder(PostViewHolder viewHolder, int position) {
        viewHolder.setPost(posts.get(position));
    }

    public List<Post> getPosts() {
        return posts;
    }
}