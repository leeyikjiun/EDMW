package xyz.edmw.thread;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.lang.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.edmw.MainActivity;
import xyz.edmw.R;
import xyz.edmw.generic.GenericMap;
import xyz.edmw.sharedpreferences.MySharedPreferences;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder>{

    GenericMap<Integer, Thread> threads;

    public ThreadAdapter(GenericMap<Integer, Thread> threads){
        this.threads = threads;
    }
    public static class ThreadViewHolder extends RecyclerView.ViewHolder {
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

        ThreadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    @Override
    public ThreadViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_thread, viewGroup, false);
        ThreadViewHolder view = new ThreadViewHolder(v);
        return view;
    }

    public void onBindViewHolder(ThreadViewHolder threadViewHolder, int position) {
        threadViewHolder.title.setText(threads.getValue(position).getTitle());
        threadViewHolder.startedBy.setText(threads.getValue(position).getStartedBy());
        threadViewHolder.lastPost.setText(Html.fromHtml(threads.getValue(position).getLastPost()));

        if(MySharedPreferences.getLoadImageAutomatically()) {
            threadViewHolder.threadstarterAvatar.setVisibility(View.VISIBLE);
            Ion.with(threadViewHolder.threadstarterAvatar)
                    .load(threads.getValue(position).getThreadstarterAvatar());
        } else {
            threadViewHolder.threadstarterAvatar.setVisibility(View.GONE);
        }

        if(!threads.getValue(position).getIsSticky()) {
            threadViewHolder.stickyLabel.setVisibility(View.GONE);
        } else {
            threadViewHolder.stickyLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}