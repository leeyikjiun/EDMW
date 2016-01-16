package xyz.edmw.topic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;

import xyz.edmw.R;

public class TopicAdapter extends UltimateViewAdapter<TopicViewHolder> {
    private final Context context;
    private final List<Topic> topics;

    public TopicAdapter(Context context, List<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @Override
    public TopicViewHolder getViewHolder(View view) {
        return new TopicViewHolder(context, view, false);
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_thread, parent, false);
        return new TopicViewHolder(context, view, true);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        if (!topics.isEmpty()) {
            holder.setTopic(topics.get(position));
        }
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
        return topics.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    public Topic getTopic(int position) {
        return topics.get(position);
    }

    public void insertTopics(List<Topic> topics) {
        int itemStartRange = this.topics.size();

        // loop to find duplicates rather than maintaining a separate set
        // uses more cpu than memory
        for (Topic topic : topics) {
            if (!this.topics.contains(topic)) {
                this.topics.add(topic);
            }
        }
        int itemEndRange = this.topics.size();
        notifyItemRangeInserted(itemStartRange, itemEndRange);
    }
}