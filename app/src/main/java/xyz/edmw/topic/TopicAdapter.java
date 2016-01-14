package xyz.edmw.topic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.edmw.R;

public class TopicAdapter extends RecyclerView.Adapter<TopicViewHolder>{
    private final Context context;
    private final List<Topic> topics;

    public TopicAdapter(Context context, List<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_thread, viewGroup, false);
        TopicViewHolder viewHolder = new TopicViewHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TopicViewHolder viewHolder, int position) {
        viewHolder.setTopic(topics.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}