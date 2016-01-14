package xyz.edmw.post;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.edmw.R;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder>{
    private final Context context;
    private final List<Post> posts;

    public PostAdapter(Context context, List<Post> posts){
        this.posts = posts;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_post, viewGroup, false);
        PostViewHolder viewHolder = new PostViewHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder viewHolder, int position) {
        viewHolder.setPost(posts.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}