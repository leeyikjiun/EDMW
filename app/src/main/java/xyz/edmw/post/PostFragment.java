package xyz.edmw.post;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.edmw.R;
import xyz.edmw.rest.RestClient;


public class PostFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String ARG_PATH = "path";
    private static final String ARG_PAGE = "arg_page";
    private List<Post> posts;

    public static PostFragment newInstance(String forum, int page) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, forum);
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
        loadPosts();
    }

    @Override
    public void onRefresh() {
        loadPosts();
    }

    private void loadPosts() {
        Bundle args = getArguments();
        String path = args.getString(ARG_PATH);
        int page = args.getInt(ARG_PAGE);

        Call<List<Post>> calls = RestClient.getService().getPosts(path, page);
        calls.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    posts = response.body();
                    setListAdapter(new PostAdapter(getContext(), posts));
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                posts = null;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
