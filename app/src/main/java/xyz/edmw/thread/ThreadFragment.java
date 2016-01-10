package xyz.edmw.thread;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.rest.RestClient;

public class ThreadFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String tag = "ThreadFragment";
    private static final String ARG_FORUM = "arg_forum";
    private static final String ARG_PAGE = "arg_page";
    private OnThreadSelectedListener callback;
    private List<Thread> threads;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static ThreadFragment newInstance(String forum, int page) {
        ThreadFragment fragment = new ThreadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FORUM, forum);
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this);
        loadThreads();
    }

    private void loadThreads() {
        Bundle args = getArguments();
        String forum = args.getString(ARG_FORUM);
        final int page = args.getInt(ARG_PAGE);

        Call<List<Thread>> calls = RestClient.getService().getThreads(forum, page);
        calls.enqueue(new Callback<List<Thread>>() {
            @Override
            public void onResponse(Response<List<Thread>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    threads = response.body();
                    if (page > 1) {
                        for (Iterator<Thread> it = threads.iterator(); it.hasNext(); ) {
                            Thread thread = it.next();
                            if (thread.isSticky()) {
                                it.remove();
                            }
                        }
                    }
                    setListAdapter(new ThreadAdapter(getContext(), threads));
                } else {
                    showErrorMessage();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                showErrorMessage();
                threads = null;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showErrorMessage() {
        Toast.makeText(getContext(), "Failed to load threads", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (callback != null) {
            callback.onThreadSelected(threads.get(position));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnThreadSelectedListener) context;
        } catch (ClassCastException e) {
            Log.e(tag, "Context needs to implement OnThreadSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onRefresh() {
        loadThreads();
    }

    public interface OnThreadSelectedListener {
        void onThreadSelected(Thread thread);

        void onThreadSelected(Thread thread, int pageNum);
    }
}
