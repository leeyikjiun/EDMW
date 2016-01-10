package xyz.edmw.thread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import xyz.edmw.R;

public class ThreadAdapter extends BaseAdapter {
    private final Context context;
    private final List<Thread> threads;

    public ThreadAdapter(Context context, List<Thread> threads) {
        this.context = context;
        this.threads = threads;
    }

    @Override
    public int getCount() {
        return threads.size();
    }

    @Override
    public Object getItem(int position) {
        return threads.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ThreadViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_thread, null);
            holder = new ThreadViewHolder(context, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ThreadViewHolder) convertView.getTag();
        }
        holder.setThread(threads.get(position));
        return convertView;
    }
}
